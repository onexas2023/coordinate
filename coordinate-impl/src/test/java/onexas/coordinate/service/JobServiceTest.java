package onexas.coordinate.service;

import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.model.Job;
import onexas.coordinate.model.JobState;
import onexas.coordinate.service.test.CoordinateImplTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
@ActiveProfiles(profiles = Env.PROFILE_JOB_NODE)
public class JobServiceTest extends CoordinateImplTestBase {

	@Autowired
	JobService service;

	@Test
	public void testSimple() {
		service.prune(System.currentTimeMillis());

		Assert.assertEquals(0, service.list(null).size());

		try {
			Assert.assertNull(service.get(9999L));
			Assert.fail();
		} catch (NotFoundException x) {
		}

		Assert.assertNull(service.find(9999L));

		Job job1 = service.execute("test", new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				sleep(1000);
				return Collections.asList("a", "b", "c");
			}
		});

		Assert.assertNotNull(job1.getCreatedDateTime());
		Assert.assertEquals("test", job1.getSubject());
		Assert.assertTrue(Collections.asSet(JobState.QUEUING, JobState.PROCESSING).contains(job1.getState()));
		Assert.assertNull(job1.getFinishedDateTime());
		Assert.assertNull(job1.getMessage());
		Assert.assertNull(job1.getResultJson());
		Assert.assertNull(job1.getError());
		Assert.assertNull(job1.getQueryUid());
		sleep1();
		job1 = service.find(job1.getId());
		if (job1.getState().equals(JobState.PROCESSING)) {
			Assert.assertNotNull(job1.getStartedDateTime());
		}
		sleep3();
		job1 = service.find(job1.getId());
		if (job1.getState().equals(JobState.FINISHED)) {
			Assert.assertNotNull(job1.getFinishedDateTime());
			@SuppressWarnings("unchecked")
			List<String> list = job1.objectifyResult(List.class);
			Assert.assertEquals(3, list.size());
			Assert.assertEquals("a", list.get(0));
			Assert.assertEquals("b", list.get(1));
			Assert.assertEquals("c", list.get(2));
			Assert.assertNull(job1.getQueryUid());
		}

		Job job2 = service.execute("test2", new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				sleep(1000);
				throw new RuntimeException("error Y");
			}
		});
		Assert.assertNotNull(job2.getCreatedDateTime());
		Assert.assertEquals("test2", job2.getSubject());
		Assert.assertTrue(Collections.asSet(JobState.QUEUING, JobState.PROCESSING).contains(job2.getState()));
		Assert.assertNull(job2.getFinishedDateTime());
		Assert.assertNull(job2.getMessage());
		Assert.assertNull(job2.getResultJson());
		Assert.assertNull(job2.getError());
		Assert.assertNull(job2.getQueryUid());
		sleep1();
		job2 = service.find(job2.getId());
		if (job2.getState().equals(JobState.PROCESSING)) {
			Assert.assertNotNull(job2.getStartedDateTime());
		}
		sleep3();
		job2 = service.find(job2.getId());
		if (job2.getState().equals(JobState.FINISHED)) {
			Assert.assertNotNull(job2.getFinishedDateTime());
			Assert.assertEquals(Boolean.TRUE, job2.getError());
			Assert.assertTrue(job2.getMessage().contains("error Y"));
			Assert.assertEquals(null, job2.getResultJson());
			Assert.assertNull(job2.getQueryUid());
		}
		Assert.assertEquals(2, service.list(null).size());
		sleep3();
		service.prune(System.currentTimeMillis());
		Assert.assertEquals(0, service.list(null).size());
	}
	
	
	@Test
	public void testSimpleQueryUid() {
		service.prune(System.currentTimeMillis());

		Assert.assertEquals(0, service.list(null).size());

		try {
			Assert.assertNull(service.get(9999L));
			Assert.fail();
		} catch (NotFoundException x) {
		}

		Assert.assertNull(service.find(9999L));

		
		String queryUid1 = Strings.randomUid();
		String queryUid2 = Strings.randomUid();
		
		Job job1 = service.execute("test", new Callable<List<String>>() {
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
		job1 = service.find(job1.getId());
		if (job1.getState().equals(JobState.PROCESSING)) {
			Assert.assertNotNull(job1.getStartedDateTime());
		}
		sleep3();
		job1 = service.find(job1.getId());
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
		job1 = service.findByQueryUid(queryUid1);
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
		

		Job job2 = service.execute("test2", new Callable<List<String>>() {
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
		job2 = service.find(job2.getId());
		if (job2.getState().equals(JobState.PROCESSING)) {
			Assert.assertNotNull(job2.getStartedDateTime());
		}
		sleep3();
		job2 = service.find(job2.getId());
		if (job2.getState().equals(JobState.FINISHED)) {
			Assert.assertNotNull(job2.getFinishedDateTime());
			Assert.assertEquals(Boolean.TRUE, job2.getError());
			Assert.assertTrue(job2.getMessage().contains("error Y"));
			Assert.assertEquals(null, job2.getResultJson());
			Assert.assertEquals(queryUid2, job2.getQueryUid());
		}
		job2 = service.findByQueryUid(queryUid2);
		if (job2.getState().equals(JobState.FINISHED)) {
			Assert.assertNotNull(job2.getFinishedDateTime());
			Assert.assertEquals(Boolean.TRUE, job2.getError());
			Assert.assertTrue(job2.getMessage().contains("error Y"));
			Assert.assertEquals(null, job2.getResultJson());
			Assert.assertEquals(queryUid2, job2.getQueryUid());
		}
		Assert.assertEquals(2, service.list(null).size());
		sleep3();
		service.prune(System.currentTimeMillis());
		Assert.assertEquals(0, service.list(null).size());
	}

	@Test
	public void testEvent() {
		service.prune(System.currentTimeMillis());

		Assert.assertEquals(0, service.list(null).size());

		try {
			Assert.assertNull(service.get(9999L));
			Assert.fail();
		} catch (NotFoundException x) {
		}

		Assert.assertNull(service.find(9999L));

		Job job1 = service.queue(new JobServiceTestListener.Event1(false));

		Assert.assertNotNull(job1.getCreatedDateTime());
		Assert.assertEquals("event1",job1.getSubject());
		Assert.assertTrue(Collections.asSet(JobState.QUEUING, JobState.PROCESSING).contains(job1.getState()));
		Assert.assertNull(job1.getFinishedDateTime());
		Assert.assertNull(job1.getMessage());
		Assert.assertNull(job1.getResultJson());
		Assert.assertNull(job1.getError());
		sleep1();
		job1 = service.find(job1.getId());
		if (job1.getState().equals(JobState.PROCESSING)) {
			Assert.assertNotNull(job1.getStartedDateTime());
		}
		sleep3();
		job1 = service.find(job1.getId());
		if (job1.getState().equals(JobState.FINISHED)) {
			Assert.assertNotNull(job1.getFinishedDateTime());
			Assert.assertNull(job1.getResultJson());
		}

		Job job2 = service.queue(new JobServiceTestListener.Event1(true));
		Assert.assertEquals("event1",job2.getSubject());
		Assert.assertNotNull(job2.getCreatedDateTime());
		Assert.assertTrue(Collections.asSet(JobState.QUEUING, JobState.PROCESSING).contains(job2.getState()));
		Assert.assertNull(job2.getFinishedDateTime());
		Assert.assertNull(job2.getMessage());
		Assert.assertNull(job2.getResultJson());
		Assert.assertNull(job2.getError());
		sleep1();
		job2 = service.find(job2.getId());
		if (job2.getState().equals(JobState.PROCESSING)) {
			Assert.assertNotNull(job2.getStartedDateTime());
		}
		sleep3();
		job2 = service.find(job2.getId());
		if (job2.getState().equals(JobState.FINISHED)) {
			Assert.assertNotNull(job2.getFinishedDateTime());
			Assert.assertEquals(Boolean.TRUE, job2.getError());
			Assert.assertTrue(job2.getMessage().contains("error X"));
			Assert.assertEquals(null, job2.getResultJson());
		}
		Assert.assertEquals(2, service.list(null).size());
		sleep3();
		service.prune(System.currentTimeMillis());
		Assert.assertEquals(0, service.list(null).size());
	}
	
	@Test
	public void testEventQueryUid() {
		service.prune(System.currentTimeMillis());

		Assert.assertEquals(0, service.list(null).size());

		try {
			Assert.assertNull(service.get(9999L));
			Assert.fail();
		} catch (NotFoundException x) {
		}

		Assert.assertNull(service.find(9999L));
		
		String queryUid1 = Strings.randomUid();
		String queryUid2 = Strings.randomUid();

		Job job1 = service.queue(new JobServiceTestListener.Event1(false), queryUid1);

		Assert.assertNotNull(job1.getCreatedDateTime());
		Assert.assertEquals("event1",job1.getSubject());
		Assert.assertTrue(Collections.asSet(JobState.QUEUING, JobState.PROCESSING).contains(job1.getState()));
		Assert.assertNull(job1.getFinishedDateTime());
		Assert.assertNull(job1.getMessage());
		Assert.assertNull(job1.getResultJson());
		Assert.assertNull(job1.getError());
		Assert.assertEquals(queryUid1, job1.getQueryUid());
		sleep1();
		job1 = service.find(job1.getId());
		if (job1.getState().equals(JobState.PROCESSING)) {
			Assert.assertNotNull(job1.getStartedDateTime());
			Assert.assertEquals(queryUid1, job1.getQueryUid());
		}
		sleep3();
		job1 = service.find(job1.getId());
		if (job1.getState().equals(JobState.FINISHED)) {
			Assert.assertNotNull(job1.getFinishedDateTime());
			Assert.assertNull(job1.getResultJson());
			
			Assert.assertEquals(queryUid1, job1.getQueryUid());
		}
		
		job1 = service.findByQueryUid(queryUid1);
		if (job1.getState().equals(JobState.FINISHED)) {
			Assert.assertNotNull(job1.getFinishedDateTime());
			Assert.assertNull(job1.getResultJson());
			
			Assert.assertEquals(queryUid1, job1.getQueryUid());
		}

		Job job2 = service.queue(new JobServiceTestListener.Event1(true), queryUid2);
		Assert.assertEquals("event1",job2.getSubject());
		Assert.assertNotNull(job2.getCreatedDateTime());
		Assert.assertTrue(Collections.asSet(JobState.QUEUING, JobState.PROCESSING).contains(job2.getState()));
		Assert.assertNull(job2.getFinishedDateTime());
		Assert.assertNull(job2.getMessage());
		Assert.assertNull(job2.getResultJson());
		Assert.assertNull(job2.getError());
		sleep1();
		job2 = service.find(job2.getId());
		if (job2.getState().equals(JobState.PROCESSING)) {
			Assert.assertNotNull(job2.getStartedDateTime());
		}
		sleep3();
		job2 = service.find(job2.getId());
		if (job2.getState().equals(JobState.FINISHED)) {
			Assert.assertNotNull(job2.getFinishedDateTime());
			Assert.assertEquals(Boolean.TRUE, job2.getError());
			Assert.assertTrue(job2.getMessage().contains("error X"));
			Assert.assertEquals(null, job2.getResultJson());
			Assert.assertEquals(queryUid2, job2.getQueryUid());
		}
		job2 = service.findByQueryUid(queryUid2);
		if (job2.getState().equals(JobState.FINISHED)) {
			Assert.assertNotNull(job2.getFinishedDateTime());
			Assert.assertEquals(Boolean.TRUE, job2.getError());
			Assert.assertTrue(job2.getMessage().contains("error X"));
			Assert.assertEquals(null, job2.getResultJson());
			Assert.assertEquals(queryUid2, job2.getQueryUid());
		}
		
		Assert.assertEquals(2, service.list(null).size());
		sleep3();
		service.prune(System.currentTimeMillis());
		Assert.assertEquals(0, service.list(null).size());
	}

}
