package onexas.axes.web.zk.util;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;
import org.zkoss.zul.Messagebox.ClickEvent;

import onexas.axes.web.zk.util.MessageboxInputs.InputCheckEvent;
import onexas.axes.web.zk.util.MessageboxInputs.InputClickEvent;
import onexas.coordinate.common.lang.Collections;

/**
 * 
 * @author Dennis Chen
 * 
 */
public class Messageboxes {

	public final static String QUESTION_ICON = "fa fa-question-circle msgbox-question";
	public final static String INFORMATION_ICON = "fa fa-info-circle msgbox-info";
	public final static String EXCLAMATION_ICON = "fa fa-exclamation-triangle msgbox-exclamation";
//	public final static String ERROR_ICON = "fa fa-question-circle fa-3x "+Messagebox.ERROR;

	static {
		Messagebox.setTemplate("~@/zkoverride/messagebox_template.zul");
	}

	public static void showInformationBox(String title, String msg, final EventListener<Event> yesListener) {
		showInformationBox(title, msg, yesListener, null);
	}

	public static void showInformationBox(String title, String msg, final EventListener<Event> yesListener,
			String sclass) {
		if (title == null) {
			title = Labels.getLabel("zechspace.information");
		}
		Map<String, String> params = null;
		if (sclass != null) {
			params = new HashMap<>();
			params.put("sclass", sclass);
		}

		Messagebox.show(msg, title, Collections.asArray(Button.YES), null, INFORMATION_ICON, Button.YES,
				yesListener == null ? null : new EventListener<ClickEvent>() {
					@Override
					public void onEvent(ClickEvent event) throws Exception {
						if (Button.YES.equals(event.getButton())) {
							if (yesListener != null) {
								yesListener.onEvent(event);
							}
						}
					}
				}, params);
	}

	public static void showWarningBox(String title, String msg, final EventListener<Event> yesListener) {
		if (title == null) {
			title = Labels.getLabel("zechspace.warning");
		}
		Messagebox.show(msg, title, Collections.asArray(Button.YES), EXCLAMATION_ICON, Button.YES,
				yesListener == null ? null : new EventListener<ClickEvent>() {
					@Override
					public void onEvent(ClickEvent event) throws Exception {
						if (Button.YES.equals(event.getButton())) {
							if (yesListener != null) {
								yesListener.onEvent(event);
							}
						}
					}
				});
	}

	public static void showConfirmationWarningBox(String title, String msg, final EventListener<Event> yesListener,
			final EventListener<Event> noListener) {
		showConfirmationBox(title, msg, EXCLAMATION_ICON, yesListener, noListener);
	}

	public static void showConfirmationBox(String title, String msg, final EventListener<Event> yesListener,
			final EventListener<Event> noListener) {
		showConfirmationBox(title, msg, QUESTION_ICON, yesListener, noListener);
	}

	public static void showConfirmationBox(String title, String msg, String iconSclass,
			final EventListener<Event> yesListener, final EventListener<Event> noListener) {
		if (title == null) {
			title = Labels.getLabel("axes.confirmation");
		}
		Messagebox.show(msg, title, Collections.asArray(Button.NO, Button.YES), iconSclass, Button.NO,
				new EventListener<ClickEvent>() {
					@Override
					public void onEvent(ClickEvent event) throws Exception {
						Event evt = new Event(event.getName(), event.getTarget());

						if (event.getButton() == null || Button.NO.equals(event.getButton())) {
							if (noListener != null) {
								noListener.onEvent(evt);
							}
						} else if (Button.YES.equals(event.getButton())) {
							if (yesListener != null) {
								yesListener.onEvent(evt);
							}
						}
					}
				});
	}

	public static void showInputConfirmWarningBox(String title, String msg, String text,
			final EventListener<Event> yesListener, final EventListener<Event> noListener) {
		showInputConfirmationBox(title, msg, text, EXCLAMATION_ICON, yesListener, noListener);
	}

	public static void showInputConfirmationBox(String title, String msg, String text,
			final EventListener<Event> yesListener, final EventListener<Event> noListener) {
		showInputConfirmationBox(title, msg, text, QUESTION_ICON, yesListener, noListener);
	}

	public static void showInputConfirmationBox(String title, String msg, String text, String iconSclass,
			final EventListener<Event> yesListener, final EventListener<Event> noListener) {
		if (title == null) {
			title = Labels.getLabel("axes.confirmation");
		}
		MessageboxInputs.showInputText(msg, title, text, Collections.asArray(Button.NO, Button.YES), iconSclass, null,
				new EventListener<InputClickEvent<String>>() {
					@Override
					public void onEvent(InputClickEvent<String> event) throws Exception {
						Event evt = new Event(event.getName(), event.getTarget(), event.getInput());

						if (event.getButton() == null || Button.NO.equals(event.getButton())) {
							if (noListener != null) {
								noListener.onEvent(evt);
							}
						} else if (Button.YES.equals(event.getButton())) {
							if (yesListener != null) {
								yesListener.onEvent(evt);
							}
						}
					}
				});
	}

	public static void showInputConfirmWarningBox(String title, String msg, String checklabel, Boolean checked,
			final EventListener<Event> yesListener, final EventListener<Event> noListener) {
		showInputConfirmationBox(title, msg, checklabel, checked, EXCLAMATION_ICON, yesListener, noListener);
	}

	public static void showInputConfirmationBox(String title, String msg, String checklabel, Boolean checked,
			final EventListener<Event> yesListener, final EventListener<Event> noListener) {
		showInputConfirmationBox(title, msg, checklabel, checked, QUESTION_ICON, yesListener, noListener);
	}

	public static void showInputConfirmationBox(String title, String msg, String checklabel, Boolean checked,
			String iconSclass, final EventListener<Event> yesListener, final EventListener<Event> noListener) {
		if (title == null) {
			title = Labels.getLabel("axes.confirmation");
		}
		MessageboxInputs.showInputCheck(msg, title, checklabel, checked, Collections.asArray(Button.NO, Button.YES),
				iconSclass, null, new EventListener<InputCheckEvent>() {
					@Override
					public void onEvent(InputCheckEvent event) throws Exception {
						Event evt = new Event(event.getName(), event.getTarget(), event.getInput());

						if (event.getButton() == null || Button.NO.equals(event.getButton())) {
							if (noListener != null) {
								noListener.onEvent(evt);
							}
						} else if (Button.YES.equals(event.getButton())) {
							if (yesListener != null) {
								yesListener.onEvent(evt);
							}
						}
					}
				});
	}
}
