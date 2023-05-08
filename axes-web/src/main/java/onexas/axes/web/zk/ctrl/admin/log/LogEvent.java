package onexas.axes.web.zk.ctrl.admin.log;

import org.zkoss.zk.ui.event.Event;

import onexas.coordinate.api.v1.sdk.model.ALog;
/**
 * 
 * @author Dennis Chen
 *
 */
public class LogEvent extends Event{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	LogEventType type;
	ALog log;
	
	public LogEvent(LogEventType type, ALog log) {
		super("onLogEvent");
		this.type = type;
		this.log = log;
	}

	public ALog getLog() {
		return log;
	}

	public void setLog(ALog log) {
		this.log = log;
	}

	public LogEventType getType() {
		return type;
	}
	

}