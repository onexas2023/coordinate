package onexas.axes.web.zk.core;

import java.net.URL;
import java.util.Locale;

import org.zkoss.util.resource.LabelLocator;

import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class ClassResourceLabeLocator implements LabelLocator {

//	private static Logger logger = LoggerFactory.getLogger(ClassResourceLabeLocator.class);

	private String path;
	@SuppressWarnings("rawtypes")
	private Class loader;
	private static String postFix = ".labels";

	public ClassResourceLabeLocator(String path) {
		this(path, ClassResourceLabeLocator.class);
	}

	public ClassResourceLabeLocator(String path, @SuppressWarnings("rawtypes") Class loader) {
		this.path = path;
		this.loader = loader;
	}

	public URL locate(Locale locale) throws Exception {
		URL url = null;
		if (locale == null) {
			url = loader.getResource(path + postFix);
		} else {
			String lan = locale.getLanguage();
			String ctry = locale.getCountry();
			// zk load lan_ctry then lan
			if (!Strings.isEmpty(lan) && !Strings.isEmpty(ctry)) {
				url = loader.getResource(Strings.cat(path, "_", lan, "_", ctry, postFix));
			} else if (!Strings.isEmpty(lan)) {
				url = loader.getResource(Strings.cat(path, "_", lan, postFix));
			}
		}
//		if (url != null) {
//			logger.info("load label resource for locale {} at {}", locale, url);
//		}
		return url;
	}

}