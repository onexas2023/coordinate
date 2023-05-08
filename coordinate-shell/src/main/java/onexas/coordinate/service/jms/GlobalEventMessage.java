package onexas.coordinate.service.jms;

import java.io.Serializable;

import onexas.coordinate.service.event.GlobalEvent;

/**
 * 
 * @author Dennis Chen
 *
 */
public class GlobalEventMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	GlobalEvent event;

	public GlobalEventMessage(GlobalEvent event) {
		super();
		this.event = event;
	}

	public GlobalEvent getEvent() {
		return event;
	}
}
