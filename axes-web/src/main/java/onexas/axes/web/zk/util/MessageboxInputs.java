package onexas.axes.web.zk.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.util.LabelObject;

import org.zkoss.zul.Textbox;

/**
 * @author Dennis
 * 
 */
public class MessageboxInputs {

//	private static final Logger logger = LoggerFactory.getLogger(MessageboxInputs.class);

	// path of the messagebox zul-template
	// private static String oldTemplate = Messagebox.getTemplate();
	private static String _input_text_templ = "~@/zkoverride/messagebox_input_text.zul";
	private static String _input_list_templ = "~@/zkoverride/messagebox_input_list.zul";
	private static String _input_check_templ = "~@/zkoverride/messagebox_input_check.zul";
//	private static Textbox inputTextbox;
	private static final Messagebox.Button[] DEFAULT_BUTTONS = new Messagebox.Button[] { Messagebox.Button.OK };

	public static void showInputText(String message, String title, String text, Button[] buttons, String icon,
			Button focus, EventListener<InputClickEvent<String>> listener) {
		showInputText(message, title, text, buttons, null, icon, focus, listener, null);
	}

	public static void showInputText(String message, String title, String text, Button[] buttons, String[] btnLabels,
			String icon, Button focus, EventListener<InputClickEvent<String>> listener, Map<String, String> params) {
		final Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("message", message);
		arg.put("title", title != null ? title : AppContext.config().getString("app.name"));
		arg.put("icon", icon);

		if (buttons == null)
			buttons = DEFAULT_BUTTONS;

		int btnmask = 0;
		for (int j = 0; j < buttons.length; ++j) {
			if (buttons[j] == null)
				throw new IllegalArgumentException("The " + j + "-th button is null");
		}
		arg.put("buttons", btnmask);

		if (params != null)
			arg.putAll(params);

		final MessageboxInputTextDlg dlg = (MessageboxInputTextDlg) Executions.createComponents(_input_text_templ, null,
				arg);
		dlg.setEventListener(listener);
		dlg.setButtons(buttons, btnLabels);

		Textbox inputTextbox = (Textbox) dlg.getFellowIfAny("inputTextbox");

		if (focus != null) {
			dlg.setFocus(focus);
		} else {
			inputTextbox.setFocus(true);
		}

		inputTextbox.setValue(text);

		dlg.doHighlighted();
		return;
	}

	public static <T> void showInputList(String message, String title, List<LabelObject<T>> list, T selected,
			Button[] buttons, String icon, Button focus, EventListener<InputClickEvent<T>> listener) {
		showInputList(message, title, list, selected, buttons, null, icon, focus, listener, null);
	}

	public static <T> void showInputList(String message, String title, List<LabelObject<T>> list, Object selected,
			Button[] buttons, String[] btnLabels, String icon, Button focus, EventListener<InputClickEvent<T>> listener,
			Map<String, String> params) {
		final Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("message", message);
		arg.put("title", title != null ? title : AppContext.config().getString("app.name"));
		arg.put("icon", icon);

		if (buttons == null)
			buttons = DEFAULT_BUTTONS;

		int btnmask = 0;
		for (int j = 0; j < buttons.length; ++j) {
			if (buttons[j] == null)
				throw new IllegalArgumentException("The " + j + "-th button is null");
		}
		arg.put("buttons", btnmask);

		if (params != null)
			arg.putAll(params);

		@SuppressWarnings("unchecked")
		final MessageboxInputListDlg<T> dlg = (MessageboxInputListDlg<T>) Executions.createComponents(_input_list_templ,
				null, arg);
		dlg.setEventListener(listener);
		dlg.setButtons(buttons, btnLabels);

		Listbox inputListbox = (Listbox) dlg.getFellowIfAny("inputListbox");

		if (focus != null) {
			dlg.setFocus(focus);
		} else {
			inputListbox.setFocus(true);
		}

		ListModelList<LabelObject<T>> model = new ListModelList<>(list);
		if (selected != null) {
			for (LabelObject<T> lo : model.getInnerList()) {
				if (selected.equals(lo.getObject())) {
					model.addToSelection(lo);
					break;
				}
			}
		}

		inputListbox.setModel(model);

		dlg.doHighlighted();
		return;
	}

	public static class InputClickEvent<T> extends Event {
		private static final long serialVersionUID = 1L;
		T input;

		public InputClickEvent(String name, Component target, Button button, T input) {
			super(name, target, button);
			this.input = input;
		}

		/**
		 * Returns the button being clicked. If the close button on the title is
		 * clicked, this method returns null (and {@link #getName} returns onClose).
		 */
		public Button getButton() {
			return (Button) getData();
		}

		public T getInput() {
			return input;
		}
	}

	public static class InputCheckEvent extends Event {
		private static final long serialVersionUID = 1L;
		Boolean input;

		public InputCheckEvent(String name, Component target, Button button, Boolean input) {
			super(name, target, button);
			this.input = input;
		}

		/**
		 * Returns the button being clicked. If the close button on the title is
		 * clicked, this method returns null (and {@link #getName} returns onClose).
		 */
		public Button getButton() {
			return (Button) getData();
		}

		public Boolean getInput() {
			return input;
		}
	}

	public static <T> void showInputCheck(String message, String title, String checklabel, Boolean checked,
			Button[] buttons, String icon, Button focus, EventListener<InputCheckEvent> listener) {
		showInputCheck(message, title, checklabel, checked, buttons, null, icon, focus, listener, null);
	}

	public static void showInputCheck(String message, String title, String checklabel, Boolean checked,
			Button[] buttons, String[] btnLabels, String icon, Button focus, EventListener<InputCheckEvent> listener,
			Map<String, String> params) {
		final Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("message", message);
		arg.put("title", title != null ? title : AppContext.config().getString("app.name"));
		arg.put("icon", icon);
		arg.put("checklabel", checklabel);

		if (buttons == null)
			buttons = DEFAULT_BUTTONS;

		int btnmask = 0;
		for (int j = 0; j < buttons.length; ++j) {
			if (buttons[j] == null)
				throw new IllegalArgumentException("The " + j + "-th button is null");
		}
		arg.put("buttons", btnmask);

		if (params != null)
			arg.putAll(params);

		final MessageboxInputCheckDlg dlg = (MessageboxInputCheckDlg) Executions.createComponents(_input_check_templ,
				null, arg);
		dlg.setEventListener(listener);
		dlg.setButtons(buttons, btnLabels);

		Checkbox inputCheckbox = (Checkbox) dlg.getFellowIfAny("inputCheckbox");

		if (focus != null) {
			dlg.setFocus(focus);
		} else {
			inputCheckbox.setFocus(true);
		}

		if (checked != null) {
			inputCheckbox.setChecked(Boolean.TRUE.equals(checked));
		}

		dlg.doHighlighted();
		return;
	}
}