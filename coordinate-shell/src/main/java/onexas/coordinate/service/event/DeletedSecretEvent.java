package onexas.coordinate.service.event;



import onexas.coordinate.common.app.ApplicationEvent;
import onexas.coordinate.model.Secret;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DeletedSecretEvent extends ApplicationEvent<Secret> {
	private static final long serialVersionUID = 1L;

	public DeletedSecretEvent(Secret secret) {
		super(secret);
	}
}
