package onexas.coordinate.common.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 
 * @author Dennis Chen
 *
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ObjectConstraintHost.class)
@Documented
public @interface ObjectConstraint {
	String message() default "{validation.object.constraint}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * Defines several <code>@ObjectConstraint</code> annotations on the same
	 * element
	 *
	 * @see ObjectConstraint
	 */
	@Target({ TYPE, ANNOTATION_TYPE })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		ObjectConstraint[] value();
	}
	
	Class<? extends ObjectConstraintValidator<?>> value();
	
}