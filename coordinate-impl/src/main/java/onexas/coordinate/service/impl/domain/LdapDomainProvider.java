package onexas.coordinate.service.impl.domain;

import java.io.IOException;

import org.apache.directory.api.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;

import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.BadConfigurationException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.BetterPropertySource;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.service.domain.DomainAuthenticator;
import onexas.coordinate.service.domain.DomainProvider;
import onexas.coordinate.service.domain.DomainUserFinder;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LdapDomainProvider implements DomainProvider {
	String domainCode;
	DomainConfig domainConfig;

	public LdapDomainProvider(String domainCode, DomainConfig domainConfig) {
		this.domainCode = domainCode;
		this.domainConfig = domainConfig;
	}

	@Override
	public DomainAuthenticator getAuthenticator() {
		return new LdapDomainAuthenticator(domainCode, domainConfig);
	}

	@Override
	public DomainUserFinder getUserFinder() {
		return new LdapDomainUserFinder(domainCode, domainConfig);
	}

	public static String checkConfig(DomainConfig domainConfig) throws BadArgumentException {
		try {
			BetterPropertySource config = domainConfig.toPropertySource();
			config.checkAllRequired(LdapConstants.CONFIG_BIND_DN, LdapConstants.CONFIG_SERVER,  LdapConstants.CONFIG_SEARCH_BASE_DN);
			config.checkAnyRequired(LdapConstants.CONFIG_PASSWORD_SECRET, LdapConstants.CONFIG_PASSWORD);

			String server = config.getString(LdapConstants.CONFIG_SERVER);
			String bindDn = config.getString(LdapConstants.CONFIG_BIND_DN);
			LdapConnection conn = null;
			try {
				try {
					conn = LdapUtils.bindLdapConnection(config, bindDn, LdapUtils.getPassword(config));
				} catch (LdapAuthenticationException e) {// eat
					throw new BadArgumentException("Can't bind ldap to dn {}, the password might be wrong", bindDn, e.getMessage());
				}
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
			return Strings.format("Bind to {} passed", server);
		} catch (BadConfigurationException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (LdapException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

}
