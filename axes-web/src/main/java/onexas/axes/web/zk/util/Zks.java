package onexas.axes.web.zk.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Idspace;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;

import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.CalendarHelper;

/**
 * 
 * @author Dennis Chen
 * 
 */
public class Zks {

	private static final Logger logger = LoggerFactory.getLogger(Zks.class);

	/**
	 * refresh the zk session time, it is usually using when you have a timeout that
	 * want to keep session alive
	 */
	public static void refreshSession() {
		((SessionCtrl) Executions.getCurrent().getSession()).notifyClientRequest(true);
	}

	public static String px(int size) {
		return Strings.format("{}px", size);
	}

	public static void openPopup(String uri, Page container, Component ref, int width, int height, String position,
			@SuppressWarnings("rawtypes") Map arg) {
		openPopup0(uri, container, ref, width, height, position, arg);
	}

	public static void openPopup(String uri, Component container, Component ref, int width, int height, String position,
			@SuppressWarnings("rawtypes") Map arg) {
		openPopup0(uri, container, ref, width, height, position, arg);
	}

	private static void openPopup0(String uri, Object container, Component ref, int width, int height, String position,
			@SuppressWarnings("rawtypes") Map arg) {

		Popup popup = new Popup();
		if (container instanceof Page) {
			popup.setPage((Page) container);
		} else {
			popup.setParent((Component) container);
		}
		popup.setHeight(height < 0 ? "100vh" : px(height));
		popup.setWidth(width < 0 ? "100vw" : px(width));
		popup.addEventListener(Events.ON_OPEN, new EventListener<OpenEvent>() {
			@Override
			public void onEvent(OpenEvent event) throws Exception {
				if (!event.isOpen()) {
					event.getTarget().detach();
				}
			}
		});
		Idspace spaceowner = new Idspace();
		spaceowner.setParent(popup);
		spaceowner.setHflex("1");
		spaceowner.setVflex("1");

		Executions.createComponents(uri, spaceowner, arg);

		popup.open(ref, position);
	}

	public static final void closePopup(Component inner) {
		while (inner != null) {
			if (inner instanceof Popup) {
				((Popup) inner).close();
				OpenEvent evt = new OpenEvent(Events.ON_OPEN, inner, false);
				Events.postEvent(evt);
				return;
			}
			inner = inner.getParent();
		}
	}

	public static final String getPrefixedLabel(String label) {
		return getPrefixedLabel(label, " ");
	}

	public static final String getPrefixedLabel(String label, String interBrace) {
		if (label == null) {
			return "";
		}
		if (label.startsWith("l:")) {
			String key = label.substring(2);
			String l = Labels.getLabel(key);
			if (l == null) {
				l = key;
				logger.warn(Strings.format("label not found. key = [{}], locale=[{}]", key, Locales.getCurrent()));
			}
			return l;
		} else if (label.startsWith("{") && label.endsWith("}")) {
			String[] ll = Strings.matchBraces(label, null);
			if (ll.length == 0) {
				return label;
			}
			StringBuilder sb = new StringBuilder();
			for (String l : ll) {
				if (sb.length() > 0) {
					sb.append(interBrace);
				}
				sb.append(getPrefixedLabel(l));
			}
			return sb.toString();
		}
		return label;
	}

	public static String getPrefixedLabel(List<String> labels) {
		return getPrefixedLabel(labels, " ");
	}

	public static String getPrefixedLabel(List<String> labels, String interLabel) {
		if (labels == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String l : labels) {
			if (sb.length() > 0) {
				sb.append(interLabel);
			}
			sb.append(getPrefixedLabel(l, interLabel));
		}
		return sb.toString();
	}

	public static final String getPrefixedLabel(String label, Object[] args) {
		if (label == null) {
			return "";
		}
		if (label.startsWith("l:")) {
			String key = label.substring(2);
			return Labels.getLabel(key, args);
		}
		return label;
	}

	public static String getLabelWithArg(String key, Object... args) {
		try {
			return Labels.getLabel(key, args);
		} catch (Exception x) {
			logger.error(x.getMessage(), x);
			// java.lang.IllegalArgumentException: Unmatched braces in the
			// pattern.
			return key;
		}
	}

