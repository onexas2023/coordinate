package onexas.axes.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LocaleThreadLocalFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//do nothing
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//TODO check this work or not
//		org.zkoss.util.Locales.setThreadLocal(AppContext.bean(Workspace.class).getPreferredLocale());
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		//do nothing
	}

}
