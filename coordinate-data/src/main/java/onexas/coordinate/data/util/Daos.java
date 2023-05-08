package onexas.coordinate.data.util;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Daos {
	
	public static Predicate buildStringPredicate(CriteriaBuilder builder, Path<?> path, String attributeName,
			String value, Boolean containing, Boolean ignorecase) {
		if (Boolean.TRUE.equals(containing)) {
			value = "%" + value + "%";
			if (Boolean.TRUE.equals(ignorecase)) {
				return builder.like(builder.lower(path.<String>get(attributeName)), value.toLowerCase());
			} else {
				return builder.like(path.<String>get(attributeName), value);
			}
		} else {
			if (Boolean.TRUE.equals(ignorecase)) {
				return builder.equal(builder.lower(path.<String>get(attributeName)), value.toLowerCase());
			} else {
				return builder.equal(path.<String>get(attributeName), value);
			}
		}
	}
}
