package onexas.coordinate.api;

import java.util.LinkedHashSet;
import java.util.Set;

import onexas.coordinate.common.lang.Collections;

/**
 * 
 * @author Dennis Chen
 *
 */
public class GrantPermission {

	String target;

	Set<String> actions;
	
	boolean matchAll = true;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Set<String> getActions() {
		return actions;
	}

	public void setActions(Set<String> actions) {
		this.actions = actions;
	}

	public boolean isMatchAll() {
		return matchAll;
	}

	public void setMatchAll(boolean matchAll) {
		this.matchAll = matchAll;
	}
	
	public GrantPermission withTarget(String target) {
		this.target = target;
		return this;
	}
	
	public GrantPermission withActions(String... actions) {
		this.actions = Collections.asSet(actions);
		return this;
	}

	public GrantPermission withMatchAll(boolean matchAll) {
		this.matchAll = matchAll;
		return this;
	}
	
	public void addToActions(String action) {
		if(this.actions == null) {
			this.actions = new LinkedHashSet<>();
		}
		this.actions.add(action);
	}

}
