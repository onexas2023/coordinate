package onexas.axes.web.zk.core;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.zkoss.lang.Strings;
import org.zkoss.util.resource.Labels;

import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Config;
import onexas.coordinate.common.util.WebUtility;

/**
 * taglib for
 * 
 * @author Dennis Chen
 *
 */
public class TaglibFn {

	public static Config getConfig() {
		return AppContext.config();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getConfigAsList(String key) {
		Config cfg = getConfig();
		return cfg.getStringList(key, Collections.EMPTY_LIST);
	}

	public static boolean getConfigAsBoolean(String key) {
		return getConfig().getBoolean(key, Boolean.FALSE);
	}

	public static String getEscLabel(String key) {
		String label = Labels.getLabel(key, key);
		return Strings.escape(label, Strings.ESCAPE_JAVASCRIPT);
	}

	public static String loadStringResource(final String resource) {
		return WebUtility.loadStringResource(resource);
	}

	public static String loadJsResource(String resource, boolean cache, boolean compress) {
		return WebUtility.loadJsResource(resource, cache, compress);
	}

	public static String loadCssResource(String resource, boolean cache, boolean compress) {
		return WebUtility.loadCssResource(resource, cache, compress);
	}

	public static boolean hasPermission(String target, String action) {
//		Credential credential = Workspaces.getCurrent().getCredential();
//		SecurityService service = Services.getService(SecurityService.class);
//		return service.hasPermission(credential, target, action);
		return true;
	}

	public static boolean hasPermission(String target) {
//		return hasPermission(target, Function.ACTION_ANY);
		return true;
	}

	public static String getLabel(String key) {
		return Labels.getLabel(key, key);
	}

	public static String getLabel(String key, String key2) {
		key = key + key2;
		return Labels.getLabel(key, key);
	}

	public static String getLabel(String key, String key2, String key3) {
		key = key + key2 + key3;
		return Labels.getLabel(key, key);
	}

	public static String getLabelWithArgs(String key, String arg) {
		return Labels.getLabel(key, key, new String[] { arg });
	}

	public static String getLabelWithArgs(String key, String arg1, String arg2) {
		return Labels.getLabel(key, key, new String[] { arg1, arg2 });
	}

	public static String getLabelWithArgs(String key, String arg1, String arg2, String arg3) {
		return Labels.getLabel(key, key, new String[] { arg1, arg2, arg3 });
	}

	public static String getPrefixedLabel(String key) {
		return Zks.getPrefixedLabel(key);
	}

	public static String getLocaleDisplay(Locale locale, Locale preferred) {
		StringBuilder sb = new StringBuilder();
		sb.append(locale.getDisplayName(preferred));
		if (!locale.equals(preferred)) {
			sb.append(" / ");
			sb.append(locale.getDisplayName(locale));
		}
		return sb.toString();
	}

	public static String ellipsis(String str, Integer length) {
		return onexas.coordinate.common.lang.Strings.ellipsis(str, length == null ? 20 : length);
	}
}
