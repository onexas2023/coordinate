package onexas.axes.web.zk.core;

import javax.servlet.http.HttpSession;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.impl.SimpleDesktopCache;
import org.zkoss.zk.ui.sys.DesktopCache;

import onexas.axes.web.WebConfiguration;

/**
 * A zk destop cache provider that store desktop instance in webapp(servelt context).
 * it works with the {@link WebConfiguration#zkHttpSessionListener()} which remove the stored instance when session timeout.
 * @author Dennis Chen
 *
 */
public class ZkDesktopCacheProvider implements org.zkoss.zk.ui.sys.DesktopCacheProvider {


	public DesktopCache getDesktopCache(Session sess) {
		final WebApp wapp = sess.getWebApp();
		final String key = getCacheKey(sess);

		DesktopCache dc = (DesktopCache) wapp.getAttribute(key);
		if (dc == null) {
			synchronized (this) {
				dc = (DesktopCache) wapp.getAttribute(key);
				if (dc == null) {
					dc = new SimpleDesktopCache(sess.getWebApp().getConfiguration());
					wapp.setAttribute(key, dc);
				}
			}
		}
		return dc;
	}

	private String getCacheKey(Session sess) {
		return WebConfiguration.DESKTOP_CACHE_KEY_PREFIX + ((HttpSession) sess.getNativeSession()).getId();
	}

	public void sessionDestroyed(Session sess) {
		// ignore it
	}

	public void sessionWillPassivate(Session sess) {
		// ignore it
	}

	public void sessionDidActivate(Session sess) {
		// ignore it
	}

	public void start(WebApp wapp) {
		// ignore it
	}

	public void stop(WebApp wapp) {
		// ignore it
	}
}
