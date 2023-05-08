package onexas.coordinate.service;

import java.util.Map;

import javax.annotation.Nullable;

import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.Hook;
import onexas.coordinate.model.HookCreate;
import onexas.coordinate.model.HookFilter;
import onexas.coordinate.model.HookMatch;
import onexas.coordinate.model.HookUpdate;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface HookService {

	//TODO provide security filter in zone
	public static final String ZONE_PUBLIC = "public";
	public static final String ZONE_INTERNAL = "internal";
	
	public static final String OWNER_USER = "user";

	public ListPage<Hook> list(@Nullable HookFilter filter);

	public Hook get(String uid);

	public Hook find(String uid);

	public void delete(String uid, boolean quiet);

	public Hook create(HookCreate hookCreate);

	public Hook update(String uid, HookUpdate hookUpdate);

	public Hook trigger(String uid);
	
	public Hook trigger(String uid, @Nullable Map<String, Object> args, @Nullable HookMatch match);

}
