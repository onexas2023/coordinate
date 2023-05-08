package onexas.axes.web.zk.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Locale;
import java.util.Map;

import org.zkoss.util.resource.LabelLocator;

import onexas.coordinate.common.app.Config;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 * 
 */
public class ConfigResourceLabeLocator implements LabelLocator {

	private static final String PATH_KEY_ATTR = "[@key]";

	private Map<String, Config> bundleCfgMap;
	private Config bundleCfg;

	public ConfigResourceLabeLocator(Map<String, Config> bundleCfgMap) {
		this.bundleCfgMap = bundleCfgMap;
	}

	public ConfigResourceLabeLocator(Config bundleCfg) {
		this.bundleCfg = bundleCfg;

	}

	public URL locate(Locale locale) throws Exception {

		String locStr = locale == null ? "" : locale.toString();

		Config cfg;
		String host;
		String path = Strings.cat("/", "".equals(locStr) ? "." : locStr);
		if (bundleCfg != null) {
			cfg = bundleCfg;
			host = "single";
		} else {
			cfg = bundleCfgMap.get(locStr);
			host = "map";
		}

		return cfg == null ? null : new URL("zs-cfg-label", host, -1, path, new InnerURLStreamHandler(cfg));
	}

	private class InnerURLStreamHandler extends URLStreamHandler {
		Config cfg;

		public InnerURLStreamHandler(Config cfg) {
			this.cfg = cfg;
		}

		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			return new InnerURLConnection(u, cfg);
		}
	}

	private class InnerURLConnection extends URLConnection {
		Config cfg;

		public InnerURLConnection(URL url, Config cfg) {
			super(url);
			this.cfg = cfg;
		}

		@Override
		public void connect() throws IOException {
			// nothing
		}

		@Override
		public InputStream getInputStream() throws IOException {
			StringBuilder sb = new StringBuilder();
			for (Config sub : cfg.getSubConfigList("entry", PATH_KEY_ATTR, false)) {
				String key = sub.getString(PATH_KEY_ATTR);
				String value = sub.getString("");
				sb.append(key).append("=").append(value).append("\n");
			}
			return new ByteArrayInputStream(sb.toString().getBytes(Strings.UTF8));
		}

	}

}