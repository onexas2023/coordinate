package onexas.axes.web.zk.ctrl.admin.secret;

import org.zkoss.zk.ui.event.Event;

import onexas.coordinate.api.v1.sdk.model.ASecret;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SecretEvent extends Event {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	SecretEventType type;
	ASecret secret;

	public SecretEvent(SecretEventType type, ASecret secret) {
		super("onSecretEvent");
		this.type = type;
		this.secret = secret;
	}

	public ASecret getSecret() {
		return secret;
	}

	public void setSecret(ASecret secret) {
		this.secret = secret;
	}

	public SecretEventType getType() {
		return type;
	}

}