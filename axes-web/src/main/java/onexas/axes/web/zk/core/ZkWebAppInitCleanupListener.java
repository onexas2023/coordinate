package onexas.axes.web.zk.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.lang.Library;
import org.zkoss.util.resource.Labels;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.sys.Attributes;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.WebAppCleanup;
import org.zkoss.zk.ui.util.WebAppInit;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Config;
import onexas.coordinate.common.lang.Classes;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.I18NLabels;
import onexas.coordinate.common.util.WebUtility;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component
public class ZkWebAppInitCleanupListener implements WebAppInit, WebAppCleanup {

	private static final Logger logger = LoggerFactory.getLogger(WebAppInitCleanupListener.class);

	public void init(WebApp wapp) throws Exception {
		initAppConfig(wapp);
	}

	private void initAppConfig(WebApp wapp) throws Exception {

		Config cfg = AppContext.config();
//
		initExtendlet(wapp, cfg);

		initI18NLabels(wapp, cfg);

		initCssJs(wapp, cfg);

		initListener(wapp, cfg);

		initMisc(wapp, cfg);
	}

	private void initCssJs(WebApp wapp, Config cfg) {
		if (cfg.getBoolean("axes.javascripts.[@startup]", Boolean.TRUE)) {
			for (String page : cfg.getStringList("axes.javascripts.javascript", Collections.<String>emptyList())) {
				WebUtility.loadJsResource(page, cfg.getBoolean("axes.javascripts.[@cache]", Boolean.TRUE),
						cfg.getBoolean("axes.javascripts.[@compress]", Boolean.TRUE));
			}
		}
		if (cfg.getBoolean("axes.csss.[@startup]", Boolean.TRUE)) {
			for (String page : cfg.getStringList("axes.csss.css", Collections.<String>emptyList())) {
				WebUtility.loadCssResource(page, cfg.getBoolean("axes.csss.[@cache]", Boolean.TRUE),
						cfg.getBoolean("axes.csss.[@compress]", Boolean.TRUE));
			}
		}
	}

	private void initExtendlet(WebApp wapp, Config cfg) {

		// add view extendslet for the zul in jar
		List<String> resourceFolder = cfg.getBoolean("debug", Boolean.FALSE)
				? (List<String>) cfg.getStringList("debug.resourceFolder", null)
				: null;
		if (resourceFolder != null && resourceFolder.size() > 0) {
			logger.warn("You should't use following resourceFolder in production env {}", resourceFolder);
		}
		Servlets.addExtendletContext(wapp.getServletContext(), "@",
				new ClassResourceExtendletContext("/view", wapp.getServletContext(), resourceFolder));

//		// how ui customization in local server folder
//		String uiResourceFolder = cfg.getString("axes.webResourceFolder");
//		Extendlet staticRes = new StaticClassResourceExtendlet("/web", uiResourceFolder);
//		for (String ext : cfg.getStringList("axes.staticImages.extension",
//				onexas.coordinate.common.lang.Collections
//						.asList(new String[] { "png", "jpg", "gif", "ico", "svg" }))) {
//			WebManager.getWebManager(wapp.getServletContext()).getClassWebResource().addExtendlet(ext, staticRes);
//		}

//		WebUtility.addResourceFolders(resourceFolder);
	}

