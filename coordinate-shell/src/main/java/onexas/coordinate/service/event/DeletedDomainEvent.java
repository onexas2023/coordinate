package onexas.coordinate.service.event;



import onexas.coordinate.common.app.ApplicationEvent;
import onexas.coordinate.model.Domain;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DeletedDomainEvent extends ApplicationEvent<Domain> {
	private static final long serialVersionUID = 1L;

	public DeletedDomainEvent(Domain domain) {
		super(domain);
	}
}
