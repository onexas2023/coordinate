package onexas.coordinate.api.v1.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import onexas.coordinate.api.v1.HookApi;
import onexas.coordinate.api.v1.model.UHook;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.Hook;
import onexas.coordinate.model.HookMatch;
import onexas.coordinate.service.HookService;
import onexas.coordinate.web.api.impl.ApiImplBase;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "HookApiImpl")
@Profile({ Env.PROFILE_API_NODE })
public class HookApiImpl extends ApiImplBase implements HookApi {

	@Autowired
	HookService hookService;

	public HookApiImpl() {
		super(HookApi.API_NAME, V1, HookApi.API_URI);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public UHook triggerHook(String type, String uid) {
		return triggerHookWithArgs(type, uid, null);
	}
	
	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public UHook triggerHookWithArgs(String zone, String uid, Map<String, Object> args) {
		Hook m = hookService.trigger(uid, args, new HookMatch().withZone(zone));
		return Jsons.transform(m, UHook.class);
	}

}