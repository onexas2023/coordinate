package onexas.coordinate.lic;

import onexas.coordinate.lic.impl.DefaultLicenseValidator;

/**
 * 
 * @author Dennis Chen
 *
 */
public abstract class LicenseValidator {
	private static LicenseValidator instance;

	public synchronized static LicenseValidator getInstance() {
		if (instance == null) {
			instance = new DefaultLicenseValidator();
		}
		return instance;
	}

	public abstract boolean isValid(License lic);
}
