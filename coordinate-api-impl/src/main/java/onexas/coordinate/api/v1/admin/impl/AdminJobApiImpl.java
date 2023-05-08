package onexas.coordinate.api.v1.admin.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.admin.AdminJobApi;
import onexas.coordinate.api.v1.admin.model.AJob;
import onexas.coordinate.api.v1.admin.model.AJobFilter;
import onexas.coordinate.api.v1.admin.model.AJobListPage;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.model.Job;
import onexas.coordinate.service.JobService;
import onexas.coordinate.web.api.impl.ApiImplBase;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "AdminJobApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantPermissions(@GrantPermission(target = AdminJobApi.API_PERMISSION_TARGET,
		action = { AdminJobApi.ACTION_VIEW, AdminJobApi.ACTION_MODIFY, AdminJobApi.ACTION_ADMIN }))
public class AdminJobApiImpl extends ApiImplBase implements AdminJobApi {
	@Autowired
	JobService jobService;

	public AdminJobApiImpl() {
		super(AdminJobApi.API_NAME, V1, AdminJobApi.API_URI);
	}

	@Override
	public AJob getJob(Long id, Boolean find) {
		return Jsons.transform(Boolean.TRUE.equals(find) ? jobService.find(id) : jobService.get(id), AJob.class);
	}

	@Override
	public AJobListPage listJob(AJobFilter filter) {
		ListPage<Job> r = jobService.list(filter);
		return new AJobListPage(Jsons.transform(r.getItems(), new TypeReference<List<AJob>>() {
		}), r.getPageIndex(), r.getPageSize(), r.getPageTotal(), r.getItemTotal());
	}
}