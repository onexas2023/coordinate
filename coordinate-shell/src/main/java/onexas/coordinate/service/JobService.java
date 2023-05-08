package onexas.coordinate.service;

import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.Job;
import onexas.coordinate.model.JobFilter;
import onexas.coordinate.service.event.JobQueueEvent;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface JobService {

	public ListPage<Job> list(JobFilter filter);

	public Job get(Long id);

	public Job find(Long id);

	public Job findByQueryUid(String queryUid);

	public <V> Job execute(String subject, Callable<V> callable, @Nullable String queryUid);

	public <V> void executeAfterTxCommit(String subject, Callable<V> callable, @Nullable String queryUid);

	public Job queue(JobQueueEvent event, @Nullable String queryUid);

	public void queueAfterTxCommit(JobQueueEvent event, @Nullable String queryUid);

	public <V> Job execute(String subject, Callable<V> callable);

	public <V> void executeAfterTxCommit(String subject, Callable<V> callable);

	public Job queue(JobQueueEvent event);

	public void queueAfterTxCommit(JobQueueEvent event);

	public void prune(long finishedDateTimeBefore);

	public long count();

}
