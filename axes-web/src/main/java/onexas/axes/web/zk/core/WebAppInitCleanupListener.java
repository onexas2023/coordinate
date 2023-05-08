package onexas.axes.web.zk.core;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppCleanup;
import org.zkoss.zk.ui.util.WebAppInit;

import onexas.coordinate.common.app.AppContext;

/**
 * 
 * @author Dennis Chen
 *
 */
public class WebAppInitCleanupListener implements WebAppInit, WebAppCleanup {

	private ZkWebAppInitCleanupListener l;

	public void init(WebApp wapp) throws Exception {
		l = AppContext.getBean(ZkWebAppInitCleanupListener.class);
		l.init(wapp);
	}

	public void cleanup(WebApp wapp) throws Exception {
		if (l != null) {
			l.cleanup(wapp);
		}
	}
}