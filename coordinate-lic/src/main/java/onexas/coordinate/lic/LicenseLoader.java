package onexas.coordinate.lic;

import onexas.coordinate.lic.impl.DefaultLicenseLoader;

/**
 * 
 * @author Dennis Chen
 *
 */
public abstract class LicenseLoader {

	private static LicenseLoader instance;

	public synchronized static LicenseLoader getInstance() {
		if (instance == null) {
			instance = new DefaultLicenseLoader();
		}
		return instance;
	}

	public abstract License load(String licenseStr);
}
