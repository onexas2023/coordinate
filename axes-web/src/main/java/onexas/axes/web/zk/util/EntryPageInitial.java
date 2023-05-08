package onexas.axes.web.zk.util;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.xel.taglib.Taglib;
import org.zkoss.xel.util.TaglibMapper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.util.InitiatorExt;

import onexas.api.invoker.ApiException;
import onexas.axes.web.Constants;
import onexas.axes.web.RequestUriFilter;
import onexas.axes.web.Workspace;
import onexas.axes.web.zk.core.ZulVariableResolver;
import onexas.coordinate.api.v1.sdk.CoordinateAuthApi;
import onexas.coordinate.api.v1.sdk.model.Authentication;
import onexas.coordinate.api.v1.sdk.model.AuthenticationRequest;
import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Config;
import onexas.coordinate.common.app.RefreshableConfigLoader;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class EntryPageInitial implements Initiator, InitiatorExt {

	private static final Logger logger = LoggerFactory.getLogger(EntryPageInitial.class);

	private static RefreshableConfigLoader<List<Taglib>> tlbConfigLoader = new RefreshableConfigLoader<List<Taglib>>() {

		@Override
		protected List<Taglib> load(Config config) {
			List<Taglib> tls = new LinkedList<>();
			Map<String, String> existed = new HashMap<>();
			StringBuilder info = new StringBuilder();
			for (Config subConfig : AppContext.config().getSubConfigList("axes.taglibs.taglib")) {
				String prefix = subConfig.getString("[@prefix]");
				String uri = subConfig.getString("");
				if (existed.containsKey(prefix)) {
					logger.warn("prefix {} has been used by {}, new one {} will be ignored", prefix,
							existed.get(prefix), uri);
					continue;
				}
				tls.add(new Taglib(prefix, uri));
				existed.put(prefix, uri);
				if (info.length() > 0) {
					info.append(", ");
				}
				info.append(Strings.format("{}:'{}'", prefix, uri));
			}
			logger.info("Aexs Taglibs : " + info.toString());
			return Collections.unmodifiableList(tls);
		}
	};

	public void doInit(Page page, Map<String, Object> args) throws Exception {

		boolean authenticated = true;
		Object arg = args.get(Constants.ENTRY_PAGE_INITIAL_PARAM_AUTH);
		if (arg != null) {
			authenticated = !"false".equals(arg.toString());
		}

		if (authenticated) {
			Workspace workspace = AppContext.bean(Workspace.class);
			Authentication auth = workspace.getAuthentication();
			boolean pass = false;
			if (auth != null) {
				CoordinateAuthApi authApi = new CoordinateAuthApi(workspace.getApiClient());
				try {
					// try to auth again since it is keep in session for a unknown time.
					auth = authApi.authenticate(new AuthenticationRequest().token(auth.getToken()));
					workspace.setAuthentication(auth);
					pass = true;
				} catch (ApiException x) {
				}
			}
			if (!pass) {
				Execution exec = Executions.getCurrent();
				String goback = (String) exec.getAttribute(RequestUriFilter.ATTR_REQUEST_URI);
				HttpServletResponse response = (HttpServletResponse)exec.getNativeResponse();
				response.sendRedirect(workspace.getLoginPage() + "?" + Constants.REQ_PARAM_GO_BACK + "=" + URLEncoder.encode(goback, Strings.UTF8.toString()));
				Executions.getCurrent().setVoided(true);
			}
		}

		// variableresolver
		page.addVariableResolver(new ZulVariableResolver());

		// initial common taglib
		List<Taglib> list = tlbConfigLoader.load();
		if (list.size() > 0) {
			TaglibMapper mapper = new TaglibMapper();
			for (Taglib tl : list) {
				mapper.load(tl, WebApps.getCurrent());
			}
			page.addFunctionMapper(mapper);
		}
	}

	@Override
	public void doAfterCompose(Page page, Component[] comps) throws Exception {
		// css link
		String cssLink = Strings.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"{}\"/>",
				Executions.encodeURL("~./axes/css/axes.css.dsp"));
		((PageCtrl) page).addAfterHeadTags(cssLink);

		// javascript
		String javascript = Strings.format(
				"<script type=\"text/javascript\" src=\"{}\"></script>"
						+ "\n<script type=\"text/javascript\" src=\"{}\"></script>",
				Executions.encodeURL("~./axes/js/axes.js.dsp"),
				Executions.encodeURL("~./axes/js/axes.lang.js.dsp"));
		((PageCtrl) page).addAfterHeadTags(javascript);

		// favicon
		String favicon = Strings.format("<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"{}\"/>",
				Executions.encodeURL(getFaviconUri()));
		((PageCtrl) page).addAfterHeadTags(favicon);
	}

	@Override
	public boolean doCatch(Throwable ex) throws Exception {
		return false;
	}

	@Override
	public void doFinally() throws Exception {
	}

	private String getFaviconUri() {
		String uri = AppContext.config().getString("axes.favicon");
		if (Strings.isBlank(uri)) {
			uri = "~./axes.ico";
		}
		return uri;
	}
}
