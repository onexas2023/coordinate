package onexas.axes.web.zk.ctrl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Vlayout;

import com.google.common.base.Objects;

import onexas.axes.web.Constants;
import onexas.axes.web.PageEntry;
import onexas.axes.web.PageEntryRegistory;
import onexas.axes.web.Workspace;
import onexas.axes.web.Workspace.OnChangeArgsEvent;
import onexas.axes.web.Workspace.OnChangePageEvent;
import onexas.axes.web.Workspace.OnSubscribeTimeoutEvent;
import onexas.axes.web.Workspace.OnTimeoutEvent;
import onexas.axes.web.Workspace.OnUnsubscribeTimeoutEvent;
import onexas.axes.web.Workspace.SubscribeAlive;
import onexas.axes.web.util.Menus;
import onexas.axes.web.util.Menus.Menu;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.MenuBuilder;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Config;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.ValueII;

/**
 * 
 * @author Dennis Chen
 *
 */
public class AxesCtrl extends CtrlBase {

	@Wire
	Component vtimerbox;
	@Wire
	Menuitem vhomemenu;
	@Wire
	Menubar vmainmenubar;
	@Wire
	Menubar vmiscmenubar;
	@Wire
	Vlayout vbody;
	@Wire
	Include vbodyInc;
	@Wire
	Div vlicense;

	String page;
	String[] args;

	private Map<Integer, ValueII<Timer, Map<EventListener<Event>, SubscribeAlive>>> timers = new HashMap<Integer, ValueII<Timer, Map<EventListener<Event>, SubscribeAlive>>>();