	private void initI18NLabels(WebApp wapp, Config cfg) {
		// set to prevent duplicate
		Set<String> temp = new LinkedHashSet<String>(cfg.getStringList("axes.labels.label",
				onexas.coordinate.common.lang.Collections.asList("axes")));
		// add i18n resource locator
		for (String label : temp) {
			String labelLoc = Strings.format("/view/{}", label);
			logger.info("Register system label {}", labelLoc);
			Labels.register(new ClassResourceLabeLocator(labelLoc));
		}
		temp = new LinkedHashSet<String>(
				cfg.getStringList("axes.labels.userLabel", java.util.Collections.<String>emptyList()));
		// user label has high priority then system
		for (String label : temp) {
			logger.info("Register user label {}", label);
			Labels.register(new FileResourceLabeLocator(label));
		}
		temp = new LinkedHashSet<String>(
				cfg.getStringList("axes.labels.universalLabel", java.util.Collections.<String>emptyList()));
		for (String label : temp) {
			logger.info("Register universal label {}", label);
			Labels.register(new FileResourceLabeLocator(label, true));
		}

		Map<String, Config> bundleCfgMap = null;
		for (Config subCfg : cfg.getSubConfigList("axes.labels.bundle", "[@locale]", true)) {
			String name = subCfg.getString("[@locale]");
			if (name == null) {
				name = "";
			}
			if (bundleCfgMap == null) {
				bundleCfgMap = new HashMap<>();
			}
			bundleCfgMap.put(name, subCfg);
		}
		if (bundleCfgMap != null) {
			logger.info("Register bundle label");
			Labels.register(new ConfigResourceLabeLocator(bundleCfgMap));
		}

		Config universalBundleCfg = cfg.getSubConfig("axes.labels.universalBundle");
		if (universalBundleCfg != null) {
			logger.info("Register universal bundle label");
			Labels.register(new ConfigResourceLabeLocator(universalBundleCfg));
		}

		// always use us to get the application name
		Locale old = org.zkoss.util.Locales.setThreadLocal(Locale.US);
		wapp.setAppName(Labels.getLabel("axes.productShortName"));
		org.zkoss.util.Locales.setThreadLocal(old);

		// override the default label provider to use zk's i18n message
		I18NLabels.setDefaultProivder(new I18NLabels.LabelProvider() {
			@Override
			public String get(String key, String defaultLabel) {
				return Labels.getLabel(key, defaultLabel);
			}

			@Override
			public String get(String key) {
				return get(key, key);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void initListener(WebApp wapp, Config cfg) {
		List<String> listeners = cfg.getStringList("zk.appListener");
		if (listeners == null || listeners.size() == 0) {
			return;
		}
		List<Class<WebAppCleanup>> cleanUpList = new LinkedList<Class<WebAppCleanup>>();
		for (String l : listeners) {
			try {
				@SuppressWarnings("rawtypes")
				Class clz = Classes.forNameByThread(l);
				if (WebAppInit.class.isAssignableFrom(clz)) {
					WebAppInit init = (WebAppInit) clz.newInstance();
					init.init(wapp);
				}
				if (WebAppCleanup.class.isAssignableFrom(clz)) {
					cleanUpList.add(clz);
				}
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
		if (cleanUpList.size() > 0) {
			wapp.setAttribute("axes.appCleanupList", cleanUpList);
		}
	}

	private void initMisc(WebApp wapp, Config cfg) {
		org.zkoss.zk.ui.util.Configuration webAppConfig = wapp.getConfiguration();

		Integer v = cfg.getInteger("zk.maxDesktopsPerSession");
		if (v != null) {
			webAppConfig.setSessionMaxDesktops(v.intValue());
		}
		v = cfg.getInteger("zk.maxRequestsPerSession");
		if (v != null) {
			webAppConfig.setSessionMaxRequests(v.intValue());
		}

		// id gen for loading test
		if (Boolean.TRUE.equals(cfg.getBoolean("axes.testing.sequenceId[@enabled]"))) {
			((WebAppCtrl) wapp).setIdGenerator(new SequenceIdGenerator());
			// don't recycle id when testing
			Library.setProperty(Attributes.UUID_RECYCLE_DISABLED, "true");
		}
	}

	public void cleanup(WebApp wapp) throws Exception {
		delegateWebAppCleanup(wapp);
	}

	private void delegateWebAppCleanup(WebApp webApp) {
		@SuppressWarnings("unchecked")
		List<Class<WebAppCleanup>> cleanUpList = (List<Class<WebAppCleanup>>) webApp
				.getAttribute("axes.appCleanupList");
		if (cleanUpList == null) {
			return;
		}
		for (Class<WebAppCleanup> clz : cleanUpList) {
			try {
				WebAppCleanup cleanup = (WebAppCleanup) clz.newInstance();
				cleanup.cleanup(webApp);
			} catch (Exception e) {
				throw new IllegalStateException();
			}
		}
	}

}