package onexas.coordinate.data.util;

import java.util.Set;

import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.lang.Collections;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Fields {

	/**
	 * check if fields are all in scopes
	 * 
	 * @throws BadArgumentException if any string in fields is not in scopes
	 */
	public static void checkFieldsIn(String[] fields, String[] scopes) {
		if (fields == null || scopes == null) {
			return;
		}
		Set<String> ss = Collections.asSet(scopes);
		for (String f : fields) {
			if (!ss.contains(f)) {
				throw new BadArgumentException("{} is not in {}", f, ss) ;
			}
		}
	}
	
}
