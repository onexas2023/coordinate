package onexas.axes.web.zk.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;

import onexas.axes.web.PageEntry;
import onexas.axes.web.PageEntryRegistory;
import onexas.axes.web.Workspace;
import onexas.axes.web.util.Menus.Menu;
import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class MenuBuilder {

	private static final Logger logger = LoggerFactory.getLogger(MenuBuilder.class);

	Menubar menubar;
	PageEntryRegistory pageEntryRegistory;
	Workspace workspace;

	public MenuBuilder(Menubar menubar) {
		this.menubar = menubar;
	}

	public void build(Menu menu) {
		workspace = AppContext.bean(Workspace.class);
		pageEntryRegistory = AppContext.bean(PageEntryRegistory.class);
		menubar.getChildren().clear();
		if (menu == null) {
			return;
		}
		List<Menu> children = menu.getChildren();
		if (children != null) {
			String group = null;
			for (Menu m : children) {

				String permissionRequest = m.getPermission();
				if (!Strings.isBlank(permissionRequest) && !workspace.hasPermission(permissionRequest)) {
					continue;
				}

				String action = m.getAction();
				if (!Strings.isBlank(action) && action.startsWith("page:")) {
					String page = action.substring(5);
					PageEntry entry = pageEntryRegistory.get(page);
					permissionRequest = entry.getPermissionRequest();
					if (!Strings.isBlank(permissionRequest) && !workspace.hasPermission(permissionRequest)) {
						continue;
					}
				}

				if (group != null && !group.equals(m.getGroup())) {
					menubar.appendChild(new Menuseparator());
				}
				group = m.getGroup();
				if (m.getChildren() == null || m.getChildren().size() == 0) {
					org.zkoss.zul.Menuitem child = new org.zkoss.zul.Menuitem(buildLabel(m));
					menubar.appendChild(child);
					buildAction(child, m);
					
					if (Strings.isBlank(m.getAction())) {
						// disable since it doesn't has child and action
						child.setDisabled(true);
					}
				} else {
					org.zkoss.zul.Menu child = new org.zkoss.zul.Menu(buildLabel(m));
					menubar.appendChild(child);
					buildAction(child, m);
					
					boolean anyChild = false;
					anyChild |= buildChildren(workspace, child, m);
					if (!anyChild && Strings.isBlank(m.getAction())) {
						// disable since it doesn't has child and action
						child.setDisabled(true);
					}
				}
			}
		}
	}

	private String buildLabel(Menu m) {
		String label = m.getLabel();
		if(Strings.isBlank(label)) {
			return "";
		}
		if(label.startsWith("#{") && label.endsWith("}")) {
			label = label.substring(2,label.length()-1);
			switch(label) {
			case "displayName":
				if(workspace.getAuthentication()!=null) {
					return workspace.getAuthentication().getDisplayName();
				}
				break;
			}
			return label;
		}
		return Zks.getPrefixedLabel(label);
	}

	private boolean buildChildren(Workspace workspace, org.zkoss.zul.Menu zmenu, Menu menu) {
		List<Menu> children = menu.getChildren();
		if (children != null && children.size() > 0) {
			Menupopup popup = new Menupopup();
			popup.setSclass("axes-menu");

			String group = null;
			boolean anyChild = false;
			for (Menu m : children) {

				String permissionRequest = m.getPermission();
				if (!Strings.isBlank(permissionRequest) && !workspace.hasPermission(permissionRequest)) {
					continue;
				}
				String action = m.getAction();
				if (!Strings.isBlank(action) && action.startsWith("page:")) {
					String page = action.substring(5);
					PageEntry entry = pageEntryRegistory.get(page);
					permissionRequest = entry.getPermissionRequest();
					if (!Strings.isBlank(permissionRequest) && !workspace.hasPermission(permissionRequest)) {
						continue;
					}
				}

				if (group != null && !group.equals(m.getGroup())) {
					popup.appendChild(new Menuseparator());
				}
				group = m.getGroup();
				if (m.getChildren() == null || m.getChildren().size() == 0) {
					org.zkoss.zul.Menuitem child = new org.zkoss.zul.Menuitem(Zks.getPrefixedLabel(m.getLabel()));
					popup.appendChild(child);
					buildAction(child, m);

					anyChild = true;
				} else {
					org.zkoss.zul.Menu child = new org.zkoss.zul.Menu(Zks.getPrefixedLabel(m.getLabel()));
					popup.appendChild(child);
					buildAction(child, m);
					anyChild |= buildChildren(workspace, child, m);
				}

			}
			if (anyChild) {
				zmenu.appendChild(popup);
			} else if (Strings.isBlank(menu.getAction())) {
				// disable since it doesn't has child and action
				zmenu.setDisabled(true);
			}
			return anyChild;
		}
		return false;
	}

	private void buildAction(org.zkoss.zul.Menu menu, Menu m) {
		final String action = m.getAction();
		if (!Strings.isBlank(action)) {
			menu.addEventListener(Events.ON_CLICK, (evt) -> {
				handleAction(action);
			});

		}
	}

	private void buildAction(org.zkoss.zul.Menuitem item, Menu m) {
		String action = m.getAction();
		if (!Strings.isBlank(action)) {
			if (action.startsWith("href:")) {
				final String href = action.substring(5);
				item.setHref(href);
			} else {
				item.addEventListener(Events.ON_CLICK, (evt) -> {
					handleAction(action);
				});
			}
		} else {
			logger.warn("menu {} has no action", m.getName());
		}
	}

	private void handleAction(String action) {
		if (action.startsWith("href:")) {
			String href = action.substring(5);
			Executions.sendRedirect(href);
		} else if (action.startsWith("page:")) {
			String page = action.substring(5);
			workspace.changePage(page);
		} else {
			logger.warn("unsupported action {}", action);
			Zks.showClientWarning("Unsupported action");
		}
	}
}
