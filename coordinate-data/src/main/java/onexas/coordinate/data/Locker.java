package onexas.coordinate.data;

import java.util.concurrent.Callable;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface Locker {

	public <V> V runOrWait(String name, Callable<V> callable) throws LockerException;
	/**
	 * run the callable with a lock on name. if the lock can't be acquired (e.g.
	 * lock by another process), it waits and try to acquired later.
	 * 
	 * @return the return value of callable
	 */
	public <V> V runOrWait(String name, Callable<V> callable, long timeout) throws LockerException;

	public <V> V runOrSkip(String name, Callable<V> callable);

	public <V> V runOrSkip(String name, Callable<V> callable, long minmunSkipTime);

	/**
	 * run the callable with a lock on name. if the lock can't be acquired (e.g.
	 * lock by another process), it just skip the callable
	 * 
	 * @return the return value of callable or null if lock can't be acquired
	 */
	public <V> V runOrSkip(String name, Callable<V> callable, long minmunSkipTime, long maximunTimeout);
}
