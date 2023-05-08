package onexas.coordinate.lic;

import java.util.List;

import onexas.coordinate.common.util.ObfuscatedString;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LicenseHolder {

	// N/A
	static final String NA = new ObfuscatedString(-4859971739808747112L, 7482311597401425363L).toString();
	// No license
	static final String NO_LICENSE = new ObfuscatedString(4476964382479834866L, -8372560142572061646L,
			8607272278950593646L).toString();

	static private License license;
	static private NoLicense noLicense = new NoLicense();

	public static synchronized License getLicense() {
		return license == null ? noLicense : license;
	}

	public static synchronized License findValidLicense() {
		return license == null ? null : LicenseValidator.getInstance().isValid(license) ? license : null;
	}

	public static synchronized boolean hasLicense() {
		return license != null;
	}

	public static synchronized void setLicense(License license) {
		LicenseHolder.license = license;
	}

	static class NoLicense implements License {

		@Override
		public String getSerialNumber() {
			return NA;
		}

		@Override
		public String getDateFrom() {
			return null;
		}

		@Override
		public String getDateTo() {
			return null;
		}

		@Override
		public String getIssuer() {
			return NA;
		}

		@Override
		public String getOwner() {
			return NA;
		}

		@Override
		public String getSubject() {
			return NA;
		}

		@Override
		public List<String> getAllowedIps() {
			return null;
		}

		@Override
		public List<String> getAllowedMacs() {
			return null;
		}

		@Override
		public boolean hasFeature(String featureName) {
			return false;
		}

		@Override
		public Feature getFeature(String featureName) {
			return null;
		}

		@Override
		public String getDescription() {
			return NO_LICENSE;
		}

		@Override
		public String getShortDescription() {
			return NO_LICENSE;
		}

		@Override
		public boolean isUnlimitedDateFrom() {
			return false;
		}

		@Override
		public boolean isUnlimitedDateTo() {
			return false;
		}

	}
}
