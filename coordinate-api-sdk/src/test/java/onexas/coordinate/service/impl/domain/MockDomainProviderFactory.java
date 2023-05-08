package onexas.coordinate.service.impl.domain;

import java.util.LinkedList;

import onexas.coordinate.common.err.BadConfigurationException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.err.UnauthenticatedException;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.BetterPropertySource;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.model.DomainUser;
import onexas.coordinate.model.DomainUserFilter;
import onexas.coordinate.model.MapDomainConfig;
import onexas.coordinate.service.domain.DomainAuthenticator;
import onexas.coordinate.service.domain.DomainProvider;
import onexas.coordinate.service.domain.DomainProviderFactory;
import onexas.coordinate.service.domain.DomainUserFinder;

/**
 * 
 * @author Dennis Chen
 *
 */
public class MockDomainProviderFactory implements DomainProviderFactory {
	String identity;
	String account;
	String password;
	String displayName;
	String email;
	String domain;

	public MockDomainProviderFactory(String domain, String identity, String account, String displayName, String email,
			String password) {
		this.identity = identity;
		this.account = account;
		this.displayName = displayName;
		this.email = email;
		this.password = password;
		this.domain = domain;
	}

	public String getProviderCode() {
		return "mock";
	}

	public DomainProvider getProvider(String domainCode, final DomainConfig domainConfig) {
		return new DomainProvider() {

			@Override
			public DomainUserFinder getUserFinder() {
				return new MockDomainUserFinder(domainConfig);
			}

			@Override
			public DomainAuthenticator getAuthenticator() {
				return new MockDomainAuthenticator(domainConfig);
			}
		};
	}

	public DomainConfig getConfigTemplate() {
		MapDomainConfig yaml = new MapDomainConfig();
		yaml.put("identity", identity);
		yaml.put("account", account);
		yaml.put("password", password);
		yaml.put("displayName", displayName);
		yaml.put("domain", domain);
		yaml.put("email", email);
		return yaml;
	}

	private class MockDomainUserFinder implements DomainUserFinder {
		BetterPropertySource conf;

		public MockDomainUserFinder(DomainConfig domainConfig) {
			try {
				conf = domainConfig.toPropertySource();
			} catch (BadConfigurationException e) {
				throw new IllegalStateException(e.getMessage());
			}
		}

		@Override
		public ListPage<DomainUser> list(DomainUserFilter filter) {
			return new ListPage<>(new LinkedList<>());
		}

		@Override
		public DomainUser get(String identity) {
			DomainUser u = find(identity);
			if (u == null) {
				throw new NotFoundException("domain user {} not found", identity);
			}
			return u;
		}

		@Override
		public DomainUser find(String identity) {
			if (identity.equals(conf.getString("identity"))) {
				DomainUser u = new DomainUser();
				u.setIdentity(conf.getString("identity"));
				u.setAccount(conf.getString("account"));
				u.setDomain(conf.getString("domain"));
				u.setDisplayName(conf.getString("displayName"));
				u.setEmail(conf.getString("email"));
				return u;
			}
			return null;
		}

		@Override
		public DomainUser findByAccount(String account) {
			if (account.equals(conf.getString("account"))) {
				DomainUser u = new DomainUser();
				u.setIdentity(conf.getString("identity"));
				u.setAccount(conf.getString("account"));
				u.setDomain(conf.getString("domain"));
				u.setDisplayName(conf.getString("displayName"));
				u.setEmail(conf.getString("email"));
				return u;
			}
			return null;
		}

	}

	private class MockDomainAuthenticator implements DomainAuthenticator {
		BetterPropertySource conf;

		public MockDomainAuthenticator(DomainConfig domainConfig) {
			try {
				conf = domainConfig.toPropertySource();
			} catch (BadConfigurationException e) {
				throw new IllegalStateException(e.getMessage());
			}
		}

		@Override
		public Authentication authenticate(String account, String password) throws UnauthenticatedException {

			if (account.equals(conf.getString("account")) && password.equals(conf.getString("password"))) {
				Authentication auth = new Authentication();
				auth.setIdentity(conf.getString("identity"));
				return auth;
			}
			throw new UnauthenticatedException();
		}

	}
	
	@Override
	public String checkConfig(DomainConfig domainConfig) {
		return "OK";
	}
}
