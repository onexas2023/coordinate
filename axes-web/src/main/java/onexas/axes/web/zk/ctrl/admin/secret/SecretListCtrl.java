package onexas.axes.web.zk.ctrl.admin.secret;

import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import onexas.axes.web.model.SecretWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminSecretApi;
import onexas.coordinate.api.v1.sdk.model.ASecret;
import onexas.coordinate.api.v1.sdk.model.ASecretFilter;
import onexas.coordinate.api.v1.sdk.model.ASecretListPage;
import onexas.coordinate.api.v1.sdk.model.UDomain;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SecretListCtrl extends CtrlBase {

	@Wire
	Textbox vkeyword;

	@Wire
	Listbox vsecrets;
	ListModelList<SecretWrap> secretsModel;

	@Wire
	Paging vpaging;

	ASecret selectedSecret;

	int pageSize;
	int pageIndex;
	long totalSize;

	Map<String, UDomain> domainMap = new LinkedHashMap<>();

	protected void afterCompose() throws Exception {

		for (UDomain domain : workspace.getMetainfo().getDomains()) {
			domainMap.put(domain.getCode(), domain);
		}

		pageSize = workspace.getPreferedPageSize();

		secretsModel = new ListModelList<SecretWrap>();
		vsecrets.setModel(secretsModel);

		mainComp.addEventListener("onSearch", (evt) -> {
			doSearch();
		});
		mainComp.addEventListener("onSelect", (evt) -> {
			doSelect();
		});
		mainComp.addEventListener("onPaging", (evt) -> {
			doPaging();
		});

		doSearch();

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});
	}

	@Override
	protected void doSort() {
		doSearch();
	}

	private void doSearch() {

		String keyword = Zks.trimValue(vkeyword);
		// domain
		CoordinateAdminSecretApi api = new CoordinateAdminSecretApi(workspace.getApiClient());
		ASecretFilter filter = new ASecretFilter().strContaining(true).strIgnoreCase(true).code(keyword)
				.pageSize(pageSize).pageIndex(pageIndex);

		if (sortBy != null) {
			filter.sortField(sortBy).sortDesc(sortDesc);
		}

		ASecretListPage page = api.listSecret(filter);
		if (pageIndex > 0 && pageIndex > page.getPageTotal() - 1) {
			pageIndex = page.getPageTotal() > 0 ? page.getPageTotal() - 1 : 0;
			page = api.listSecret(filter.pageIndex(pageIndex));
		}

		pageIndex = page.getPageIndex();
		totalSize = page.getItemTotal();

		secretsModel.clear();
		secretsModel.clearSelection();
		for (ASecret secret : page.getItems()) {
			secretsModel.add(new SecretWrap(secret));
		}
		if (selectedSecret != null) {
			secretsModel.addToSelection(new SecretWrap(selectedSecret));
		}

		refreshPaging();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof SecretEvent) {
			ASecret secret = ((SecretEvent) evt).getSecret();
			switch (((SecretEvent) evt).getType()) {
			case CREATED:
				selectedSecret = secret;
				doSearch();
				break;
			case DELETED:
				if (selectedSecret != null && selectedSecret.getUid().equals(secret.getUid())) {
					selectedSecret = null;
				}
				doSearch();
				break;
			case UPDATED:
				if (selectedSecret != null && selectedSecret.getUid().equals(secret.getUid())) {
					selectedSecret = secret;
				}
				doSearch();
				break;
			case START_TO_CREATE:
				selectedSecret = null;
				secretsModel.clearSelection();
				break;
			default:
			}
		}
	}

	private void refreshPaging() {
		vpaging.setActivePage(pageIndex);
		vpaging.setPageSize(pageSize);
		vpaging.setTotalSize((int) totalSize);
	}

	private void doSelect() {
		SecretWrap wrap = secretsModel.getSingleSelection();
		selectedSecret = wrap == null ? null : wrap.delegatee;
		workspace.publish(new SecretEvent(SecretEventType.SELECTED, selectedSecret));
	}

	private void doPaging() {
		pageIndex = vpaging.getActivePage();
		doSearch();
	}

	public String getDomainName(String code) {
		return domainMap.containsKey(code) ? domainMap.get(code).getName() : code;
	}
}
