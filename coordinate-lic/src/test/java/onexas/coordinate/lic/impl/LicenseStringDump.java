package onexas.coordinate.lic.impl;

import org.junit.Test;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LicenseStringDump extends onexas.coordinate.lic.LicenseStringDump {

	@Test
	public void dumpLicenseDefaultLicenseValidator() {
		printCode("MSG_DURATION_NOT_MATCH", DefaultLicenseValidator.MSG_DURATION_NOT_MATCH);
		printCode("MSG_IP_NOT_MATCH", DefaultLicenseValidator.MSG_IP_NOT_MATCH);
		printCode("MSG_NO_HOST_MAC", DefaultLicenseValidator.MSG_NO_HOST_MAC);
		printCode("MSG_MAC_NOT_MATCH", DefaultLicenseValidator.MSG_MAC_NOT_MATCH);
	}

	@Test
	public void dumpDefaultLicenseLoader() {
		printCode("DIGEST", DefaultLicenseLoader.DIGEST);
		printCode("MSG_LICENSE_STR", DefaultLicenseLoader.MSG_LICENSE_STR);
		printCode("MSG_MESH", DefaultLicenseLoader.MSG_MESH);
		printCode("MSG_MESH_MD5", DefaultLicenseLoader.MSG_MESH_MD5);
		printCode("MSG_LICENSE_DIGEST", DefaultLicenseLoader.MSG_LICENSE_DIGEST);
		printCode("MSG_LICENSE_MD5", DefaultLicenseLoader.MSG_LICENSE_MD5);
	}
}
