package onexas.coordinate.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;

/**
 * 
 * @author Dennis Chen
 *
 */
public class ObjectConstraintHost implements ConstraintValidator<ObjectConstraint, Object> {

	ObjectConstraintValidator<Object> validator;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(final ObjectConstraint constraintAnnotation) {
		try {
			validator = (ObjectConstraintValidator<Object>) constraintAnnotation.value().newInstance();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			validator.validate(value);
			return true;
		} catch (ValidationException x) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(x.getMessage()).addConstraintViolation();
			return false;
		}
	}
}