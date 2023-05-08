package onexas.coordinate.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;

import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 * 
 */
public class BeanValidator {

	// from javadoc, it is thread safe.
	private static final ValidatorFactory validatorFactory;
	static {
		validatorFactory = Validation.buildDefaultValidatorFactory();
	}

	public static final MessageInterpolator DEFAULT_MESSAGE_INTERPOLATOR = validatorFactory.getMessageInterpolator();

	private Validator validator;

	private MessageInterpolator messageInterpolator;

	public MessageInterpolator getMessageInterpolator() {
		return messageInterpolator;
	}

	public void setMessageInterpolator(MessageInterpolator messageInterpolator) {
		this.messageInterpolator = messageInterpolator;
		validator = null;
	}

	private Validator getValidator() {
		if (validator != null) {
			return validator;
		}
		ValidatorContext ctx = validatorFactory.usingContext();
		if (messageInterpolator != null) {
			ctx.messageInterpolator(messageInterpolator);
		}
		return validator = ctx.getValidator();
	}

	public BeanValidator() {
	}

	public <T> ValidationResult<T> validate(Class<T> clazz, String property, Object value) {
		Set<ConstraintViolation<T>> set = getValidator().validateValue(clazz, property, value);
		List<ConstraintViolation<T>> list = sort(set);
		return new ValidationResult<T>(clazz, property, value, list);
	}

	public <T> List<ValidationResult<T>> validate(Class<T> clazz, List<String> properties, List<Object> values) {
		int size = properties.size();
		List<ValidationResult<T>> result = new ArrayList<BeanValidator.ValidationResult<T>>(size);
		for (int i = 0; i < size; i++) {
			result.add(validate(clazz, properties.get(i), values.get(i)));
			i++;
		}
		return result;
	}

	public <T> ValidationResult<T> validate(T bean) {
		Set<ConstraintViolation<T>> set = getValidator().validate(bean);
		List<ConstraintViolation<T>> list = sort(set);
		return new ValidationResult<T>(bean, list);
	}

	public <T> ValidationResult<T> validateCollection(Collection<T> beans) {
		Set<ConstraintViolation<T>> set = new LinkedHashSet<>();
		beans.forEach((bean) -> {
			Set<ConstraintViolation<T>> s = getValidator().validate(bean);
			if (s != null && s.size() > 0) {
				set.addAll(sort(s));
			}
		});
		List<ConstraintViolation<T>> list = new LinkedList<>(set);
		return new ValidationResult<T>(beans, list);
	}

	/**
	 * Sort the violations, make multiple violations order more predictable. By
	 * default, sort it by the constraint name.
	 * 
	 * @param viloations
	 */
	static private <T> List<ConstraintViolation<T>> sort(Set<ConstraintViolation<T>> viloations) {
		if (viloations == null) {
			return null;
		}
		List<ConstraintViolation<T>> list = new ArrayList<ConstraintViolation<T>>(viloations);
		java.util.Collections.sort(list, new Comparator<ConstraintViolation<T>>() {
			public int compare(ConstraintViolation<T> o1, ConstraintViolation<T> o2) {
				String s1 = o1.getConstraintDescriptor().getAnnotation().toString();
				String s2 = o2.getConstraintDescriptor().getAnnotation().toString();
				return s1.compareTo(s2);
			}
		});
		return list;
	}

	static public class ValidationResult<T> {
		final Class<T> clazz;
		final String property;
		final Object value;
		final List<ConstraintViolation<T>> violations;

		private ValidationResult(Class<T> clazz, String property, Object value,
				List<ConstraintViolation<T>> violation) {
			this.clazz = clazz;
			this.property = property;
			this.value = value;
			this.violations = violation;
		}

		@SuppressWarnings("unchecked")
		private ValidationResult(T bean, List<ConstraintViolation<T>> violation) {
			this.clazz = (Class<T>) bean.getClass();
			this.property = "*";
			this.value = bean;
			this.violations = violation;
		}

		@SuppressWarnings("unchecked")
		private ValidationResult(Collection<T> beans, List<ConstraintViolation<T>> violation) {
			this.clazz = (Class<T>) beans.getClass();
			this.property = "*";
			this.value = beans;
			this.violations = violation;
		}

		public Class<T> getClazz() {
			return clazz;
		}

		public String getProperty() {
			return property;
		}

		public Object getValue() {
			return value;
		}

		public List<ConstraintViolation<T>> getViolations() {
			return violations;
		}

		public List<String> getMessages() {
			if (violations == null) {
				return null;
			}
			List<String> msgs = new ArrayList<String>();
			for (ConstraintViolation<T> v : violations) {
				msgs.add(v.getPropertyPath().toString() + ":" + v.getMessage());
			}

			return msgs;
		}

		public boolean isValid() {
			return violations == null || violations.size() == 0;
		}

		public void checkArgument() throws BadArgumentException {
			if (!isValid()) {
				throw new BadArgumentException(Strings.cat(getMessages()));
			}
		}
	}

	public static void validateCheckArgument(Object bean) {
		new BeanValidator().validate(bean).checkArgument();
	}
}