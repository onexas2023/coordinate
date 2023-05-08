package onexas.coordinate.lic.impl;

import java.io.StringReader;
import java.security.PublicKey;

import org.apache.commons.configuration.PropertiesConfiguration;

import onexas.coordinate.common.lang.Objects;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.ObfuscatedString;
import onexas.coordinate.common.util.Securitys;
import onexas.coordinate.lic.License;
import onexas.coordinate.lic.LicenseLoader;

/**
 * 
 * @author Dennis Chen
 * 
 */
public class DefaultLicenseLoader extends LicenseLoader {

	// DIGEST
	static final String DIGEST = new ObfuscatedString(1966399571695852824L, -6132318958032421250L).toString();
	// licenseStr {}
	static final String MSG_LICENSE_STR = new ObfuscatedString(-6078196156745271225L, 5503401117358861159L,
			-3162957280382707823L).toString();
	// mesh {}
	static final String MSG_MESH = new ObfuscatedString(-6627225011478705442L, 3725678329652750708L).toString();
	// meshMD5 {}
	static final String MSG_MESH_MD5 = new ObfuscatedString(1811409270072183778L, -8613172631667976989L,
			-5138863985497723569L).toString();
	// licenseDigest {}
	static final String MSG_LICENSE_DIGEST = new ObfuscatedString(6841554532128857558L, -758751480220289581L,
			-7621855361015955127L).toString();
	// licenseMD5 {}
	static final String MSG_LICENSE_MD5 = new ObfuscatedString(-2174628666188363098L, 1822191720181455220L,
			-4745182758162391147L).toString();

	private static final byte[] RSA_PUBKEY = new byte[] { 48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1,
			1, 5, 0, 3, -127, -115, 0, 48, -127, -119, 2, -127, -127, 0, -125, -52, 110, 71, -4, 114, 53, -52, -87, 101,
			105, 88, 60, -103, -58, -122, 110, 125, 26, -66, -74, 94, -2, 37, 0, -59, 55, -99, -88, 113, 101, -128,
			-109, 108, 102, -4, 83, -10, -58, -14, -114, 15, 102, -30, -48, 33, 28, -86, 81, 108, 47, -82, 52, 106, 87,
			-84, 74, -59, 39, -89, -95, -106, -87, 118, -33, -116, -48, 108, -45, -93, 93, 24, -85, -127, 26, -28, 46,
			-77, 81, 124, -70, -113, -33, -122, -11, 107, 80, 46, -66, -99, 116, -23, 30, 88, -34, -13, 22, 122, -100,
			104, -53, 92, -24, 70, 96, 48, -67, 67, -1, 113, 69, -112, 73, -72, 112, -102, -2, -90, 51, 99, 26, -16, 14,
			-21, -73, -81, 31, 109, 2, 3, 1, 0, 1 };

	@Override
	public License load(String licenseStr) {
		try {

			PropertiesConfiguration pp = new PropertiesConfiguration();
			pp.setDelimiterParsingDisabled(true);
			pp.setEncoding("UTF8");

			pp.load(new StringReader(licenseStr));

			LoaderLicense license = new LoaderLicense();

			String licenseDigestStr = pp.getString(DIGEST); // the mesh's digest is what we need

			if (licenseDigestStr == null) {
				return null;
			}

			license.load(pp);

			String mesh = license.getMesh();
			byte[] md5 = Securitys.md5(mesh);
			byte[] licenseDigest = Strings.toByteArray(licenseDigestStr);
			PublicKey pubKey = Securitys.decodePublicKey("RSA", RSA_PUBKEY);
			byte[] licenseMD5 = Securitys.decrypt(licenseDigest, pubKey);

			if (License.logger.isDebugEnabled()) {
				License.logger.debug(MSG_LICENSE_STR, licenseStr);
				License.logger.debug(MSG_MESH, mesh);
				License.logger.debug(MSG_MESH_MD5, Strings.toHexString(md5));
				License.logger.debug(MSG_LICENSE_DIGEST, Strings.toHexString(licenseDigest));
				License.logger.debug(MSG_LICENSE_MD5, Strings.toHexString(licenseMD5));
			}

			if (!Objects.equals(md5, licenseMD5)) {
				return null;
			}
			return license;

		} catch (Throwable e) {
			License.logger.error(e.getClass() + ":" + e.getMessage());
			// eat when loading
			return null;
		}
	}

}
