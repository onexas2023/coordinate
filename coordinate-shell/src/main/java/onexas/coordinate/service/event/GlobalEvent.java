package onexas.coordinate.service.event;

import java.io.Serializable;

import onexas.coordinate.common.app.ApplicationEvent;

/**
 * 
 * @author Dennis Chen
 *
 */
public class GlobalEvent extends ApplicationEvent<Serializable> {
	private static final long serialVersionUID = 1L;
	
	String name;

	public GlobalEvent(String name, Serializable data) {
		super(data);
		this.name = name;
	}
	public GlobalEvent(String name) {
		this(name, null);
	}

	public String getName() {
		return name;
	}
	
}
