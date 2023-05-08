package onexas.coordinate.service.impl.domain;

/**
 * 
 * @author Dennis Chen
 *
 */

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.directory.api.ldap.codec.controls.search.pagedSearch.PagedResultsDecorator;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.CursorLdapReferralException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchResultDone;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.message.controls.PagedResults;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.EntryCursorImpl;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onexas.coordinate.common.err.BadConfigurationException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.BetterPropertySource;
import onexas.coordinate.common.util.Validations;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.model.DomainUser;
import onexas.coordinate.model.DomainUserFilter;
import onexas.coordinate.service.domain.DomainUserFinder;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LdapDomainUserFinder implements DomainUserFinder {

	private static final Logger logger = LoggerFactory.getLogger(LdapDomainUserFinder.class);

	protected final String domainCode;
	protected final BetterPropertySource config;

	public LdapDomainUserFinder(String domainCode, DomainConfig domainConfig) {
		this.domainCode = domainCode;
		try {
			config = domainConfig.toPropertySource();
		} catch (BadConfigurationException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public ListPage<DomainUser> list(DomainUserFilter filter) {

		/*
		 * we don't know how to let pageIndex and itemTotal work in LDAP search so we
		 * just keep it in one page with total number in result.
		 */

		List<DomainUser> list = new LinkedList<>();

		Integer pageIndex = 0;
		Integer pageTotal = 1;
		Long itemTotal = 0L;
		Integer pageSize = null;
		String criteria = null;

		if (filter != null) {
			pageIndex = filter.getPageIndex();
			pageSize = filter.getPageSize();
			criteria = filter.getCriteria();
		}

		config.checkAllRequired(LdapConstants.CONFIG_BIND_DN, LdapConstants.CONFIG_SERVER,
				LdapConstants.CONFIG_SEARCH_BASE_DN);

		String bindDn = config.getString(LdapConstants.CONFIG_BIND_DN);

		LdapConnection conn = null;
		try {
			try {
				conn = LdapUtils.bindLdapConnection(config, bindDn, LdapUtils.getPassword(config));
			} catch (LdapAuthenticationException e) {// eat
				logger.warn("Can't bind ldap to dn {}", bindDn, e.getMessage());
				new ListPage<DomainUser>(Collections.emptyList());
			}

			String searchFilter;
			if (Strings.isBlank(criteria)) {
				searchFilter = config.getString(LdapConstants.CONFIG_SEARCH_NO_CRITERIA_FILTER,
						LdapConstants.DEFAULT_SEARCH_NO_CRITERIA_FILTER);
			} else {
				// allow 4 condition (hard code)
				searchFilter = Strings.format(
						config.getString(LdapConstants.CONFIG_SEARCH_CRITERIA_FILTER,
								LdapConstants.DEFAULT_SEARCH_CRITERIA_FILTER),
						criteria, criteria, criteria, criteria, criteria, criteria, criteria, criteria, criteria,
						criteria);
			}

			PagedResults pagedSearchControl = new PagedResultsDecorator(conn.getCodecService());
			if (pageSize >= 0) {
				pagedSearchControl.setSize(pageSize);
			} else {
				pagedSearchControl.setSize(Integer.MAX_VALUE);
			}

//			if (cookie != null) {
//				pagedSearchControl.setCookie(cookie);
//			}

			SearchRequest req = new SearchRequestImpl();
			req.setBase(new Dn(config.getString(LdapConstants.CONFIG_SEARCH_BASE_DN)));
			req.setFilter(searchFilter);
			req.setScope(SearchScope.SUBTREE);
			req.addAttributes("*");
			req.addControl(pagedSearchControl);

			EntryCursor cursor = new EntryCursorImpl(conn.search(req));
			while (cursor.next()) {
				try {
					Entry entry = cursor.get();
					DomainUser user = wrap(entry);
					list.add(user);

				} catch (CursorLdapReferralException x) {
					// eat for CursorLdapReferralException
				} catch (LdapException x) {
					logger.warn(x.getMessage(), x);
				}
			}

			SearchResultDone result = cursor.getSearchResultDone();
			if (result != null) {
//				PagedResults rpr = (PagedResults) result.getControl(PagedResults.OID);
//				if (rpr != null) {
//					nextCookie = rpr.getCookie();
//				}
			}
			try {
				cursor.close();
			} catch (IOException e) {
			}
		} catch (CursorException | LdapException x) {
			logger.warn(x.getMessage(), x);
		} finally {
			if (conn != null) {
				try {
					conn.unBind();
				} catch (LdapException e1) {
				}
				try {
					conn.close();
				} catch (IOException e) {
				}
			}
		}
		itemTotal = (long) list.size();
		return new ListPage<DomainUser>(list, pageIndex, pageSize == null ? list.size() : pageSize, pageTotal,
				itemTotal);
	}

	@Override
	public DomainUser get(String identity) {
		DomainUser u = find(identity);
		if (u == null) {
			throw new NotFoundException("user {}#{} not found", identity, domainCode);
		}
		return u;
	}

	@Override
	public DomainUser find(String identity) {

		String userDn = identity;

		config.checkAllRequired(LdapConstants.CONFIG_BIND_DN, LdapConstants.CONFIG_SERVER);

		String bindDn = config.getString(LdapConstants.CONFIG_BIND_DN);

		LdapConnection conn = null;
		try {
			try {
				conn = LdapUtils.bindLdapConnection(config, bindDn, LdapUtils.getPassword(config));
			} catch (LdapAuthenticationException e) {// eat
				logger.warn("Can't bind ldap to dn {}", bindDn, e.getMessage());
				return null;
			}
			try {
				Entry entry = conn.lookup(userDn);
				return wrap(entry);
			} catch (LdapException x) {
				logger.warn(x.getMessage());
				return null;
			}
		} catch (LdapException x) {
			throw new IllegalStateException(x.getMessage(), x);
		} finally {
			if (conn != null) {
				try {
					conn.unBind();
				} catch (LdapException e1) {
				}
				try {
					conn.close();
				} catch (IOException e) {
				}
			}
		}
	}

	protected DomainUser wrap(Entry entry) throws LdapException {
		String id = entry.getDn().toString().toLowerCase();
		String account = null;
		String name = null;
		Attribute attr;
		String userAccountAttribute = config.getString(LdapConstants.CONFIG_USER_ACCOUNT_ATTRIBUTE,
				LdapConstants.DEFAULT_USER_ACCOUNT_ATTRIBUTE);
		if ((attr = entry.get(userAccountAttribute)) != null) {
			account = attr.getString();
		} else {
			// just in case
			throw new LdapException("can't find user's account attribute " + userAccountAttribute);
		}
		String userNameAttribute = config.getString(LdapConstants.CONFIG_USER_DISPLAY_NAME_ATTRIBUTE,
				LdapConstants.DEFAULT_USER_DISPLAY_NAME_ATTRIBUTE);
		if ((attr = entry.get(userNameAttribute)) != null) {
			name = attr.getString();
		}
		if (Strings.isBlank(name)) {
			name = account;
		}

		DomainUser user = new DomainUser().withIdentity(id).withDomain(domainCode).withAccount(account)
				.withDisplayName(name);

		String userEmailAttribute = config.getString(LdapConstants.CONFIG_USER_EMAIL_ATTRIBUTE,
				LdapConstants.DEFAULT_USER_EMAIL_ATTRIBUTE);

		if ((attr = entry.get(userEmailAttribute)) != null) {
			String email = attr.getString();
			if (Validations.isValidEmail(email)) {// just in case
				user.setEmail(email);
			}
		}

		return user;
	}

	@Override
	public DomainUser findByAccount(String account) {

		config.checkAllRequired(LdapConstants.CONFIG_BIND_DN, LdapConstants.CONFIG_SERVER, LdapConstants.CONFIG_SEARCH_BASE_DN);

		String server = config.getString(LdapConstants.CONFIG_SERVER);
		String bindDn = config.getString(LdapConstants.CONFIG_BIND_DN);

		// https://code.google.com/p/jianwikis/wiki/LdapAuthenticationInJava
		LdapConnection conn = null;

		try {
			try {
				conn = LdapUtils.bindLdapConnection(config, bindDn, LdapUtils.getPassword(config));
			} catch (LdapAuthenticationException e) {// eat
				logger.warn("Can't bind ldap to dn {} for user's dn search, {}", bindDn, e.getMessage());
				return null;
			}

			String filter = Strings.format(config.getString(LdapConstants.CONFIG_AUTHENTICATOR_FILTER, LdapConstants.DEFAULT_AUTHENTICATOR_FILTER), account);

			String userDn = searchUserDn(conn, config.getString(LdapConstants.CONFIG_SEARCH_BASE_DN), filter);

			try {
				Entry entry = conn.lookup(userDn);
				return wrap(entry);
			} catch (LdapException x) {
				logger.warn(x.getMessage());
				return null;
			}

		} catch (LdapAuthenticationException e) {// eat
		} catch (Exception e) {
			logger.warn(Strings.format("Exception when connecting to {} : {}", server, e.getMessage()), e);
		} finally {
			try {
				conn.unBind();
			} catch (LdapException e) {
				logger.warn(e.getMessage(), e);
			}
			try {
				conn.close();
			} catch (IOException e) {
				logger.warn(e.getMessage(), e);
			}
		}
		return null;
	}

	private String searchUserDn(LdapConnection conn, String searchBase, String filter) {
		EntryCursor cursor = null;
		try {
			cursor = conn.search(searchBase, filter, SearchScope.SUBTREE);
			try {
				while (cursor.next()) {
					Entry entry = cursor.get();
					String dn = entry.getDn().toString().toLowerCase();
					return dn;
				}
			} catch (CursorException x) {
			} // eat for CursorLdapReferralException
		} catch (LdapException e) {
			logger.warn(Strings.format("error when search user dn, searchBase: {}, filter: {}", searchBase, filter), e);
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
}
