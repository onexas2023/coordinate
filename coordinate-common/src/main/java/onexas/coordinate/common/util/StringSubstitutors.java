package onexas.coordinate.common.util;

import java.util.Map;

import org.apache.commons.text.StringSubstitutor;

/**
 * 
 * @author Dennis Chen
 *
 */
public class StringSubstitutors {

	/**
	 * replace variables in expression with ${variableName}
	 */
	public static String replace(String expression, Map<String, Object> variables) {
		StringSubstitutor sub = new StringSubstitutor(variables);
		sub.setEnableSubstitutionInVariables(true);
		String result = sub.replace(expression);
		return result;
	}
}
