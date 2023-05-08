package onexas.axes.web.zk.ctrl.admin.domain;

import java.util.Map;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import onexas.axes.web.model.DomainWrap;
import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminDomainApi;
import onexas.coordinate.api.v1.sdk.model.ADomain;
import onexas.coordinate.api.v1.sdk.model.ADomainConfigCheck;
import onexas.coordinate.api.v1.sdk.model.ADomainCreate;
import onexas.coordinate.api.v1.sdk.model.ADomainProviderFactory;
import onexas.coordinate.api.v1.sdk.model.Response;
import onexas.coordinate.common.util.Yamls;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DomainCreatorCtrl extends CtrlBase {

	@Wire
	Textbox vcode;

	@Wire
	Textbox vname;

	@Wire
	Checkbox vdisabled;

	@Wire
	Textbox vdescription;

	@Wire
	Listbox vfactory;
	ListModelList<ADomainProviderFactory> factoryModel;

	@Wire
	Textbox vconfig;

	protected void afterCompose() throws Exception {

		factoryModel = new ListModelList<>();
		CoordinateAdminDomainApi api = new CoordinateAdminDomainApi(workspace.getApiClient());
		for (ADomainProviderFactory f : api.listDomainProviderFactory()) {
			factoryModel.add(f);
		}
		vfactory.setModel(factoryModel);

		mainComp.addEventListener("onSave", (evt) -> {
			doSave();
		});
		mainComp.addEventListener("onSelectFactory", (evt) -> {
			doResetConfig();
		});
		mainComp.addEventListener("onResetConfig", (evt) -> {
			doResetConfig();
		});
		mainComp.addEventListener("onCheckConfig", (evt) -> {
			doCheckConfig();
		});
		mainComp.addEventListener("onCancel", (evt)->{
			doCancel();
		});
	}
	
	private void doCancel() {
		workspace.publish(new DomainEvent(DomainEventType.SELECTED, null));
	}

	private void doResetConfig() {
		ADomainProviderFactory factory = factoryModel.getSingleSelection();
		if (factory != null) {
			vconfig.setValue(factory.getConfigYamlTemplate());
		} else {
			vconfig.setValue("");
		}
	}

	private void doCheckConfig() {
		ADomainProviderFactory factory = factoryModel.getSingleSelection();

		String config = Zks.trimValue(vconfig);

		ComponentValidator validator = new ComponentValidator(mainComp);
		
		validator.validate(factory != null, vfactory, Zks.getLabel("axes.validation.blankSelection"));
		
		try {
			// check
			Yamls.objectify(config, Map.class);
		} catch (Exception x) {
			validator.validate(false, vconfig, Zks.getLabel("axes.validation.configInvalidate"));
		}
		if (!validator.isValid()) {
			return;
		}

		CoordinateAdminDomainApi api = new CoordinateAdminDomainApi(workspace.getApiClient());

		Response r = api.checkDomainConfig(
				new ADomainConfigCheck().provider(factory.getProviderCode()).configYaml(config));

		if (Boolean.TRUE.equals(r.getErr())) {
			Zks.showClientWarning(Zks.getLabel("axes.validation.configInvalidate") + " : " + r.getMsg(), vconfig,
					"bottom center", "top center", 10000, true);
		} else {
			Zks.showClientNotification(r.getMsg(), vconfig, "bottom center", "top center", 3000, true);
		}
	}

	private void doSave() {
		String code = Zks.trimValue(vcode);
		String name = Zks.trimValue(vname);
		boolean disabled = vdisabled.isChecked();
		String description = Zks.trimValue(vdescription);
		String config = Zks.trimValue(vconfig);

		ADomainProviderFactory factory = factoryModel.getSingleSelection();

		ComponentValidator validator = new ComponentValidator(mainComp);
		validator.validate(DomainWrap.class, "code", code, vcode);
		validator.validate(DomainWrap.class, "name", name, vname);
		validator.validate(DomainWrap.class, "description", description, vdescription);
		validator.validate(factory != null, vfactory, Zks.getLabel("axes.validation.blankSelection"));
		if (!validator.isValid()) {
			return;
		}

		try {
			// check
			Yamls.objectify(config, Map.class);
		} catch (Exception x) {
			validator.validate(false, vconfig, Zks.getLabel("axes.validation.configInvalidate"));
		}
		if (!validator.isValid()) {
			return;
		}

		CoordinateAdminDomainApi api = new CoordinateAdminDomainApi(workspace.getApiClient());
		ADomain domain = api.createDomain(new ADomainCreate().code(code).name(name).disabled(disabled)
				.provider(factory.getProviderCode()).description(description).configYaml(config));
		workspace.publish(new DomainEvent(DomainEventType.CREATED, domain));

		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyCreated", domain.getName()));
	}
}
