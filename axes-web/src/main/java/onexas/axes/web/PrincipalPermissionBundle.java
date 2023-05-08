package onexas.axes.web;
/**
 * 
 * @author Dennis Chen
 *
 */
public class PrincipalPermissionBundle {
	String target;
	String[] actions;

	public PrincipalPermissionBundle() {
	}

	public PrincipalPermissionBundle(String target, String[] actions) {
		this.target = target;
		this.actions = actions;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String[] getActions() {
		return actions;
	}

	public void setActions(String[] actions) {
		this.actions = actions;
	}
}