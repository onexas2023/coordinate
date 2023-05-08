package onexas.axes.web.zk.component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import onexas.axes.web.zk.core.TaglibFn;


/**
 * 
 * @author Dennis Chen
 * 
 */
public class Wpds {

	public static final String getXtermJs(ServletRequest request, ServletResponse response) {
		StringBuilder out = new StringBuilder();
		out.append(TaglibFn.loadJsResource("/3rdjs/axes/xterm.js", true, false));
		out.append(TaglibFn.loadJsResource("/3rdjs/axes/xterm-addon-fit.js", true, false));
		out.append(TaglibFn.loadJsResource("/3rdjs/axes/xterm-addon-attach.js", true, false));
		return out.toString();
	}
}
