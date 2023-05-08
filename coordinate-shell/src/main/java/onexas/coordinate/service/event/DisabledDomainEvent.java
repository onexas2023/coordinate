package onexas.coordinate.service.event;



import onexas.coordinate.common.app.ApplicationEvent;
import onexas.coordinate.model.Domain;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DisabledDomainEvent extends ApplicationEvent<Domain> {
	private static final long serialVersionUID = 1L;

	long createdDateTime;
	
	public DisabledDomainEvent(Domain domain) {
		super(domain);
		createdDateTime = System.currentTimeMillis();
	}
	
	public long getCreatedDateTime() {
		return createdDateTime;
	}

}
