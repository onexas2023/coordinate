package onexas.coordinate.service;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.springframework.core.task.TaskRejectedException;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface AsyncExService{
	
	public <T> CompletableFuture<T> asyncCall(Callable<T> callable) throws TaskRejectedException;
	
	public void asyncRun(Runnable runnable) throws TaskRejectedException;
	
	public void asyncRunWithDelay(Runnable runnable, long msdelay) throws TaskRejectedException;

	public void asyncRunAfterTxCommit(Runnable runnable);
	
	public void asyncRunWithDelayAfterTxCommit(Runnable runnable, long msdelay);
	
	public void asyncRunAfterTxRollback(Runnable runnable);
	
	public void asyncRunWithDelayAfterTxRollback(Runnable runnable, long msdelay);
}
