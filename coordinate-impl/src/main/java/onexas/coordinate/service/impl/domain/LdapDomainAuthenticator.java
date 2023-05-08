package onexas.coordinate.service.impl.domain;

import java.io.IOException;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onexas.coordinate.common.err.BadConfigurationException;
import onexas.coordinate.common.err.UnauthenticatedException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.BetterPropertySource;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.service.domain.DomainAuthenticator;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LdapDomainAuthenticator implements DomainAuthenticator {

	private static final Logger logger = LoggerFactory.getLogger(LdapDomainAuthenticator.class);

	protected final String domainCode;
	protected final BetterPropertySource config;

	public LdapDomainAuthenticator(String domainCode, DomainConfig domainConfig) {
		this.domainCode = domainCode;
		try {
			config = domainConfig.toPropertySource();
		} catch (BadConfigurationException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	


	@Override
	public Authentication authenticate(String account, String password) throws UnauthenticatedException {

		config.checkAllRequired(LdapConstants.CONFIG_BIND_DN, LdapConstants.CONFIG_SERVER, LdapConstants.CONFIG_SEARCH_BASE_DN);

		String server = config.getString(LdapConstants.CONFIG_SERVER);
		String bindDn = config.getString(LdapConstants.CONFIG_BIND_DN);
		
		String searchBaseDN = config.getString(LdapConstants.CONFIG_SEARCH_BASE_DN);
		String authenticatorFilter = config.getString(LdapConstants.CONFIG_AUTHENTICATOR_FILTER, LdapConstants.DEFAULT_AUTHENTICATOR_FILTER);

		// https://code.google.com/p/jianwikis/wiki/LdapAuthenticationInJava
		LdapConnection conn = null;

		try {
			try {
				conn = LdapUtils.bindLdapConnection(config, bindDn, LdapUtils.getPassword(config));
			} catch (LdapAuthenticationException e) {// eat
				logger.warn("Can't bind ldap to dn {} for user's dn search, {}", bindDn, e.getMessage());
				throw new UnauthenticatedException("can't bind ldap with base dn, the password might be wrong");
			}

			String filter = Strings.format(authenticatorFilter, account);

			String userDN = searchUserDN(conn, searchBaseDN, filter);

			if (!Strings.isBlank(userDN)) {
				conn.unBind();
				conn.bind(userDN, password);
				if (conn.isAuthenticated()) {
					Authentication auth = new Authentication();
					auth.setIdentity(userDN);
					return auth;
				}
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
		throw new UnauthenticatedException("wrong account or password");
	}

	private String searchUserDN(LdapConnection conn, String searchBase, String filter) {
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
