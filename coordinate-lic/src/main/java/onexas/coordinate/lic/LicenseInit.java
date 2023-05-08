package onexas.coordinate.lic;

import java.io.File;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Config;
import onexas.coordinate.common.app.Configs;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Files;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.Networks;
import onexas.coordinate.common.util.ObfuscatedString;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(LicenseInit.BEAN_NAME)
@DependsOn(AppContext.BEAN_NAME)
public class LicenseInit {

	public static final String BEAN_NAME = Env.NS_BEAN + "LicenseInit";

	// can't find user config folder to load license
	static final String MSG_CANT_FIND_CFG_FOLDER = new ObfuscatedString(4322320074208079648L, -4583933694701568786L,
			2091262325077386393L, -1357678644444656738L, -9022352348570727231L, 2386615553569411058L,
			4920699286763303166L).toString();
	// License file {} is not found
	static final String MSG_LIC_FILE_NOT_FOUND = new ObfuscatedString(-6515164947367972427L, -5418927872840543510L,
			-6354831679049040280L, -5164493135383963368L, -6088895355946459024L).toString();
	// License file {} is not valid, it was modified or corrupted.
	static final String MSG_LIC_FILE_NOT_VALID = new ObfuscatedString(-5894108898756329832L, -2930274731278164935L,
			-7817545809432604081L, -3136235946767067616L, 6729114570332183466L, 4213429300115024231L,
			5374074256727703331L, -4487402973398583409L, -7085132400444004508L).toString();
	// License '{}' of '{}' loaded, time available from {} to {}. location {}
	static final String MSG_LIC_FILE_LOAD_OK = new ObfuscatedString(2285515655345183257L, 1180903679980100462L,
			-7064501469594695298L, -5489635616378478082L, -1502396481548504685L, 5490766502888891436L,
			526648169740690919L, 2310980446241721803L, -1319405636358753541L, -8836644762867232153L).toString();
	// Can't load license at {}
	static final String MSG_LIC_FILE_LOAD_ERROR = new ObfuscatedString(-2182340942588719417L, 5862022710090901641L,
			105496624017873769L, 82522842676908525L).toString();

	@PostConstruct
	public void postConstruct() {

		if (LicenseHolder.hasLicense()) {
			return;
		}

		Config cfg = AppContext.config();
		boolean initLicense = cfg.getBoolean("app.initLicense", Boolean.TRUE);
		if (!initLicense) {
			return;
		}

		String licenseUrl = System.getProperty("coordinateLicenseUrl");

		if (!Strings.isBlank(licenseUrl)) {
			try {
				String licenseStr = Networks.loadString(new URL(licenseUrl), "UTF8");
				License lic = LicenseLoader.getInstance().load(licenseStr);
				if (lic == null) {
					License.logger.warn(MSG_LIC_FILE_NOT_VALID, licenseUrl);
				} else {
					LicenseHolder.setLicense(lic);
					License.logger.info(MSG_LIC_FILE_LOAD_OK, lic.getSubject(), lic.getOwner(), lic.getDateFrom(),
							lic.getDateTo(), licenseUrl);
				}
			} catch (Throwable x) {
				License.logger.warn(MSG_LIC_FILE_LOAD_ERROR, licenseUrl);
			}

			return;
		}

		String licenseFile = cfg.getString("app.licenseFile");
		if (Strings.isBlank(licenseFile)) {
			String folder = cfg.getString(Configs.KEY_USER_CFG_FOLDER);
			if (folder == null) {
				License.logger.warn(MSG_CANT_FIND_CFG_FOLDER);
				return;
			}

			String licenseName = cfg.getString("app.licenseName");
			if (Strings.isBlank(licenseName)) {
				licenseName = "coordinate.license";
			}
			File licFile = new File(folder, licenseName);
			licenseFile = licFile.getAbsolutePath();
		}

		File licFile = new File(licenseFile);

		// try local
		String filename = licFile.getName();
		String main = Files.getMain(filename);
		String ext = Files.getExtension(filename);
		File localLicFile = new File(licFile.getParent(), main + ".local." + ext);
		if (localLicFile.exists() && localLicFile.isFile()) {
			licFile = localLicFile;
		}
		

		if (!licFile.exists() || !licFile.isFile()) {
			License.logger.warn(MSG_LIC_FILE_NOT_FOUND, licFile);
		} else {
			try {
				String licenseStr = Files.loadString(licFile, "UTF8");
				License lic = LicenseLoader.getInstance().load(licenseStr);
				if (lic == null) {
					License.logger.warn(MSG_LIC_FILE_NOT_VALID, licFile);
				} else {
					LicenseHolder.setLicense(lic);
					License.logger.info(MSG_LIC_FILE_LOAD_OK, lic.getSubject(), lic.getOwner(), lic.getDateFrom(),
							lic.getDateTo(), licFile);
				}
			} catch (Throwable x) {
				License.logger.warn(MSG_LIC_FILE_LOAD_ERROR, licFile);
			}
		}

	}

}
