package onexas.axes.web.zk.ctrl;

import java.util.Locale;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import onexas.api.invoker.ApiException;
import onexas.axes.web.Constants;
import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAuthApi;
import onexas.coordinate.api.v1.sdk.model.Authentication;
import onexas.coordinate.api.v1.sdk.model.AuthenticationRequest;
import onexas.coordinate.api.v1.sdk.model.UDomain;
import onexas.coordinate.api.v1.sdk.model.UMetainfo;
import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LoginCtrl extends CtrlBase {

	@Wire
	Image vlogo;
	
	@Wire
	Textbox vaccountBox;

	@Wire
	Textbox vpasswordBox;

	@Wire
	Listbox vdomainBox;
	ListModelList<UDomain> domainModel;

	@Wire
	Listbox vlanguageBox;
	ListModelList<Locale> languageModel;

	String goback;

	protected void afterCompose() throws Exception {

		goback = Executions.getCurrent().getParameter(Constants.REQ_PARAM_GO_BACK);

		if (Strings.isBlank(goback)) {
			goback = workspace.getMainPage();
		} else if (goback.equals(workspace.getLoginPage())) {
			goback = workspace.getMainPage();
		}

		Authentication auth = workspace.getAuthentication();
		if (auth != null) {
			CoordinateAuthApi authApi = new CoordinateAuthApi(workspace.getApiClient());
			try {
				auth = authApi.authenticate(new AuthenticationRequest().token(auth.getToken()));
				workspace.setAuthentication(auth);
				mainComp.getChildren().clear();
				Executions.sendRedirect(goback);
				return;
			} catch (ApiException x) {
				if (x.getCode() == 401 || x.getCode() == 403) {
					// just continue;
				} else {
					Zks.showClientWarning(x.getResponseBody());
				}
			}
		}
		
		vlogo.setSrc(AppContext.config().getString("axes.logoUrl", "/res/axes/img/logo.png"));

		// domain
		UMetainfo Metainfo = workspace.getMetainfo();

		String preferedDomain = workspace.getPreferredDomain();
		UDomain selectedDomain = null;
		for (UDomain d : Metainfo.getDomains()) {
			if (d.getCode().equals(preferedDomain)) {
				selectedDomain = d;
			}
		}
		domainModel = new ListModelList<UDomain>(Metainfo.getDomains());
		domainModel.addToSelection(selectedDomain == null && domainModel.size()>0 ? domainModel.get(0) : selectedDomain);
		vdomainBox.setModel(domainModel);

		// language
		languageModel = new ListModelList<Locale>(workspace.getSupportedLocales());
		languageModel.addToSelection(workspace.getPreferredLocale());
		vlanguageBox.setModel(languageModel);

		mainComp.addEventListener("onChangeLanguage", (evt) -> {
			doChangeLanguage();
		});
		mainComp.addEventListener("onLogin", (evt) -> {
			doLogin();
		});
	}

	private void doLogin() {
		String account = Zks.trimValue(vaccountBox);
		String password = Zks.trimValue(vpasswordBox);

		UDomain domain = domainModel.getSingleSelection();
		
		ComponentValidator validator = new ComponentValidator(mainComp);
		
		
		validator.validate(domain!=null, vdomainBox, Zks.getLabel("axes.msg.login.needDomain"));
		if(domain!=null) {
			if (!domain.getName().equals(workspace.getPreferredDomain())) {
				workspace.setPreferredDomain(domain.getCode());
			}
		}

		validator.validate(!Strings.isBlank(account), vaccountBox, Zks.getLabel("axes.msg.login.needAccount"));
		validator.validate(!Strings.isBlank(password), vpasswordBox, Zks.getLabel("axes.msg.login.needPassword"));

		if (!validator.isValid()) {
			return;
		}

		CoordinateAuthApi authApi = new CoordinateAuthApi(workspace.getApiClient());
		try {
			Authentication auth = authApi.authenticate(
					new AuthenticationRequest().account(account).password(password).domain(domain.getCode()));
			workspace.setAuthentication(auth);
			
			if(!workspace.hasPermission(Constants.AXES_PERMISSION_REQUEST)) {
				Zks.showClientWarning(Zks.getLabel("axes.msg.nopermission"));
				workspace.setAuthentication(null);
			}else {
				Executions.sendRedirect(goback);
			}
		} catch (ApiException x) {
			if (x.getCode() == 401 || x.getCode() == 403) {
				Zks.showWrongValue(vpasswordBox, Zks.getLabel("axes.msg.login.incorrectAccountPassword"));
			} else {
				throw x;
			}
		}

	}

	private void doChangeLanguage() {
		Locale locale = languageModel.getSingleSelection();
		if (locale != null) {
			workspace.setPreferredLocale(locale);
			Executions.sendRedirect(null);// just reload current page
		}
	}
}