	public static String getLabel(String key) {
		return Labels.getLabel(key, key);
	}

	public static String getLabel(String key, String defValue) {
		return Labels.getLabel(key, defValue);
	}

	public static void showClientNotification(String msg) {
		showClientNotification(msg, null, null, null, 3000, true);
	}
	
	public static void showClientNotification(String msg, int duration) {
		showClientNotification(msg, null, null, null, duration, true);
	}

	public static void showClientNotification(String msg, Component ref) {
		showClientNotification(msg, ref, "center right", "center left", 3000, true);
	}

	public static void showClientNotification(String msg, Component ref, String atPos, String myPos, int duration,
			boolean closable) {
		showDesktopMessage("notify", msg, ref, atPos, myPos, duration, closable);
	}

	public static void showClientWarning(String msg) {
		showClientWarning(msg, null, null, null, 10000, true);
	}
	
	public static void showClientWarning(String msg, int duration) {
		showClientWarning(msg, null, null, null, duration, true);
	}

	public static void showClientWarning(String msg, Component ref) {
		showClientWarning(msg, ref, "center right", "center left", 10000, true);
	}

	public static void showClientWarning(String msg, Component ref, String atPos, String myPos, int duration,
			boolean closable) {
		showDesktopMessage("warning", msg, ref, atPos, myPos, duration, closable);
	}

	private static void showDesktopMessage(String type, String msg, Component ref, String atPos, String myPos,
			int duration, boolean closable) {
		StringBuilder sb = new StringBuilder();

		// zs.message = function(selector, type, msg, atPos, myPos, duration,
		// closable){
		// use timeout to solve ref renrender sometime after update.
		sb.append("setTimeout(function(){_axes.message(");
		if (ref == null) {
			sb.append("null,");
		} else {
			sb.append("'#").append(ref.getUuid()).append("',");
		}

		sb.append("'").append(type).append("',");

		msg = StringEscapeUtils.escapeJavaScript(msg);

		sb.append("'").append(msg).append("',");

		if (Strings.isBlank(atPos)) {
			sb.append("null,");
		} else {
			sb.append("'").append(atPos).append("',");
		}

		if (Strings.isBlank(myPos)) {
			sb.append("null,");
		} else {
			sb.append("'").append(myPos).append("',");
		}

		sb.append(duration).append(",").append(closable);
		sb.append(")},50);");
		Clients.evalJavaScript(sb.toString());
	}
	
	public static void copyToClipboard(String text) {
		StringBuilder sb = new StringBuilder();

		sb.append("setTimeout(function(){");

		text = StringEscapeUtils.escapeJavaScript(text);
		sb.append("_axes.copyToClipboard('").append(text).append("');");

		//end set function/timeout
		sb.append("},50);");
		Clients.evalJavaScript(sb.toString());
	}

	public static void showClientBusy(String msg) {
		Clients.showBusy(msg);
	}

	public static void showClientBusy(Component comp, String msg) {
		Clients.showBusy(comp, msg);
	}

	public static void clearClientBusy() {
		Clients.clearBusy();
	}

	public static void showBusyThenRun(Component comp, String msg, Runnable run) {
		Zks.showClientBusy(msg);
		Zks.echo(comp, new ClearBusyRunnable(run));
	}

	public static void showBusyThenRun(Component comp, Runnable run) {
		showBusyThenRun(comp, Zks.getLabel("axes.processing"), run);
	}

	public static void showBusyThenRun(Runnable run) {
		showBusyThenRun(getFirstComponent(), Zks.getLabel("axes.processing"), run);
	}

	public static void showBusyThenRun(String msg, Runnable run) {
		showBusyThenRun(getFirstComponent(), msg, run);
	}

	public static Component getFirstComponent() {
		Collection<Page> pages = Executions.getCurrent().getDesktop().getPages();
		for (Page p : pages) {
			Collection<Component> roots = p.getRoots();
			for (Component r : roots) {
				return r;
			}
		}
		return null;
	}

