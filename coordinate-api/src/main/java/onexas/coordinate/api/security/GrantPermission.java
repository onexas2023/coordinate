package onexas.coordinate.api.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Dennis Chen
 *
 */
@Target({ ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GrantPermission {

	String target();

	String[] action();
	
	boolean matchAll() default false;
}
