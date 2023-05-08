package onexas.coordinate.service.impl;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.ApplicationEvent;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.data.util.Fields;
import onexas.coordinate.model.Job;
import onexas.coordinate.model.JobFilter;
import onexas.coordinate.model.JobState;
import onexas.coordinate.model.UserActivityToken;
import onexas.coordinate.service.AsyncExService;
import onexas.coordinate.service.JobExService;
import onexas.coordinate.service.JobService;
import onexas.coordinate.service.UserActivityContext;
import onexas.coordinate.service.event.JobQueueEvent;
import onexas.coordinate.service.impl.dao.JobEntityRepo;
import onexas.coordinate.service.impl.entity.JobEntity;
import onexas.coordinate.service.jms.JobQueueEventMessage;
import onexas.coordinate.service.jms.JobQueueEventSender;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "JobServiceImpl")
public class JobServiceImpl implements JobService, JobExService {

	private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

	@Autowired
	JobEntityRepo jobRepo;

	@Autowired
	JobServiceImplHelper helper;

	@Autowired
	AsyncExService asyncExService;

	@Autowired
	JobQueueEventSender sender;

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@Autowired
	UserActivityContext userActivityContext;

	@Override
	public ListPage<Job> list(JobFilter filter) {
		List<JobEntity> list = new LinkedList<>();
		Direction direction = filter != null && Boolean.TRUE.equals(filter.getSortDesc()) ? Direction.DESC
				: Direction.ASC;
		Sort sort = Sort.by(direction, "createdDateTime");
		Integer pageIndex = filter == null || filter.getPageIndex() == null ? 0 : filter.getPageIndex();
		Integer pageSize = filter == null || filter.getPageSize() == null ? Integer.MAX_VALUE : filter.getPageSize();
		Integer pageTotal = 1;
		Long itemTotal;

		if (filter == null) {
			list = jobRepo.findAll(sort);
			itemTotal = Long.valueOf(list.size());
		} else {
			String sortField = filter.getSortField();

			if (sortField != null) {
				Fields.checkFieldsIn(new String[] { sortField },
						new String[] { "createdDateTime", "startedDateTime", "finishedDateTime" });
				sort = Sort.by(direction, sortField);
			}
			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

			JobEntity ex = Jsons.transform(filter, JobEntity.class);
			ExampleMatcher matcher = Boolean.TRUE.equals(filter.getMatchAny()) ? ExampleMatcher.matchingAny()
					: ExampleMatcher.matchingAll();
			matcher = matcher.withIgnoreNullValues();

			if (Boolean.TRUE.equals(filter.getStrIgnoreCase())) {
				matcher = matcher.withIgnoreCase();
			}
			if (Boolean.TRUE.equals(filter.getStrContaining())) {
				matcher = matcher.withStringMatcher(StringMatcher.CONTAINING);
			}
			Example<JobEntity> example = Example.of(ex, matcher);

			Page<JobEntity> page = jobRepo.findAll(example, pageable);

			list = page.getContent();
			pageTotal = page.getTotalPages();
			itemTotal = page.getTotalElements();
		}
		return new ListPage<>(Jsons.transform(list, new TypeReference<List<Job>>() {
		}), pageIndex, pageSize == null ? list.size() : pageSize, pageTotal, itemTotal);
	}

