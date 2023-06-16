package onexas.coordinate.common.util;

import java.util.Map;

import javax.annotation.Nullable;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Spels {

	public static Expression parse(String expression) throws ParseException {
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression(expression);
		return exp;
	}

	public static Object eval(String expression) throws ParseException, EvaluationException {
		return eval(expression, Object.class, null);
	}

	public static Object eval(String expression, Map<String, Object> variables)
			throws ParseException, EvaluationException {
		return eval(expression, Object.class, variables);
	}

	public static <T> T eval(String expression, Class<T> expectedType) throws ParseException, EvaluationException {
		return eval(expression, expectedType, null);
	}

	public static <T> T eval(String expression, Class<T> expectedType, @Nullable Map<String, Object> variables)
			throws ParseException, EvaluationException {
		return eval(parse(expression), expectedType, variables);
	}

	public static Object eval(Expression expression) throws EvaluationException {
		return eval(expression, Object.class, null);
	}

	public static Object eval(Expression expression, Map<String, Object> variables) throws EvaluationException {
		return eval(expression, Object.class, variables);
	}

	public static <T> T eval(Expression expression, Class<T> expectedType) throws EvaluationException {
		return eval(expression, expectedType, null);
	}

	public static <T> T eval(Expression expression, Class<T> expectedType, @Nullable Map<String, Object> variables)
			throws EvaluationException {
		StandardEvaluationContext evalContext = new StandardEvaluationContext();
		if (variables != null) {
			evalContext.setVariables(variables);
		}
		T value = expression.getValue(evalContext, variables, expectedType);
		return value;
	}
}
