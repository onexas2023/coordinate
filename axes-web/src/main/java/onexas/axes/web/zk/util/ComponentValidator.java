package onexas.axes.web.zk.util;

import java.util.List;
import java.util.Locale;

import javax.validation.MessageInterpolator;

import org.zkoss.lang.Objects;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Component;

import onexas.coordinate.common.util.BeanValidator;
import onexas.coordinate.common.util.BeanValidator.ValidationResult;

/**
 * 
 * @author Dennis Chen
 *
 */
public class ComponentValidator {

	boolean valid = true;

	BeanValidator beanValidator;

	Component container;

	public ComponentValidator() {

	}

	/**
	 * 
	 * @param container the container to contain validation message, it is usually
	 *                  provided if you have a movable or scrollable parent.
	 */
	public ComponentValidator(Component container) {
		this.container = container;
	}

	private void initBeanValidator() {
		if (beanValidator == null) {
			beanValidator = new BeanValidator();
			beanValidator.setMessageInterpolator(
					new LocaleAawareMessageInterpolator(BeanValidator.DEFAULT_MESSAGE_INTERPOLATOR));
		}
	}

	/**
	 * validate the beanClz's property, return the local validation result.
	 */
	public boolean validate(Class<?> beanClz, String property, Object value, Component comp) {
		initBeanValidator();
		ValidationResult<?> r = beanValidator.validate(beanClz, property, value);
		boolean valid = r.isValid();
		if (valid) {
			clearWrongValue(comp);
		} else {
			List<String> msgs = r.getMessages();
			StringBuilder sb = new StringBuilder();
			for (String msg : msgs) {
				// trim
				if (msg.startsWith(property + ":")) {
					msg = msg.substring(property.length() + 1, msg.length());
				}
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(Zks.getPrefixedLabel(msg));
			}
			wrongValue(comp, sb.toString());
		}
		this.valid &= valid;
		return valid;
	}

	public String validate(Class<?> beanClz, String property, Object value) {
		initBeanValidator();
		ValidationResult<?> r = beanValidator.validate(beanClz, property, value);
		boolean valid = r.isValid();
		String allMsg = null;
		if (valid) {
			//nothing
		} else {
			List<String> msgs = r.getMessages();
			StringBuilder sb = new StringBuilder();
			for (String msg : msgs) {
				// trim
				if (msg.startsWith(property + ":")) {
					msg = msg.substring(property.length() + 1, msg.length());
				}
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(Zks.getPrefixedLabel(msg));
			}
			allMsg = sb.toString();
		}
		this.valid &= valid;
		return allMsg;
	}

	public boolean validate(Object value, Component comp) {
		initBeanValidator();

		ValidationResult<?> r = beanValidator.validate(value);
		boolean valid = r.isValid();
		if (valid) {
			clearWrongValue(comp);
		} else {
			List<String> msgs = r.getMessages();
			StringBuilder sb = new StringBuilder();
			for (String msg : msgs) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(Zks.getPrefixedLabel(msg));
			}
			wrongValue(comp, sb.toString());
		}
		this.valid &= valid;
		return valid;
	}

	public boolean validateEquals(Object value1, Object value2, Component comp, String invalidMsg) {
		if (Objects.equals(value1, value2)) {
			clearWrongValue(comp);
			return true;
		} else {
			wrongValue(comp, invalidMsg);
			valid = false;
			return false;
		}
	}

	/**
	 * invalidate the component, you usually have to call {@link #clear(Component)}
	 * manually if it passes validation.
	 * 
	 * @param comp
	 * @param invalidMsg
	 */
	public void invalidate(Component comp, String invalidMsg) {
		wrongValue(comp, invalidMsg);
		valid = false;
	}

	public void invalidate(String invalidMsg) {
		Zks.showClientWarning(invalidMsg);
		valid = false;
	}

	/**
	 * clear the validation message of the component
	 */
	public void clear(Component comp) {
		clearWrongValue(comp);
	}

	private void wrongValue(Component comp, String msg) {
		Zks.showWrongValue(comp, msg, container);
	}

	public static void clearWrongValue(Component comp) {
		if (comp == null) {
			return;
		}
		Zks.clearWrongValue(comp);
	}

	public boolean validate(boolean valid, Component comp, String invalidMsg) {
		if (comp == null) {
			throw new NullPointerException("component is null for message " + invalidMsg);
		}
		if (valid) {
			clear(comp);
		} else {
			invalidate(comp, invalidMsg);
		}
		return valid;
	}

	public boolean isValid() {
		return valid;
	}

	static class LocaleAawareMessageInterpolator implements MessageInterpolator {

		MessageInterpolator delegator;

		public LocaleAawareMessageInterpolator(MessageInterpolator delegator) {
			this.delegator = delegator;
		}

		@Override
		public String interpolate(String messageTemplate, Context context) {
			return interpolate(messageTemplate, context, Locales.getCurrent());
		}

		@Override
		public String interpolate(String messageTemplate, Context context, Locale locale) {
			return delegator.interpolate(messageTemplate, context, locale);
		}

	}
}
