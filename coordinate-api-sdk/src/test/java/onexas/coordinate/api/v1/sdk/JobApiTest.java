package onexas.coordinate.api.v1.sdk;

import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.UJob;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.model.Job;
import onexas.coordinate.model.JobState;
import onexas.coordinate.model.PrincipalPermission;
import onexas.coordinate.service.JobService;

/**
 * 
 * @author Dennis Chen
 *
 */
public class JobApiTest extends CoordinateApiSDKTestBase {

	@Autowired
	JobService jobService;

	@Test
	public void testNoPermission() {
		try {
			ApiClient client = getApiClientWithAuthCreate("someone", "1234", "somerole");

			CoordinateJobApi api = new CoordinateJobApi(client);
			try {
				api.getJob("abcd");
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			client = getApiClientWithAuthCreate(
					new PrincipalPermission(onexas.coordinate.api.v1.JobApi.API_PERMISSION_TARGET,
							onexas.coordinate.api.v1.JobApi.ACTION_VIEW));
			api = new CoordinateJobApi(client);
			try {
				api.getJob("abcd");
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(404, x.getCode());
			}

		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

	@Test
	public void testSimple() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authuser", Strings.randomPassword(10), "authrole",
					new PrincipalPermission("*", "*"));
			CoordinateJobApi api = new CoordinateJobApi(client);

			jobService.prune(System.currentTimeMillis());

			Assert.assertEquals(0, jobService.list(null).size());

			try {
				Assert.assertNull(jobService.get(9999L));
				Assert.fail();
			} catch (NotFoundException x) {
			}

			Assert.assertNull(jobService.find(9999L));

			String queryUid1 = Strings.randomUid();
			String queryUid2 = Strings.randomUid();

			Job job1 = jobService.execute("test", new Callable<List<String>>() {
				@Override
				public List<String> call() throws Exception {
					sleep(1000);
					return Collections.asList("a", "b", "c");
				}
			}, queryUid1);

			Assert.assertNotNull(job1.getCreatedDateTime());
			Assert.assertEquals("test", job1.getSubject());
			Assert.assertTrue(Collections.asSet(JobState.QUEUING, JobState.PROCESSING).contains(job1.getState()));
			Assert.assertNull(job1.getFinishedDateTime());
			Assert.assertNull(job1.getMessage());
			Assert.assertNull(job1.getResultJson());
			Assert.assertNull(job1.getError());
			Assert.assertEquals(queryUid1, job1.getQueryUid());
			sleep1();
			job1 = jobService.find(job1.getId());
			if (job1.getState().equals(JobState.PROCESSING)) {
				Assert.assertNotNull(job1.getStartedDateTime());
			}
			sleep(2000);
			job1 = jobService.find(job1.getId());
			if (job1.getState().equals(JobState.FINISHED)) {
				Assert.assertNotNull(job1.getFinishedDateTime());
				@SuppressWarnings("unchecked")
				List<String> list = job1.objectifyResult(List.class);
				Assert.assertEquals(3, list.size());
				Assert.assertEquals("a", list.get(0));
				Assert.assertEquals("b", list.get(1));
				Assert.assertEquals("c", list.get(2));
				Assert.assertEquals(queryUid1, job1.getQueryUid());
			}
			UJob ujob1 = api.getJob(queryUid1);
			if (ujob1.getState().equals(JobState.FINISHED)) {
				Assert.assertNotNull(ujob1.getFinishedDateTime());
				Assert.assertEquals(queryUid1, ujob1.getQueryUid());
			}

			Job job2 = jobService.execute("test2", new Callable<List<String>>() {
				@Override
				public List<String> call() throws Exception {
					sleep(1000);
					throw new RuntimeException("error Y");
				}
			}, queryUid2);
			Assert.assertNotNull(job2.getCreatedDateTime());
			Assert.assertEquals("test2", job2.getSubject());
			Assert.assertTrue(Collections.asSet(JobState.QUEUING, JobState.PROCESSING).contains(job2.getState()));
			Assert.assertNull(job2.getFinishedDateTime());
			Assert.assertNull(job2.getMessage());
			Assert.assertNull(job2.getResultJson());
			Assert.assertNull(job2.getError());
			Assert.assertEquals(queryUid2, job2.getQueryUid());
			sleep1();
			job2 = jobService.find(job2.getId());
			if (job2.getState().equals(JobState.PROCESSING)) {
				Assert.assertNotNull(job2.getStartedDateTime());
			}
			sleep3();
			job2 = jobService.find(job2.getId());
			if (job2.getState().equals(JobState.FINISHED)) {
				Assert.assertNotNull(job2.getFinishedDateTime());
				Assert.assertEquals(Boolean.TRUE, job2.getError());
				Assert.assertTrue(job2.getMessage().contains("error Y"));
				Assert.assertEquals(null, job2.getResultJson());
				Assert.assertEquals(queryUid2, job2.getQueryUid());
			}
			UJob ujob2 = api.getJob(queryUid2);
			if (ujob2.getState().equals(onexas.coordinate.api.v1.sdk.model.JobState.FINISHED)) {
				Assert.assertNotNull(ujob2.getFinishedDateTime());
				Assert.assertEquals(Boolean.TRUE, ujob2.getError());
				Assert.assertTrue(ujob2.getMessage().contains("error Y"));
				Assert.assertEquals(queryUid2, ujob2.getQueryUid());
			}
			Assert.assertEquals(2, jobService.list(null).size());
			sleep3();
			jobService.prune(System.currentTimeMillis());
			Assert.assertEquals(0, jobService.list(null).size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
}
