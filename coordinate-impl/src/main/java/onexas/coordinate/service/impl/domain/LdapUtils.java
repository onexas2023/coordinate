package onexas.coordinate.service.impl.domain;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.BetterPropertySource;
import onexas.coordinate.service.SecretService;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LdapUtils {

	public static String getPassword(BetterPropertySource config) throws BadArgumentException {
		String passwordSecret = config.getString(LdapConstants.CONFIG_PASSWORD_SECRET);
		String password;
		if (passwordSecret != null) {
			try {
				password = AppContext.bean(SecretService.class).getSecret(passwordSecret);
				password = Strings.trim(password);
			} catch (NotFoundException x) {
				throw new BadArgumentException("{} not found", LdapConstants.CONFIG_PASSWORD_SECRET);
			}
		} else {
			password = config.getString(LdapConstants.CONFIG_PASSWORD);
		}
		if(Strings.isBlank(password)) {
			throw new BadArgumentException("{} or {} is blank", LdapConstants.CONFIG_PASSWORD_SECRET, LdapConstants.CONFIG_PASSWORD);
		}
		return password;
	}

	public static LdapConnection bindLdapConnection(BetterPropertySource config, String bindDn, String password)
			throws LdapException {
		LdapConnection conn = null;

		String server = config.getString(LdapConstants.CONFIG_SERVER);
		Integer port = config.getInteger(LdapConstants.CONFIG_PORT, LdapConstants.DEFAULT_PORT);
		Boolean ssl = config.getBoolean(LdapConstants.CONFIG_SSL, LdapConstants.DEFAULT_SSL);

		conn = new LdapNetworkConnection(server, port, ssl);
		conn.bind(bindDn, password);
		return conn;

	}
}
