package onexas.coordinate.service.impl;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import onexas.coordinate.common.app.ApplicationEvent;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.service.AsyncService;
import onexas.coordinate.model.UserActivityToken;
import onexas.coordinate.service.AsyncExService;
import onexas.coordinate.service.UserActivityContext;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "AsyncExServiceImpl")
public class AsyncExServiceImpl implements AsyncExService {
	
	private static final Logger logger = LoggerFactory.getLogger(AsyncExServiceImpl.class);

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@Autowired
	AsyncService asyncService;

	@Autowired
	UserActivityContext userActivityContext;

	@Override
	public void asyncRunAfterTxCommit(Runnable runnable) {
		if(!TransactionSynchronizationManager.isActualTransactionActive()) {
			logger.warn("calling after tx commit method with no-tx will not trigger the runnable");
		}
		eventPublisher.publishEvent(new PostTxCommitEvent(runnable, null, userActivityContext.getTokenIfAny()));
	}

	@Override
	public void asyncRunWithDelayAfterTxCommit(Runnable runnable, long msdelay) {
		if(!TransactionSynchronizationManager.isActualTransactionActive()) {
			logger.warn("calling after tx commit method with no-tx will not trigger the runnable");
		}
		eventPublisher.publishEvent(new PostTxCommitEvent(runnable, msdelay, userActivityContext.getTokenIfAny()));
	}
	
	@Override
	public void asyncRunAfterTxRollback(Runnable runnable) {
		if(!TransactionSynchronizationManager.isActualTransactionActive()) {
			logger.warn("calling after tx commit method with no-tx will not trigger the runnable");
		}
		eventPublisher.publishEvent(new PostTxRollbackEvent(runnable, null, userActivityContext.getTokenIfAny()));
	}

	@Override
	public void asyncRunWithDelayAfterTxRollback(Runnable runnable, long msdelay) {
		if(!TransactionSynchronizationManager.isActualTransactionActive()) {
			logger.warn("calling after tx commit method with no-tx will not trigger the runnable");
		}
		eventPublisher.publishEvent(new PostTxRollbackEvent(runnable, msdelay, userActivityContext.getTokenIfAny()));
	}

	public static class PostTxCommitEvent extends ApplicationEvent<Serializable> {
		private static final long serialVersionUID = 1L;

		Runnable runnable;
		Long delay;
		UserActivityToken userActivityToken;

		public PostTxCommitEvent(Runnable runnable, Long delay, UserActivityToken userActivityToken) {
			super(null);
			this.runnable = runnable;
			this.delay = delay;
			this.userActivityToken = userActivityToken;
		}
	}
	
	public static class PostTxRollbackEvent extends ApplicationEvent<Serializable> {
		private static final long serialVersionUID = 1L;

		Runnable runnable;
		Long delay;
		UserActivityToken userActivityToken;

		public PostTxRollbackEvent(Runnable runnable, Long delay, UserActivityToken userActivityToken) {
			super(null);
			this.runnable = runnable;
			this.delay = delay;
			this.userActivityToken = userActivityToken;
		}
	}

	@TransactionalEventListener
	public void handle(PostTxCommitEvent event) {
		if (event.delay == null) {
			asyncService.asyncCall(() -> {
				UserActivityToken prev = userActivityContext.follow(event.userActivityToken);
				try {
					event.runnable.run();
					return null;
				} finally {
					userActivityContext.unfollow(prev);
				}
			});
		} else {
			asyncService.asyncRunWithDelay(() -> {
				UserActivityToken prev = userActivityContext.follow(event.userActivityToken);
				try {
					event.runnable.run();
				} finally {
					userActivityContext.unfollow(prev);
				}
			}, event.delay);
		}
	}
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
	public void handle(PostTxRollbackEvent event) {
		if (event.delay == null) {
			asyncService.asyncCall(() -> {
				UserActivityToken prev = userActivityContext.follow(event.userActivityToken);
				try {
					event.runnable.run();
					return null;
				} finally {
					userActivityContext.unfollow(prev);
				}
			});
		} else {
			asyncService.asyncRunWithDelay(() -> {
				UserActivityToken prev = userActivityContext.follow(event.userActivityToken);
				try {
					event.runnable.run();
				} finally {
					userActivityContext.unfollow(prev);
				}
			}, event.delay);
		}
	}

	@Override
	public <T> CompletableFuture<T> asyncCall(Callable<T> callable) throws TaskRejectedException {
		UserActivityToken token = userActivityContext.getTokenIfAny();
		return asyncService.asyncCall(() -> {
			UserActivityToken prev = userActivityContext.follow(token);
			try {
				return callable.call();
			} finally {
				userActivityContext.unfollow(prev);
			}

		});
	}

	@Override
	public void asyncRunWithDelay(Runnable runnable, long msdelay) throws TaskRejectedException {
		UserActivityToken token = userActivityContext.getTokenIfAny();
		asyncService.asyncRunWithDelay(() -> {
			UserActivityToken prev = userActivityContext.follow(token);
			try {
				runnable.run();
			} finally {
				userActivityContext.unfollow(prev);
			}
		}, msdelay);
	}
	
	@Override
	public void asyncRun(Runnable runnable) throws TaskRejectedException {
		UserActivityToken token = userActivityContext.getTokenIfAny();
		asyncService.asyncRun(() -> {
			UserActivityToken prev = userActivityContext.follow(token);
			try {
				runnable.run();
			} finally {
				userActivityContext.unfollow(prev);
			}
		});
	}

}