	@Override
	public Job get(Long id) {
		Optional<JobEntity> o = jobRepo.findById(id);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Job.class);
		}
		throw new NotFoundException("job {} not found", id);
	}

	@Override
	public Job find(Long id) {
		Optional<JobEntity> o = jobRepo.findById(id);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Job.class);
		}
		return null;
	}
	
	@Override
	public Job findByQueryUid(String queryUid) {
		Optional<JobEntity> o = jobRepo.findByQueryUid(queryUid);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Job.class);
		}
		return null;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void prune(long finishedDateTimeBefore) {
		jobRepo.deleteByFinishedDateTime(finishedDateTimeBefore);
	}
	
	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public <V> Job execute(String subject, final Callable<V> callable) {
		return this.execute(subject, callable, null);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public <V> Job execute(String subject, final Callable<V> callable, String queryUid) {
		JobEntity e = helper.createJob(subject, userActivityContext.getTokenIfAny(), queryUid);
		final Long id = e.getId();
		asyncExService.asyncRunAfterTxCommit(() -> {
			helper.updateProcessing(id);
			try {
				V r = callable.call();
				helper.updateFinished(id, r);
			} catch (Exception x) {
				logger.error(x.getMessage(), x);
				helper.updateError(id, x);
			}
		});

		return Jsons.transform(e, Job.class);
	}

	@Service(Env.NS_BEAN + "JobServiceImplHelper")
	public static class JobServiceImplHelper {
		@Autowired
		JobEntityRepo jobRepo;

		@Value("${coordinate.node}")
		private String node;

		@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
		public JobEntity createJob(String subject, UserActivityToken token, String queryUid) {
			JobEntity e = new JobEntity();
			e.setSubject(subject);
			e.setNode(node);
			if (token != null) {
				e.setRequestUid(token.getRequestUid());
			}
			e.setState(JobState.QUEUING);
			e.setQueryUid(queryUid);
			e = jobRepo.save(e);
			return e;
		}

		@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
		public void updateError(Long id, Exception x) {
			Optional<JobEntity> o = jobRepo.findById(id);
			if (o.isPresent()) {
				JobEntity e = o.get();
				e.setState(JobState.FINISHED);
				e.setFinishedDateTime(System.currentTimeMillis());
				e.setMessage(Strings.format("{} : {}", x.getClass().getName(), x.getMessage()));
				e.setError(Boolean.TRUE);
				e.setResultJson(null);
				jobRepo.flush();
			}
		}

		@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
		public <V> void updateFinished(Long id, V result) {
			Optional<JobEntity> o = jobRepo.findById(id);
			if (o.isPresent()) {
				JobEntity e = o.get();
				e.setState(JobState.FINISHED);
				e.setFinishedDateTime(System.currentTimeMillis());
				e.setMessage("ok");
				e.setError(Boolean.FALSE);
				try {
					e.setResultJson(result == null ? null : Jsons.jsonify(result));
				} catch (Exception x) {
					logger.warn("cann't jsonify object {}" + result);
				}
				jobRepo.flush();
			}
		}

		@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
		public void updateProcessing(Long id) {
			Optional<JobEntity> o = jobRepo.findById(id);
			if (o.isPresent()) {
				JobEntity e = o.get();
				e.setState(JobState.PROCESSING);
				e.setStartedDateTime(System.currentTimeMillis());
				jobRepo.flush();
			}
		}

	}

	@Override
	public Job queue(JobQueueEvent event) {
		return this.queue(event, null);
	}
	@Override
	public Job queue(JobQueueEvent event, String queryUid) {
		JobEntity e = helper.createJob(event.getSubject(), userActivityContext.getTokenIfAny(), queryUid);
		sender.sendQueue(new JobQueueEventMessage(e.getId(), event, userActivityContext.getTokenIfAny()));
		return Jsons.transform(e, Job.class);
	}

	@Override
	public void onReceive(JobQueueEventMessage message) {
		Long id = message.getJobId();
		JobQueueEvent event = message.getEvent();
		helper.updateProcessing(id);
		UserActivityToken prev = userActivityContext.follow(message.getUserActivityToken());
		try {
			eventPublisher.publishEvent(event);
			helper.updateFinished(id, null);
		} catch (Exception x) {
			logger.error(x.getMessage(), x);
			helper.updateError(id, x);
		} finally {
			userActivityContext.unfollow(prev);
		}
	}

	@Override
	public long count() {
		return jobRepo.count();
	}

	@Override
	public void queueAfterTxCommit(JobQueueEvent event) {
		this.queueAfterTxCommit(event, null);
	}
	
	@Override
	public void queueAfterTxCommit(JobQueueEvent event, String queryUid) {
		if (!TransactionSynchronizationManager.isActualTransactionActive()) {
			logger.warn("calling after tx commit method with no-tx will not trigger the event");
		}
		eventPublisher.publishEvent(new PostTxJobQueueEvent(event, userActivityContext.getTokenIfAny(), queryUid));
	}

	@Override
	public <V> void executeAfterTxCommit(String subject, Callable<V> callable) {
		this.executeAfterTxCommit(subject, callable, null);
	}
	
	@Override
	public <V> void executeAfterTxCommit(String subject, Callable<V> callable, String queryUid) {
		if (!TransactionSynchronizationManager.isActualTransactionActive()) {
			logger.warn("calling after tx commit method with no-tx will not trigger the callable");
		}
		eventPublisher.publishEvent(new PostTxCallableEvent(subject, callable, userActivityContext.getTokenIfAny(), queryUid));
	}

	public static class PostTxJobQueueEvent extends ApplicationEvent<JobQueueEvent> {
		private static final long serialVersionUID = 1L;

		final UserActivityToken userActivityToken;
		
		final String queryUid;

		public PostTxJobQueueEvent(JobQueueEvent source, UserActivityToken userActivityToken, String queryUid) {
			super(source);
			this.userActivityToken = userActivityToken;
			this.queryUid = queryUid;
		}
	}

	public static class PostTxCallableEvent extends ApplicationEvent<Serializable> {
		private static final long serialVersionUID = 1L;
		
		final String subject;

		final Callable<?> callable;

		final UserActivityToken userActivityToken;
		
		final String queryUid;

		public PostTxCallableEvent(String subject, Callable<?> callable, UserActivityToken userActivityToken, String queryUid) {
			super(null);
			this.subject = subject;
			this.callable = callable;
			this.userActivityToken = userActivityToken;
			this.queryUid = queryUid;
		}
	}

	@TransactionalEventListener
	public void handle(PostTxJobQueueEvent event) {
		UserActivityToken prev = userActivityContext.follow(event.userActivityToken);
		try {
			AppContext.bean(JobServiceImpl.class).queue(event.getData());
		} finally {
			userActivityContext.unfollow(prev);
		}
	}

	@TransactionalEventListener
	public void handle(PostTxCallableEvent event) {
		UserActivityToken prev = userActivityContext.follow(event.userActivityToken);
		try {
			AppContext.bean(JobServiceImpl.class).execute(event.subject, event.callable, event.queryUid);
		} finally {
			userActivityContext.unfollow(prev);
		}
	}

}