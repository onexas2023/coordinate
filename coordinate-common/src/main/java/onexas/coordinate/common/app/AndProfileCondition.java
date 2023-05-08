package onexas.coordinate.common.app;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Profiles;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

/**
 * 
 * @author Dennis Chen
 *
 */
class AndProfileCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(AndProfile.class.getName());
		if (attrs != null) {
			for (Object value : attrs.get("value")) {
				String[] profiles = (String[]) value;
				for(String profile:profiles) {
					if (!context.getEnvironment().acceptsProfiles(Profiles.of(profile))) {
						return false;
					}
				}
			}
			return true;
		}
		return true;
	}

}