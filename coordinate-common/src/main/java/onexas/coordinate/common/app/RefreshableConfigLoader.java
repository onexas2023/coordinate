package onexas.coordinate.common.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author dennis
 * 
 */
public abstract class RefreshableConfigLoader<T> {

	private static final Logger logger = LoggerFactory.getLogger(RefreshableConfigLoader.class);

	private volatile long lastModified;

	private T lastLoaded;

	protected Config getConfig() {
		return AppContext.instance().getConfig();
	}

	public T load() {
		long now = System.currentTimeMillis();

		//don't check cfg.lastModified too quick
		if (lastLoaded != null && (now - lastModified) < 10000) {
			return lastLoaded;
		}

		Config cfg = getConfig();
		long lm = cfg.lastModified();
		if (lastLoaded == null || lm > lastModified) {
			synchronized (this) {
				if (lastLoaded == null || lm > lastModified) {
					try {
						lastLoaded = load(cfg);
					} catch (RuntimeException x) {
						// throw exception directly when initial loading.
						if (lastLoaded == null) {
							throw x;
						} else {
							logger.error(x.getMessage(), x);
						}
					}
					lastModified = lm;
				}
			}
		}
		return lastLoaded;
	}

	abstract protected T load(Config cfg);
}