	public static class ClearBusyRunnable implements Runnable {
		Runnable run;

		public ClearBusyRunnable(Runnable run) {
			this.run = run;
		}

		@Override
		public void run() {
			clearClientBusy();
			run.run();
		}
	}

	/**
	 * return non-null value and trim it;
	 */
	public static String trimValue(Textbox textbox) {
		return trimValue(textbox.getValue());
	}

	/**
	 * return non-null value and trim it;
	 */
	public static String trimValue(String value) {
		return value == null ? "" : value.trim();
	}

	/**
	 * return non-null value;
	 */
	public static String value(Textbox textbox) {
		String value = textbox.getValue();
		return value == null ? "" : value;
	}

	public static void postEvent(String evtName, Component target, Object data) {
		Executions.getCurrent().postEvent(new Event(evtName, target, data));
	}

	public static void dump(Component comp) {
		StringBuilder sb = new StringBuilder("====component dump====\n");
		dump(comp, sb, 0);
		System.out.println(sb.toString());
	}

	private static void dump(Component comp, StringBuilder sb, int indent) {
		if (indent > 0) {
			sb.append("\n");
		}
		for (int i = 0; i < indent; i++) {
			if (i == indent - 1) {
				sb.append(" +");
			} else {
				sb.append("  ");
			}
		}
		sb.append(comp.toString());
		List<Component> children = comp.getChildren();
		for (Component child : children) {
			dump(child, sb, indent + 1);
		}
	}

	public static TimeZone getTimeZone(Datebox fromBox) {
		TimeZone timeZone = fromBox.getTimeZone();
		return timeZone == null ? TimeZones.getCurrent() : timeZone;
	}

	public static TimeZone getTimeZone(Timebox fromBox) {
		TimeZone timeZone = fromBox.getTimeZone();
		return timeZone == null ? TimeZones.getCurrent() : timeZone;
	}

	public static String getTimeZoneDisplayName(TimeZone timeZone) {
		String customKey = new StringBuilder("axes.timeZone.").append(timeZone.getID()).toString();
		String display = Labels.getLabel(customKey);
		return display == null ? timeZone.getDisplayName(Locales.getCurrent()) : display;
	}

	public static final Object NULL_SCOPE_ARG = new Object();

	public static Object getScopeArg(Component scope, String name) {
		Execution exec = Executions.getCurrent();
		@SuppressWarnings("rawtypes")
		Map arg = exec.getArg();
		Object val = null;
		if (arg != null) {
			val = arg.get(name);
			if (NULL_SCOPE_ARG == val) {
				return null;
			}
		}
		if (val == null) {
			val = exec.getAttribute(name);
			if (NULL_SCOPE_ARG == val) {
				return null;
			}
		}
		if (val == null && scope != null) {
			val = scope.getAttribute(name, true);
		}
		return val;
	}

	public static void showWrongValue(Component comp, String msg) {
		showWrongValue(comp, msg, null);
	}

	public static void showWrongValue(Component comp, String msg, Component container) {
		StringBuilder sb = new StringBuilder();
		msg = StringEscapeUtils.escapeJavaScript(msg);

		sb.append("_axes.wrongValue('#").append(comp.getUuid()).append("'");
		sb.append(",'").append(msg).append("'");
		if (container != null) {
			sb.append(",'#").append(container.getUuid()).append("'");
		}
		sb.append(");");
		Clients.evalJavaScript(sb.toString());
	}

	public static void clearWrongValue(Component comp) {
		StringBuilder sb = new StringBuilder();
		sb.append("_axes.clearWrongValue('#").append(comp.getUuid()).append("');");
		Clients.evalJavaScript(sb.toString());
	}

	public static CalendarHelper getCalendarHelper() {
		return new CalendarHelper(TimeZones.getCurrent());
	}

	public static void resize(Component ref) {
		Clients.resize(ref);
	}

