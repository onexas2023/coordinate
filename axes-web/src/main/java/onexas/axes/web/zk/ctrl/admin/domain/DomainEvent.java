package onexas.axes.web.zk.ctrl.admin.domain;

import org.zkoss.zk.ui.event.Event;

import onexas.coordinate.api.v1.sdk.model.ADomain;
/**
 * 
 * @author Dennis Chen
 *
 */
public class DomainEvent extends Event{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DomainEventType type;
	ADomain domain;
	
	public DomainEvent(DomainEventType type, ADomain domain) {
		super("onDomainEvent");
		this.type = type;
		this.domain = domain;
	}

	public ADomain getDomain() {
		return domain;
	}

	public void setDomain(ADomain domain) {
		this.domain = domain;
	}

	public DomainEventType getType() {
		return type;
	}
	

}