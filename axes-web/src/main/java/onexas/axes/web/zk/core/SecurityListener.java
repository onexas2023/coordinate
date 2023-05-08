package onexas.axes.web.zk.core;

import java.util.List;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.EventInterceptor;
import org.zkoss.zk.ui.util.ExecutionCleanup;
import org.zkoss.zk.ui.util.ExecutionInit;


/**
 * 
 * @author Dennis Chen
 * 
 */
public class SecurityListener implements ExecutionInit, ExecutionCleanup, EventInterceptor {

//	
//	private static Logger logger = LoggerFactory.getLogger(SecurityListener.class);
//	
//	private static final String CREDENTAIL_NOT_AVAILABLE = SecurityListener.class.getName()
//			+ ".not_available";
//	private static final String LOGGED_OUT = SecurityListener.class.getName() + ".logged_out";

	@Override
	public void init(Execution exec, Execution parent) throws Exception {
		//TODO
//		Credential cred;
//		cred = Workspaces.getCurrent().getCredential();
//		if (cred.isGuest()) {
//			return;
//		}

//		if (!Services.getService(SecurityService.class).isAvailable(cred)) {
//			//can't reload here, set a flag, we reload when execute event
//			exec.setAttribute(CREDENTAIL_NOT_AVAILABLE, Boolean.TRUE);
//		}
	}

	@Override
	public void cleanup(Execution exec, Execution parent, List<Throwable> errs) throws Exception {
		//TODO
	}

	@Override
	public Event beforeSendEvent(Event event) {
		if(!checkAvailable()){
			return null;
		}
		return event;
	}

	private boolean checkAvailable() {
		//TODO
//		Execution exec = Executions.getCurrent();
//		if (Boolean.TRUE.equals(exec.getAttribute(CREDENTAIL_NOT_AVAILABLE))) {
//			if(!Boolean.TRUE.equals(exec.getAttribute(LOGGED_OUT))){
//				logger.info("credential {} not available anymore, logout user", Workspaces.getCurrent()
//						.getCredential().getAccount());
//				Workspaces.getCurrent().logout();
//				Executions.getCurrent().setAttribute(LOGGED_OUT,Boolean.TRUE);
//			}
//			return false;
//		}
		return true;
	}

	@Override
	public Event beforePostEvent(Event event) {
		if(!checkAvailable()){
			return null;
		}
		return event;
	}

	@Override
	public Event beforeProcessEvent(Event event) {
		if(!checkAvailable()){
			return null;
		}
		return event;
	}

	@Override
	public void afterProcessEvent(Event event) {
		//TODO
	}

}