	public static void echo(Component target, final Runnable run) {
		String key = "_zks.echoCount";
		Integer count = (Integer) target.getAttribute(key);
		if (count == null) {
			count = 0;
		}
		target.setAttribute(key, count + 1);

		final String evtnm = "onEchoBack" + count;
		final EventListener<Event> listener = new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				event.getTarget().removeEventListener(evtnm, this);
				run.run();
			}
		};
		Event evt = new Event(evtnm, target);
		target.addEventListener(evtnm, listener);
		Events.echoEvent(evt);
	}

	public static <T> void refreshListModeList(ListModelList<T> listModel, List<T> refreshedList) {
		refreshListModeList(listModel, refreshedList, null);
	}

	public interface RefreshReuqirer<T> {
		boolean require(T oldElement, T newElement);
	}

	public static <T> void refreshListModeList(ListModelList<T> listModel, List<T> refreshedList,
			RefreshReuqirer<T> requirer) {
		int s1 = listModel.size();
		int s2 = refreshedList.size();
		int i = 0;
		for (; i < s1 && i < s2; i++) {
			T elm1 = listModel.get(i);
			T elm2 = refreshedList.get(i);
			if (elm1.equals(elm2)) {
				if (elm1 != elm2) {
					if ((requirer == null || requirer.require(elm1, elm2))) {
						listModel.set(i, elm2);
					} else {
						// update without notify
						listModel.getInnerList().set(i, elm2);
					}
				}
			} else {
				break;
			}
		}
		for (; i < s2; i++) {
			T elm2 = refreshedList.get(i);
			if (i < s1) {
				listModel.set(i, elm2);
			} else {
				listModel.add(elm2);
			}
		}
		int j = i;
		for (; i < s1; i++) {
			listModel.remove(j);
		}
	}

	public static void redirect(String url, String target) {
		Executions.getCurrent().sendRedirect(url, target);
	}

	public static void redirect(String url) {
		Executions.getCurrent().sendRedirect(url);
	}

	public static Locale getLocale() {
		return Locales.getCurrent();
	}

	public static void setLocale(Locale locale) {
		Locales.setThreadLocal(locale);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Event> T unwrap(Event event, Class<T> type) {
		if (event == null)
			return null;

		while (event instanceof ForwardEvent) {
			event = ((ForwardEvent) event).getOrigin();
		}
		if (type.isAssignableFrom(event.getClass())) {
			return (T) event;
		}
		throw new ClassCastException(
				onexas.coordinate.common.lang.Strings.format("{} is not the type {}", event.getClass(), type));

	}

	public static void openDialog(String uri, Page page, String title, int width, int height, Map<String, Object> arg) {
		Window window = new Window();
		window.setPage(page);

		window.setTitle(title);
		window.setHeight(height < 0 ? "100vh" : px(height));
		window.setWidth(width < 0 ? "100vw" : px(width));
		window.setContentStyle("padding:0px");
		window.setSizable(true);
		window.setMode(Window.HIGHLIGHTED);
		window.setBorder("normal");
		window.setClosable(true);
		window.setMaximizable(true);

		window.setAttribute("$dialog.flag$", Boolean.TRUE);
		window.addEventListener(Events.ON_CLOSE, (evt) -> {
			window.setPage(null);
		});

		Executions.createComponents(uri, window, arg);
	}

	public static final void closeDialog(Component inner) {
		while (inner != null) {
			if (inner instanceof Window && Boolean.TRUE.equals(inner.getAttribute("$dialog.flag$"))) {
				Event evt = new Event(Events.ON_CLOSE, inner);
				Events.postEvent(evt);
				return;
			}
			inner = inner.getParent();
		}
	}

	public static void fixUploadMediaClean(Media m) {
		// a fix of zk doesn't clean media in upload case
		try {
			for (Field fi : m.getClass().getDeclaredFields()) {
				if(!"_fi".equals(fi.getName()) || !FileItem.class.isAssignableFrom(fi.getType())) {
					continue;
				}
				if (!fi.isAccessible()) {
					fi.setAccessible(true);
				}
				Object fiObj = fi.get(m);
				if (fiObj instanceof FileItem) {
					((FileItem) fiObj).delete();
				}
				break;
			}

		} catch (Exception x) {
			logger.warn(x.getMessage(), x);
		}
	}
}
