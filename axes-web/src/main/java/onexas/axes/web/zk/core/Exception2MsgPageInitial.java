package onexas.axes.web.zk.core;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

import onexas.axes.web.zk.util.Zks;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Exception2MsgPageInitial implements Initiator {

	public void doInit(Page page, Map<String, Object> args) throws Exception {
		String msg = (String) Executions.getCurrent().getAttribute("javax.servlet.error.message");
		Zks.showClientWarning(msg);
	}

}