	protected void afterCompose() throws Exception {

		if (!workspace.hasPermission(Constants.AXES_PERMISSION_REQUEST)) {
			mainComp.detach();
			Clients.evalJavaScript("alert('" + Zks.getLabel("axes.msg.nopermission").replace("'", "\"") + "')");
			Zks.redirect("/logout", null);
			return;
		}

		Config config = AppContext.config();
		String iconUrl = config.getString("axes.homeMenu.iconUrl", "~./axes/img/logo-menu.png");
		if(!Strings.isBlank(iconUrl)) {			
			vhomemenu.setImage(iconUrl);
			vhomemenu.setSclass("axes-home");
		}else {
			vhomemenu.setIconSclass("fas fa-home axes-home");
		}
		vhomemenu.setHref(config.getString("axes.homeMenu.hrefUrl", "/"));
		String bgurl = config.getString("axes.backgroundUrl");
		if (bgurl != null) {
			vbody.setStyle(vbody.getStyle() + ";background-image:url('" + bgurl + "');");
		}

		EventQueues.lookup(Constants.EVENT_QUEUE_INTERNAL).subscribe((evt) -> {
			doWorkspaceEvent(evt);
		});

		Menu main = Menus.getMenu(Menus.MENU_MAIN);
		new MenuBuilder(vmainmenubar).build(main);
		Menu misc = Menus.getMenu(Menus.MENU_MISC);
		new MenuBuilder(vmiscmenubar).build(misc);

		page = (String) Executions.getCurrent().getAttribute(Constants.REQ_PATH_PAGE);
		args = (String[]) Executions.getCurrent().getAttribute(Constants.REQ_PATH_PAGE_ARGS);
		if (args == null) {
			args = new String[0];
		}

		if (!Strings.isBlank(page)) {
			AppContext.bean(Workspace.class).changePage(page, args);
		}

	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof OnChangePageEvent) {
			doChangePage(((OnChangePageEvent) evt).getPageName(), ((OnChangePageEvent) evt).getArgs());
		} else if (evt instanceof OnChangeArgsEvent) {
			doChangeArgs(((OnChangeArgsEvent) evt).isRefresh(), ((OnChangeArgsEvent) evt).getArgs());
		} else if (evt instanceof OnSubscribeTimeoutEvent) {
			doSubscribeTimeout(((OnSubscribeTimeoutEvent) evt).getTimeout(),
					((OnSubscribeTimeoutEvent) evt).getListener(), ((OnSubscribeTimeoutEvent) evt).getAlive());
		} else if (evt instanceof OnUnsubscribeTimeoutEvent) {
			doUnsubscribeTimeout(((OnUnsubscribeTimeoutEvent) evt).getTimeout(),
					((OnUnsubscribeTimeoutEvent) evt).getListener());
		}
	}

	private void doUnsubscribeTimeout(int timeout, EventListener<Event> listener) {
		ValueII<Timer, Map<EventListener<Event>, SubscribeAlive>> pair1 = timers.get(timeout);
		if (pair1 != null) {
			Map<EventListener<Event>, SubscribeAlive> pair2 = pair1.getValue2();
			if (pair2.containsKey(listener)) {
				pair2.remove(listener);
			}
			if (pair2.size() == 0) {
				timers.remove(timeout);

				pair1.getValue1().detach();
			}
		}
	}

	private void doSubscribeTimeout(final int timeout, EventListener<Event> listener, SubscribeAlive alive) {
		ValueII<Timer, Map<EventListener<Event>, SubscribeAlive>> pair1 = timers.get(timeout);
		if (pair1 == null) {
			Timer timer = new Timer(timeout * 1000);
			timer.setRepeats(true);
			timer.setParent(vtimerbox);
			timer.addEventListener(Events.ON_TIMER, new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					doTimeout(timeout);
				}
			});

			pair1 = new ValueII<>(timer, new LinkedHashMap<EventListener<Event>, Workspace.SubscribeAlive>());
			timers.put(timeout, pair1);
		}

		pair1.getValue2().put(listener, alive);
	}

	private void doTimeout(int timeout) throws Exception {
		ValueII<Timer, Map<EventListener<Event>, SubscribeAlive>> pair1 = timers.get(timeout);
		if (pair1 != null) {
			Map<EventListener<Event>, SubscribeAlive> pair2 = pair1.getValue2();
			Iterator<Entry<EventListener<Event>, SubscribeAlive>> iter = pair2.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<EventListener<Event>, SubscribeAlive> e = iter.next();
				if (e.getValue().alive()) {
					e.getKey().onEvent(new OnTimeoutEvent(timeout));
				} else {
					iter.remove();
				}
			}

			if (pair2.size() == 0) {
				timers.remove(timeout);
				pair1.getValue1().detach();
			}
		}
	}

	private void doChangeArgs(boolean refresh, String... args) {
		PageEntry entry = null;
		try {
			entry = AppContext.bean(PageEntryRegistory.class).get(page);
		} catch (NotFoundException x) {
			Zks.showClientWarning(Zks.getLabelWithArg("axes.msg.pageNotFound", page));
			return;
		}

		if (args == null) {
			args = new String[0];
		}

		String thisArgPath = Strings.cat(Collections.asList(this.args), "/");
		String argPath = Strings.cat(Collections.asList(args), "/");

		this.args = args;

		vbodyInc.setDynamicProperty(Constants.INCLUDE_ARGS, args);

		if (!Objects.equal(thisArgPath, argPath)) {
			Clients.evalJavaScript(Strings.format("_axes.pushUrl('/{}/{}{}{}','{}')", workspace.getContextName(), page,
					Strings.isBlank(argPath) ? "" : "/", argPath,
					Zks.getPrefixedLabel(entry.getTitle().replace("'", "\""))));
		}
		if (refresh) {
			vbodyInc.invalidate();
		}
	}

	private void doChangePage(String page, String... args) {
		// following code sequence is important.
		PageEntry entry = null;
		try {
			entry = AppContext.bean(PageEntryRegistory.class).get(page);
		} catch (NotFoundException x) {
			Zks.showClientWarning(Zks.getLabelWithArg("axes.msg.pageNotFound", page));
			return;
		}

		if (!workspace.hasPermission(entry.getPermissionRequest())) {
			Zks.showClientWarning(Zks.getLabel("axes.msg.nopermission"));
			return;
		}
		if (args == null) {
			args = new String[0];
		}

		String thisPage = this.page;
		String thisArgPath = Strings.cat(Collections.asList(this.args), "/");

		String argPath = Strings.cat(Collections.asList(args), "/");

		this.page = page;
		this.args = args;

		if (!Objects.equal(thisPage, page) || !Objects.equal(thisArgPath, argPath)) {
			Clients.evalJavaScript(Strings.format("_axes.pushUrl('/{}/{}{}{}','{}')", workspace.getContextName(), page,
					Strings.isBlank(argPath) ? "" : "/", argPath,
					Zks.getPrefixedLabel(entry.getTitle().replace("'", "\""))));
		} else {
			Executions.getCurrent().getDesktop().getFirstPage()
					.setTitle(Zks.getPrefixedLabel(entry.getTitle().replace("'", "\"")));
		}

		vbodyInc.setDynamicProperty(Constants.INCLUDE_ARGS, args);
		vbodyInc.setSrc(entry.getViewUri());

		if (!Objects.equal(thisPage, page) || !Objects.equal(thisArgPath, argPath)) {
			vbodyInc.invalidate();
		}
		vlicense.setVisible(Strings.isBlank(vbodyInc.getSrc()));
	}
}
