package onexas.axes.web.zk.core;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

import onexas.api.invoker.ApiException;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.model.Response;
import onexas.coordinate.common.util.Jsons;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Exception2AlertPageInitial implements Initiator {
	private static final Logger logger = LoggerFactory.getLogger(Exception2AlertPageInitial.class);

	public void doInit(Page page, Map<String, Object> args) throws Exception {
		String msg = (String) Executions.getCurrent().getAttribute("javax.servlet.error.message");
		Throwable exception = (Throwable) Executions.getCurrent().getAttribute("javax.servlet.error.exception");

		if (exception != null && exception instanceof ApiException) {
			ApiException apiex = (ApiException) exception;
			int code = apiex.getCode();
			
			Response res = null;

			String responsebody = apiex.getResponseBody();
			if (responsebody != null) {
				try {
					res = Jsons.objectify(responsebody, Response.class);
					msg = res.getMsg();
				}catch(Exception x) {}
			}
			
			if (msg == null) {
				msg = "Unknown message, code "+code;
			}
			
			switch (code) {
			case 400:
				msg = Zks.getLabelWithArg("axes.msg.apiBadRequest", msg);
				break;
			case 401:
				msg = Zks.getLabelWithArg("axes.msg.apiUnauthenticed", msg);
				break;
			case 403:
				msg = Zks.getLabelWithArg("axes.msg.apiNoPermission", msg);
				break;
			default:
				logger.error("Error Api Exception, body : {} ", responsebody);
				break;
			}
			logger.error("Api Exception : {}", msg);
			Zks.showClientWarning(msg);
			return;
		}

		if (Strings.isBlank(msg) && exception != null) {
			msg = exception.getClass().toString();
		}
//		
		msg = Zks.getLabelWithArg("axes.msg.systemError", msg);
		// TODO
//		Messageboxes.showWarningBox(title, errmsg, null);
		Zks.showClientWarning(msg);
	}
}
