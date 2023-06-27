package onexas.coordinate.common.util;


import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.el.BeanNameELResolver;
import javax.el.BeanNameResolver;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.StandardELContext;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Expressions {

	private static final ExpressionFactory factory = ExpressionFactory.newInstance();

	public static Object eval(String expression,@Nullable ELResolver ...resolvers) throws ELException {
		return eval(expression, Object.class, resolvers);
	}

	public static Object eval(String expression, @Nullable Map<String, Object> variables) throws ELException {
		return eval(expression, Object.class, variables);
	}

	public static <T> T eval(String expression, Class<T> expectedType, @Nullable ELResolver ...resolvers) throws ELException {
		return eval(expression, expectedType, null, resolvers);
	}

	@SuppressWarnings("unchecked")
	public static <T> T eval(String expression, Class<T> expectedType, @Nullable Map<String, Object> variables,
			@Nullable ELResolver ...resolvers) throws ELException {

		StandardELContext elContext = new StandardELContext(factory);

		if (variables != null) {
			VariableMapper m = elContext.getVariableMapper();
			for (Entry<String, Object> entry : variables.entrySet()) {
				m.setVariable(entry.getKey(), factory.createValueExpression(entry.getValue(), Object.class));
			}
		}
		if (resolvers != null) {
			for (ELResolver r : resolvers) {
				elContext.addELResolver(r);
			}
		}

		ValueExpression ve = factory.createValueExpression(elContext, expression, expectedType);

		Object r = ve.getValue(elContext);
		return (T) r;
	}
	
	public static final BeanNameELResolver AllowNullBaseObjectResolver = new BeanNameELResolver(new AlwaysResolvedBeanNameResolver()); 
	
	private static class AlwaysResolvedBeanNameResolver extends BeanNameResolver{

		@Override
		public boolean isNameResolved(String beanName) {
			return true;		}

		@Override
		public Object getBean(String beanName) {
			return null;
		}
		
	}
}
