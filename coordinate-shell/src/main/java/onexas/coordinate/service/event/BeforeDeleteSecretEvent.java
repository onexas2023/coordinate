package onexas.coordinate.service.event;



import onexas.coordinate.common.app.ApplicationEvent;
import onexas.coordinate.model.Secret;

/**
 * 
 * @author Dennis Chen
 *
 */
public class BeforeDeleteSecretEvent extends ApplicationEvent<Secret> {
	private static final long serialVersionUID = 1L;

	public BeforeDeleteSecretEvent(Secret secret) {
		super(secret);
	}

}
