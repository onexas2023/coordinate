package onexas.coordinate.common.service;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.springframework.core.task.TaskRejectedException;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface AsyncService {

	public <T> CompletableFuture<T> asyncCall(Callable<T> callable) throws TaskRejectedException;
	
	public void asyncRun(Runnable runnable) throws TaskRejectedException;
	
	public void asyncRunWithDelay(Runnable runnable, long msdelay) throws TaskRejectedException;
}
