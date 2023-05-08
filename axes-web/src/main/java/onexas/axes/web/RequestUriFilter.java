package onexas.axes.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import onexas.coordinate.common.lang.Strings;

/**
 * to make the very first request uri
 * 
 * @author Dennis Chen
 *
 */
public class RequestUriFilter implements Filter {

	public static final String ATTR_REQUEST_URI = "axes.request_uri";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//do nothing
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request.getAttribute(ATTR_REQUEST_URI) == null) {
			String queryString = ((HttpServletRequest) request).getQueryString();
			if (!Strings.isBlank(queryString)) {
				queryString = "?" + queryString;
			} else {
				queryString = "";
			}
			request.setAttribute(ATTR_REQUEST_URI, ((HttpServletRequest) request).getRequestURI() + queryString);
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		//do nothing
	}

}
