package onexas.axes.web.zk.ctrl.admin.domain;

import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import onexas.axes.web.Constants;
import onexas.axes.web.model.DomainWrap;
import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Messageboxes;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminDomainApi;
import onexas.coordinate.api.v1.sdk.model.ADomain;
import onexas.coordinate.api.v1.sdk.model.ADomainConfigCheck;
import onexas.coordinate.api.v1.sdk.model.ADomainProviderFactory;
import onexas.coordinate.api.v1.sdk.model.ADomainUpdate;
import onexas.coordinate.api.v1.sdk.model.Response;
import onexas.coordinate.common.util.Yamls;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DomainInfoEditorCtrl extends CtrlBase {

	@Wire
	Label vcode;

	@Wire
	Textbox vname;

	@Wire
	Checkbox vdisabled;

	@Wire
	Textbox vdescription;

	@Wire
	Label vfactory;
	ListModelList<ADomainProviderFactory> factoryModel;

	@Wire
	Textbox vconfig;

	ADomain selectedDomain;

	protected void afterCompose() throws Exception {

		selectedDomain = (ADomain) Zks.getScopeArg(mainComp, Constants.ARG_EDITING_OBJ);

		factoryModel = new ListModelList<>();
		CoordinateAdminDomainApi api = new CoordinateAdminDomainApi(workspace.getApiClient());
		for (ADomainProviderFactory f : api.listDomainProviderFactory()) {
			factoryModel.add(f);
		}

		mainComp.addEventListener("onSave", (evt) -> {
			doSave();
		});
		mainComp.addEventListener("onDelete", (evt) -> {
			doDelete();
		});
		mainComp.addEventListener("onResetConfig", (evt) -> {
			doResetConfig();
		});
		mainComp.addEventListener("onCheckConfig", (evt) -> {
			doCheckConfig();
		});

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

		refreshEditor();
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

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof DomainEvent) {
			ADomain domain = ((DomainEvent) evt).getDomain();
			switch (((DomainEvent) evt).getType()) {
			case CREATED:
			case UPDATED:
			case SELECTED:
				selectedDomain = domain;
				refreshEditor();
				break;
			case DELETED:
				selectedDomain = null;
				refreshEditor();
				break;
			default:
			}
		}
	}

	private void refreshEditor() {

		if (selectedDomain != null) {
			vcode.setValue(selectedDomain.getCode());
			vname.setValue(selectedDomain.getName());
			vdisabled.setChecked(Boolean.TRUE.equals(selectedDomain.getDisabled()));
			vdescription.setValue(selectedDomain.getDescription());
			factoryModel.clearSelection();
			vfactory.setValue("");
			
			for (ADomainProviderFactory f : factoryModel.getInnerList()) {
				if (f.getProviderCode().equals(selectedDomain.getProvider())) {
					factoryModel.addToSelection(f);
					vfactory.setValue(f.getProviderCode());
					break;
				}
			}

			CoordinateAdminDomainApi api = new CoordinateAdminDomainApi(workspace.getApiClient());

			String yaml = api.getDomainConfigYaml(selectedDomain.getCode());
			vconfig.setValue(yaml);
		} else {
			vcode.setValue("");
			vname.setValue("");
			vdescription.setValue("");
			factoryModel.clearSelection();
			vfactory.setValue("");
			vconfig.setValue("");
			vdisabled.setChecked(false);
		}
	}

	private void doSave() {
		if (selectedDomain == null) {
			return;
		}
		String name = Zks.trimValue(vname);
		String description = Zks.trimValue(vdescription);
		String config = Zks.trimValue(vconfig);
		boolean disabled = vdisabled.isChecked();

		ADomainProviderFactory factory = factoryModel.getSingleSelection();

		ComponentValidator validator = new ComponentValidator(mainComp);
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
		ADomain domain = api.updateDomain(selectedDomain.getCode(), new ADomainUpdate().name(name)
				.disabled(disabled).description(description).configYaml(config));
		workspace.publish(new DomainEvent(DomainEventType.UPDATED, domain));
		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyUpdated", domain.getName()));
	}

	private void doDelete() {
		if (selectedDomain == null) {
			return;
		}
		final ADomain domain = selectedDomain;
		Messageboxes.showConfirmationBox(null,
				Zks.getLabelWithArg("axes.msg.confirmDelete", selectedDomain.getName()), (evt) -> {
					Zks.showBusyThenRun(() -> {
						CoordinateAdminDomainApi api = new CoordinateAdminDomainApi(workspace.getApiClient());
						api.deleteDomain(domain.getCode(), false);
						workspace.publish(new DomainEvent(DomainEventType.DELETED, domain));
						Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyDeleted", domain.getName()));
					});
				}, null);
	}
}
