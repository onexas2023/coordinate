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

	String posterUid;
	String name;

	public GlobalEvent(String name, String posterUid, Serializable data) {
		super(data);
		this.posterUid = posterUid;
		this.name = name;
	}

	public GlobalEvent(String name, String posterUid) {
		this(name, posterUid, null);
	}

	public GlobalEvent(String name) {
		this(name, null, null);
	}

	public String getPosterUid() {
		return posterUid;
	}

	public String getName() {
		return name;
	}

}
