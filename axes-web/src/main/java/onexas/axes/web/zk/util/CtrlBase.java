package onexas.axes.web.zk.util;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;
import org.zkoss.zul.impl.HeaderElement;

import onexas.axes.web.Constants;
import onexas.axes.web.Workspace;
import onexas.axes.web.Workspace.SubscribeAlive;
import onexas.axes.web.zk.core.ZkSelectors;
import onexas.coordinate.common.app.AppContext;

/**
 * 
 * @author Dennis Chen
 *
 */
public class CtrlBase implements Composer<Component>, ComposerExt<Component> {

	protected Component mainComp;
	protected Workspace workspace;
	protected SubscribeAlive alive;
	
	protected boolean sortDesc;
	protected String sortBy;
	private HeaderElement lastSortColumn;

	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) throws Exception {
		workspace = AppContext.bean(Workspace.class);
		return compInfo;
	}

	@Override
	public void doBeforeComposeChildren(Component comp) throws Exception {
		comp.setAttribute(Constants.ATTR_MAIN_CTRL, this);
		comp.setAttribute(Constants.ATTR_MAIN_COMP, comp);
		mainComp = comp;
		alive = new SubscribeAlive() {
			@Override
			public boolean alive() {
				return mainComp.getDesktop() != null;
			}
		};
		beforeComposeChildren();
	}

	protected void beforeComposeChildren() throws Exception {
	}

	@Override
	public boolean doCatch(Throwable ex) throws Exception {
		return false;
	}

	@Override
	public void doFinally() throws Exception {
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// bind components
		ZkSelectors.wireComponents(comp, this, true);

		mainComp.addEventListener("onColumnSort", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				HeaderElement column = (HeaderElement) Events.unwrap(event, Event.class).getTarget();

				String sortdir = (String) column.getAttribute("sortdir");
				if (!"desc".equals(sortdir)) {
					sortDesc = true;
					column.setSclass("sortable desc");
					column.setAttribute("sortdir", "desc");
				} else {
					sortDesc = false;
					column.setSclass("sortable asc");
					column.setAttribute("sortdir", "asc");
				}

				if (lastSortColumn != null && lastSortColumn != column) {
					lastSortColumn.setSclass("sortable");
					lastSortColumn.removeAttribute("sortdir");
				}

				lastSortColumn = column;

				sortBy = (String) event.getData();

				doSort();
			}
		});

		afterCompose();
	}

	protected void doSort() {
	}
	protected void afterCompose() throws Exception {
	}

}