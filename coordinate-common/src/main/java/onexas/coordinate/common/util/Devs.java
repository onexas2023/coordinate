package onexas.coordinate.common.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Streams;

/**
 * 
 * @author Dennis
 *
 */
public class Devs {

	public static Map<String, Object> loadDevTestYaml(Class<?> base) {
		return loadYaml(base, "dev-test.yaml", "dev-test.local.yaml");
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> loadYaml(Class<?> base, String name, String localName) {

		Map<String, Object> configMap;

		try {
			ClassLoader loader = base.getClassLoader();

			URL baseUrl = base.getResource(base.getSimpleName() + ".class");
			Package pkg = base.getPackage();
			int pkgl = pkg.getName().split("\\.").length;
			String basePath = baseUrl.getPath();
			for (int i = 0; i <= pkgl; i++) {
				basePath = basePath.substring(0, basePath.lastIndexOf("/"));
			}

			baseUrl = new URL(baseUrl.getProtocol(), baseUrl.getHost(), baseUrl.getPort(), basePath);
			basePath = baseUrl.toString();

			URL yamlUrl = null;
			Enumeration<URL> yamlUrls = loader.getResources(localName);

			while (yamlUrls.hasMoreElements()) {
				URL l = yamlUrls.nextElement();
				String s = l.toString();
				if (s.startsWith(basePath)) {
					yamlUrl = l;
					break;
				}
			}
			if (yamlUrl == null) {
				yamlUrls = loader.getResources(name);
				while (yamlUrls.hasMoreElements()) {
					URL l = yamlUrls.nextElement();
					String s = l.toString();
					if (s.startsWith(basePath)) {
						yamlUrl = l;
						break;
					}
				}
			}
			if (yamlUrl != null) {
				try (InputStream is = yamlUrl.openStream()) {
					String yaml = Streams.loadString(is);
					configMap = Yamls.objectify(yaml, LinkedHashMap.class);
					return configMap;
				}
			}
			throw new NotFoundException("can't find any dev-test.yaml in {}", baseUrl);

		} catch (Exception x) {
			throw new RuntimeException(x.getMessage(), x);
		}
	}
}
