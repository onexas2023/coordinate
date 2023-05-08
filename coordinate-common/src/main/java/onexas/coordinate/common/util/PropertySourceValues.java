package onexas.coordinate.common.util;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class PropertySourceValues {
	public static String getSecretFirst(BetterPropertySource config, String valKey, String secretKey)
			throws BadArgumentException {
		String val = getSecretFirstIfAny(config, valKey, secretKey);
		if (Strings.isBlank(val)) {
			throw new BadArgumentException("value of {} or {} is blank", secretKey, valKey);
		}
		return val;
	}

	public static String getSecretFirstIfAny(BetterPropertySource config, String valKey,
			String secretKey) throws BadArgumentException {
		String valSecret = config.getString(secretKey);
		String val = null;
		if (valSecret != null) {
			try {
				SecretProvider sp = AppContext.beanIfAny(SecretProvider.class);
				if(sp!=null) {	
					val = sp.getSecret(valSecret);
				}
			} catch (NotFoundException x) {
				throw new BadArgumentException("value of {} not found", secretKey);
			}
		} else {
			val = config.getString(valKey);
		}
		return val == null ? null : val.trim();
	}
}
