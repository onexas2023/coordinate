package onexas.coordinate.api.v1.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

import onexas.coordinate.api.security.GrantAuthentication;
import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.JobApi;
import onexas.coordinate.api.v1.model.UJob;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.model.Job;
import onexas.coordinate.service.JobService;
import onexas.coordinate.web.api.impl.ApiImplBase;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "JobApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantAuthentication
@GrantPermissions(@GrantPermission(target = JobApi.API_PERMISSION_TARGET, action = { JobApi.ACTION_VIEW,
		JobApi.ACTION_MODIFY, JobApi.ACTION_ADMIN }))
public class JobApiImpl extends ApiImplBase implements JobApi {

	@Autowired
	JobService jobService;

	public JobApiImpl() {
		super(JobApi.API_NAME, V1, JobApi.API_URI);
	}

	@Override
	public UJob getJob(String queryUid) {
		Job job = jobService.findByQueryUid(queryUid);
		if (job == null) {
			throw new NotFoundException("job not found by query-uid {}", queryUid);
		}
		return Jsons.transform(job, UJob.class);
	}

}