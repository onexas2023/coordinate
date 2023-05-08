package onexas.coordinate.common.app;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onexas.coordinate.common.app.SimpleConfig.ConfigurationProvider;
import onexas.coordinate.common.lang.Classes;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Strings;

/**
 * The nested implementation of configuration
 * 
 * @author Dennis Chen
 * 
 */
public class Configs {
	// to avoid SFL4j complaint
	// Substitute loggers were created during the default configuration phase of
	// the underlying logging system
	// see http://www.slf4j.org/codes.html#substituteLogger
	private static final Logger logger() {
		return LoggerFactory.getLogger(Configs.class);
	}

	public static Config loadXML(URL url, Config parent) throws IOException {
		XMLConfiguration cf;
		try {
			cf = new XMLConfiguration();
			cf.setDelimiterParsingDisabled(true);
			cf.load(url);
			return new SimpleConfig(cf, parent, url);
		} catch (ConfigurationException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	public static Config loadProperties(URL url, Config parent) throws IOException {
		Configuration cf;
		try {
			cf = new PropertiesConfiguration(url);
			return new SimpleConfig(cf, parent, url);
		} catch (ConfigurationException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	public static Config loadInheritedXML(String classResourceName, Config parent) throws IOException {
		Enumeration<URL> configs = Classes.getResourcesByThread(Configs.class.getClassLoader(), classResourceName);

		Map<String, SimpleConfig> allConfig = new HashMap<String, SimpleConfig>();

		while (configs.hasMoreElements()) {
			URL url = configs.nextElement();
			SimpleConfig con = (SimpleConfig) loadXML(url, null);
			String nm = con.getName();
			if (allConfig.containsKey(nm)) {
				SimpleConfig old = allConfig.get(nm);
				String ver = con.getString("[@version]");
				String oldver = old.getString("[@version]");
				if (ver != null && oldver != null && ver.equals(oldver)) {
					logger().warn(Strings.format("loaded duplicated config {} version {} at {} and {}, ignore last one",
							nm, ver, old.getUrl(), con.getUrl()));
					continue;
				}
				throw new IllegalStateException(
						Strings.format("duplicated config {} at {} and {}", nm, old.getUrl(), con.getUrl()));
			}
			allConfig.put(nm, con);
		}
		Set<String> loaded = new HashSet<String>();
		loaded.add("");// the empty dependency

		Config loadedCurr = parent;
		while (loadedCurr != null) {
			String ns = loadedCurr.getName();
			for (String n : ns.split(",")) {
				loaded.add(n.trim());
			}
			loadedCurr = loadedCurr.getParent();
		}

		Config parentCurr = parent;
		while (allConfig.size() > 0) {

			AggregatedConfig aggConf = new AggregatedConfig(null);
			Set<String> temploaded = new HashSet<String>();
			Iterator<Entry<String, SimpleConfig>> iter = allConfig.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, SimpleConfig> entry = iter.next();
				SimpleConfig conf = entry.getValue();
				Set<String> depends = conf.getDepends();
				if (loaded.containsAll(depends)) {// loaded, then add to agg
					aggConf.addConfig(conf);
					temploaded.add(entry.getKey());
					String ver = conf.getString("[@version]");

					logger().info("load config {}:{} at {}", conf.getName(), ver == null ? "" : ver, conf.getUrl());
					iter.remove();
				}
			}
			loaded.addAll(temploaded);
			if (aggConf.size() == 0 && allConfig.size() > 0) {
				// non hit in this run, there must be lost dependency
				List<String> msgs = new LinkedList<String>();

				for (SimpleConfig conf : allConfig.values()) {
					msgs.add(
							Strings.format("{} depends on {} at {}", conf.getName(), conf.getDepends(), conf.getUrl()));
				}
				String msg = Strings.format("can't match dependency, lost or nested, msg : {}", msgs);
				logger().error(msg);
				throw new IllegalStateException("Can't match config dependency, lost or nested");
			} else {
				Config temp;
				if (aggConf.size() == 1) {
					temp = aggConf.getConfig(0);
				} else {
					temp = aggConf;
				}

				if (temp instanceof SimpleConfig) {
					((SimpleConfig) temp).setParent(parentCurr);
				} else if (temp instanceof AggregatedConfig) {
					((AggregatedConfig) temp).setParent(parentCurr);
				}
				parentCurr = temp;
			}
		}
		return parentCurr;
	}

	static final String[] userconfigFolderPorps = new String[] { "user.home" };

	public static final String KEY_USER_CFG_FOLDER = "userCfgFolder";

	public static Config loadUserConfig(String userCfgName, Config parentCfg) throws IOException {

		StringBuilder location = new StringBuilder();

		// load the user's custom config from jar
		Config cfg = loadInheritedXML(userCfgName, parentCfg);

		// then load from outer resource
		File customerCfgFile = null;
		// follow the spring config reading sequence 
		//https://docs.spring.io/spring-boot/docs/1.0.1.RELEASE/reference/html/boot-features-external-config.html
		// ./config/, ./, 
		for (String folder : new String[] { "./config", "." }) {
			customerCfgFile = new File(folder, userCfgName).getCanonicalFile();
			if (customerCfgFile.exists()) {
				break;
			}
			if(location.length()>0) {
				location.append(", ");
			}
			location.append(customerCfgFile.getParent());
		}

		if (!customerCfgFile.exists()) {
			for (String prop : userconfigFolderPorps) {
				if (customerCfgFile == null || !customerCfgFile.exists()) {
					if (System.getProperty(prop) != null) {
						customerCfgFile = new File(System.getProperty(prop), userCfgName).getCanonicalFile();
						if (customerCfgFile.exists()) {
							break;
						}
						location.append(", ").append(customerCfgFile.getParent());
					}
				}
			}
		}

		if (customerCfgFile != null && customerCfgFile.exists()) {
			Set<String> loaded = Collections.asSet(customerCfgFile.getAbsolutePath());

			cfg = loadUserConfigWithImport(new CachedConfig(cfg), customerCfgFile, loaded);

			((SimpleConfig) cfg).setProperty(KEY_USER_CFG_FOLDER, customerCfgFile.getParentFile().getAbsolutePath());
			logger().info("load user config from {}", customerCfgFile);
		} else if (cfg == parentCfg) {
			logger().info("can't find any {}. you could put it in one of following folder {}", userCfgName,
					location.toString());
		}
		return cfg;
	}

	private static Config loadUserConfigWithImport(Config parentCfg, File userCfgFile, Set<String> imported)
			throws IOException {
		FreshFileConfigurationProvider cfgProvider = new FreshFileConfigurationProvider(userCfgFile);

		AggregatedConfig impAggCfg = null;
		String[] imports = cfgProvider.get().getStringArray("import");
		if (imports != null && imports.length > 0) {

			File customerCfgFolder = userCfgFile.getParentFile();

			impAggCfg = new AggregatedConfig(parentCfg);
			for (String imp : imports) {
				File impFile = new File(customerCfgFolder, imp);
				if (imported.contains(impFile.getAbsolutePath())) {
					// ignore it
					continue;
				}
				if (!impFile.exists() || !impFile.isFile()) {
					// try absolute file directly
					File directFile = new File(imp);
					if (!directFile.exists() || !directFile.isFile()) {
						throw new IOException(Strings.format("config file {} doesn't not exist ", impFile));
					}
					impFile = directFile;
				}

				logger().info("load user config (imported) {}", impFile.getAbsolutePath());
				imported.add(impFile.getAbsolutePath());

				Config cfg = loadUserConfigWithImport(null, impFile, imported);
				// in aggregate cfg, first has high priority,
				// to make last has high priority in import, we use insert;
				impAggCfg.insertConfig(cfg);
			}
		}

		if (impAggCfg == null || impAggCfg.size() == 0) {
			return new SimpleConfig(cfgProvider, parentCfg, userCfgFile.toURI().toURL());
		} else {
			return new SimpleConfig(cfgProvider, impAggCfg, userCfgFile.toURI().toURL());
		}
	}

	static class FreshFileConfigurationProvider implements ConfigurationProvider {

		File cfgFile;
		volatile long lastModified = 0;

		Configuration cfg;

		public FreshFileConfigurationProvider(File cfgFile) {
			this.cfgFile = cfgFile;
		}

		@Override
		public Configuration get() {
			if (cfg == null || lastModified < cfgFile.lastModified()) {
				synchronized (this) {
					long lm = cfgFile.lastModified();
					if (cfg == null || lm > lastModified) {
						XMLConfiguration xmlcf;
						try {
							xmlcf = new XMLConfiguration();
							xmlcf.setDelimiterParsingDisabled(true);
							xmlcf.load(cfgFile);
							// log info if it is refreshed (not init)
							if (lastModified > 0) {
								logger().info("refresh config {} {}", cfgFile.getAbsoluteFile(), lastModified);
							}
							cfg = xmlcf;
						} catch (ConfigurationException | RuntimeException e) {
							// throw exception directly when initial loading.
							if (cfg == null) {
								throw new IllegalStateException(e.getMessage(), e);
							} else {
								logger().error(e.getMessage(), e);
							}
						}
						lastModified = lm;
					}
				}
			}
			return cfg;
		}

		@Override
		public long lastModified() {
			return cfgFile.lastModified();
		}

	}
}
