package onexas.coordinate.common.lang;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 
 * @author Dennis Chen
 *
 */
public class JNDIs {

	public static Object get(String jndi) throws NamingException {
		InitialContext ctx = new InitialContext();
		Object obj = ctx.lookup(jndi);
		return obj;
	}
}