package onexas.coordinate.service.event;

import java.util.Map;

import onexas.coordinate.common.app.ApplicationEvent;
import onexas.coordinate.model.Hook;

/**
 * 
 * @author Dennis Chen
 *
 */
public class TriggerHookEvent extends ApplicationEvent<Hook> {
	private static final long serialVersionUID = 1L;

	Map<String, Object> args;

	public TriggerHookEvent(Hook hook, Map<String, Object> args) {
		super(hook);
		this.args = args;
	}

	public Map<String, Object> getArgs() {
		return args;
	}

}
