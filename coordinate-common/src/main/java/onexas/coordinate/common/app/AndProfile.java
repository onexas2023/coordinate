package onexas.coordinate.common.app;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

/**
 * 
 * @author Dennis Chen
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(AndProfileCondition.class)
public @interface AndProfile {

	/**
	 * The set of profiles for which the annotated component should be registered.
	 */
	String[] value();

}