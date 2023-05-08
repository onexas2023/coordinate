package onexas.coordinate.common.validation;

import javax.validation.ValidationException;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface ObjectConstraintValidator<T> {

	public void validate(T bean) throws ValidationException;
}
