package onexas.coordinate.common.util;

/**
 * 
 * @author Dennis Chen
 * 
 */
public class I18NLabels {

	// default implement do nothing.
	static private LabelProvider defaultProivder = new LabelProvider() {
		@Override
		public String get(String key) {
			return get(key, key);
		}

		@Override
		public String get(String key, String defaultLabel) {
			return defaultLabel;
		}
	};
	static private final ThreadLocal<LabelProvider> threadLocal = new ThreadLocal<LabelProvider>();

	public static LabelProvider getDefaultProivder() {
		return defaultProivder;
	}

	public static LabelProvider setDefaultProivder(LabelProvider provider) {
		if (provider == null) {
			throw new NullPointerException("set a null default provider");
		}
		LabelProvider old = I18NLabels.defaultProivder;
		I18NLabels.defaultProivder = provider;
		return old;
	}

	public static LabelProvider getThreadlocal() {
		return threadLocal.get();
	}

	public static LabelProvider setThreadLocal(LabelProvider provider) {
		LabelProvider old = threadLocal.get();
		threadLocal.set(provider);
		return old;
	}

	public static LabelProvider getCurrent() {
		if (threadLocal.get() != null) {
			return threadLocal.get();
		}
		return defaultProivder;
	}

	public interface LabelProvider {

		public String get(String key);

		public String get(String key, String defaultLabel);
	}

	public static String get(String key) {
		return getCurrent().get(key);
	}

	public static String get(String key, String defaultLabel) {
		return getCurrent().get(key, defaultLabel);
	}

}
