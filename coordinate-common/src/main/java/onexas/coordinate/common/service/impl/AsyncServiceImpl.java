package onexas.coordinate.common.service.impl;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.service.AsyncService;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "AsyncServiceImpl")
public class AsyncServiceImpl implements AsyncService {

	private static final Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);

	@Override
	@Async(AppContext.TASK_EXECUTOR_NAME)
	public <T> CompletableFuture<T> asyncCall(Callable<T> call) throws TaskRejectedException{
		T result = null;
		try {
			result = call.call();
		} catch (Exception e) {
			logger.error(Strings.format("Async Error: {}", e.getMessage()), e);
			throw new RuntimeException(e.getMessage(), e);
		}
		return CompletableFuture.completedFuture(result);
	}
	
	@Override
	@Async(AppContext.TASK_EXECUTOR_NAME)
	public void asyncRun(Runnable runnable) throws TaskRejectedException{
		runnable.run();
	}

	@Override
	public void asyncRunWithDelay(Runnable run, long msdelay) throws TaskRejectedException{
		AppContext.scheduler().schedule(run, new Date(System.currentTimeMillis() + msdelay));
	}
}