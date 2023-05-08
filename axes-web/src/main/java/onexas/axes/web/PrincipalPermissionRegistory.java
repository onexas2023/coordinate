package onexas.axes.web;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import onexas.coordinate.api.v1.sdk.model.APrincipalPermission;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Collections;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "axes.PrincipalPermissionRegistory")
public class PrincipalPermissionRegistory {

	private static final Logger logger = LoggerFactory.getLogger(PrincipalPermissionRegistory.class);

	Map<String, PrincipalPermissionBundle> entries = Collections.newConcurrentMap();
	
	public List<APrincipalPermission> list() {
		List<APrincipalPermission> fs = new LinkedList<>();
		List<PrincipalPermissionBundle> l = new LinkedList<>(entries.values());
		java.util.Collections.sort(fs, (o1, o2) -> {
			return o1.getTarget().compareTo(o2.getTarget());
		});
		for(PrincipalPermissionBundle b:l) {
			for(String a:b.actions) {
				fs.add(new APrincipalPermission().target(b.getTarget()).action(a));
			}
		}
		return fs;
	}

	public void register(PrincipalPermissionBundle bundle) {
		entries.put(bundle.getTarget(), bundle);
		logger.info("Register principal permission, target {}, action {}", bundle.getTarget(), Collections.asSet(bundle.getActions()));
	}

}
