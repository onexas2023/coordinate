package onexas.coordinate.api.v1.admin.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.admin.AdminLogApi;
import onexas.coordinate.api.v1.admin.model.ALog;
import onexas.coordinate.api.v1.admin.model.ALogFilter;
import onexas.coordinate.api.v1.admin.model.ALogListPage;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.Log;
import onexas.coordinate.service.LogService;
import onexas.coordinate.web.api.impl.ApiImplBase;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "AdminLogApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantPermissions(@GrantPermission(target = AdminLogApi.API_PERMISSION_TARGET,
		action = { AdminLogApi.ACTION_VIEW, AdminLogApi.ACTION_MODIFY, AdminLogApi.ACTION_ADMIN }))
public class AdminLogApiImpl extends ApiImplBase implements AdminLogApi {
	@Autowired
	LogService logService;

	public AdminLogApiImpl() {
		super(AdminLogApi.API_NAME, V1, AdminLogApi.API_URI);
	}

	@Override
	public ALog getLog(Long id, Boolean find) {
		return Jsons.transform(Boolean.TRUE.equals(find) ? logService.find(id) : logService.get(id), ALog.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminLogApi.API_PERMISSION_TARGET,
			action = { AdminLogApi.ACTION_MODIFY, AdminLogApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response deleteLog(Long id, Boolean quiet) {
		logService.delete(id, Boolean.TRUE.equals(quiet) ? true : false);
		return new Response();
	}

	@Override
	public ALogListPage listLog(ALogFilter filter) {
		ListPage<Log> r = logService.list(filter);
		return new ALogListPage(Jsons.transform(r.getItems(), new TypeReference<List<ALog>>() {
		}), r.getPageIndex(), r.getPageSize(), r.getPageTotal(), r.getItemTotal());
	}
}