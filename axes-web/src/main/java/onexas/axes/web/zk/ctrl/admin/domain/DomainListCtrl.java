package onexas.axes.web.zk.ctrl.admin.domain;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import onexas.axes.web.model.DomainWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminDomainApi;
import onexas.coordinate.api.v1.sdk.model.ADomain;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DomainListCtrl extends CtrlBase {

	@Wire
	Textbox vkeyword;

	@Wire
	Listbox vdomains;
	ListModelList<DomainWrap> domainsModel;

	ADomain selectedDomain;

	protected void afterCompose() throws Exception {

		domainsModel = new ListModelList<DomainWrap>();
		vdomains.setModel(domainsModel);

		mainComp.addEventListener("onSearch", (evt) -> {
			doSearch();
		});
		mainComp.addEventListener("onSelect", (evt) -> {
			doSelect();
		});

		doSearch();

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});
	}

	private void doSearch() {

		String keyword = Zks.trimValue(vkeyword).toUpperCase();
		CoordinateAdminDomainApi api = new CoordinateAdminDomainApi(workspace.getApiClient());
		List<ADomain> items = api.listDomain();

		domainsModel.clear();
		domainsModel.clearSelection();

		List<DomainWrap> l = new LinkedList<DomainWrap>();
		for (ADomain domain : items) {
			if (domain.getCode().toUpperCase().contains(keyword) || domain.getName().toUpperCase().contains(keyword)) {
				l.add(new DomainWrap(domain));
			}
		}

		if (sortBy != null) {
			Collections.sort(l, (e1, e2) -> {
				DomainWrap t;
				if (sortDesc) {
					t = e1;
					e1 = e2;
					e2 = t;
				}
				switch (sortBy) {
				case "code":
					return e1.getCode().compareTo(e2.getCode());
				case "name":
					return e1.getName().compareTo(e2.getName());
				}
				throw new IllegalArgumentException("unsupported " + sortBy);
			});
		}
		domainsModel.addAll(l);

		if (selectedDomain != null) {
			domainsModel.addToSelection(new DomainWrap(selectedDomain));
		}
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof DomainEvent) {
			ADomain domain = ((DomainEvent) evt).getDomain();
			switch (((DomainEvent) evt).getType()) {
			case CREATED:
				selectedDomain = domain;
				doSearch();
				break;
			case DELETED:
				if (selectedDomain != null && selectedDomain.getCode().equals(domain.getCode())) {
					selectedDomain = null;
				}
				doSearch();
				break;
			case UPDATED:
				if (selectedDomain != null && selectedDomain.getCode().equals(domain.getCode())) {
					selectedDomain = domain;
				}
				doSearch();
				break;
			case START_TO_CREATE:
				selectedDomain = null;
				domainsModel.clearSelection();
				break;
			default:
			}
		}
	}

	@Override
	protected void doSort() {
		doSearch();
	}

	private void doSelect() {
		DomainWrap wrap = domainsModel.getSingleSelection();
		selectedDomain = wrap == null ? null : wrap.delegatee;
		workspace.publish(new DomainEvent(DomainEventType.SELECTED, selectedDomain));
	}
}
