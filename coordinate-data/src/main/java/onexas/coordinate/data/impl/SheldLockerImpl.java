package onexas.coordinate.data.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Randoms;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.data.Constants;
import onexas.coordinate.data.Locker;
import onexas.coordinate.data.LockerException;
/**
 * 
 * @author Dennis Chen
 *
 */
@Profile("!" + Constants.PROFILE_DISABLE_DATA_SOURCE)
@Component(Env.NS_BEAN + "SheldLockerImpl")
public class SheldLockerImpl implements Locker {

	@Value("${coordinate-data.locker.defaultTimeout}")
	String defaultTimeout;

	@Value("${coordinate-data.locker.waitfoxFix}")
	String waitfoxFix;

	@Value("${coordinate-data.locker.waitforVariety}")
	String waitforVariety;

	@Autowired
	LockProvider lockProvider;

	public <V> V runOrWait(String name, Callable<V> callable) throws LockerException {
		return runOrWait(name, callable, Strings.parseMillisecond(defaultTimeout));
	}

	/**
	 * run the callable with a lock on name. if the lock can't be acquired (e.g.
	 * lock by another process), it waits and try to acquired later.
	 * 
	 * @return the return value of callable
	 */
	public <V> V runOrWait(String name, Callable<V> callable, long timeout) throws LockerException {
		SimpleLock lock = null;
		Instant now = Instant.now();
		Instant until = now.plusMillis(timeout);
		long fix = Strings.parseMillisecond(waitfoxFix);
		long variety = Strings.parseMillisecond(waitforVariety);

		LockConfiguration lockConfig = new LockConfiguration(name, until);
		while (true) {
			//Tx Exception on HA Schedule Task
			//it happens in galera multiple nodes mode
			Optional<SimpleLock> opt = null;
			try {
				opt = lockProvider.lock(lockConfig);
			}catch(TransactionSystemException e) { 
				//treat just like lock is fail
				opt = Optional.empty();
			}catch(PessimisticLockingFailureException e) {
				//Unexpected error occurred in scheduled task in ha galera
				opt = Optional.empty();
			}
			
			if (opt.isPresent()) {
				lock = opt.get();
				try {
					return callable.call();
				} catch (Exception e) {
					throw new LockerException(e.getMessage(), e);
				} finally {
					lock.unlock();
					lock = null;
				}
			}
			if (Instant.now().isAfter(until)) {
				throw new LockerException("lock timeout, the call was not executed");
			}
			try {
				Thread.sleep(fix + Randoms.random.nextInt((int) variety));
			} catch (InterruptedException e) {
				throw new LockerException("lock wait, interrupted, the call was not executed", e);
			}
		}
	}

	public <V> V runOrSkip(String name, Callable<V> callable) {
		return runOrSkip(name, callable, -1, Strings.parseMillisecond(defaultTimeout));
	}

	public <V> V runOrSkip(String name, Callable<V> callable, long minmunSkipTime) {
		return runOrSkip(name, callable, minmunSkipTime, Strings.parseMillisecond(defaultTimeout));
	}

	/**
	 * run the callable with a lock on name. if the lock can't be acquired (e.g.
	 * lock by another process), it just skip the callable
	 * 
	 * @return the return value of callable or null if lock can't be acquired
	 */
	public <V> V runOrSkip(String name, Callable<V> callable, long minmunSkipTime, long maximunTimeout) {
		SimpleLock lock = null;
		Instant now = Instant.now();
		Instant until = now.plusMillis(maximunTimeout);

		LockConfiguration lockConfig = new LockConfiguration(name, until);
		
		//Tx Exception on HA Schedule Task
		//it happens in galera multiple nodes mode
		Optional<SimpleLock> opt = null;
		try {
			opt = lockProvider.lock(lockConfig);
		}catch(TransactionSystemException e) { 
			//treat just like lock is fail
			opt = Optional.empty();
		}
		
		V result = null;
		if (opt.isPresent()) {
			lock = opt.get();
			try {
				result = callable.call();
			} catch (Exception e) {
				throw new LockerException(e.getMessage(), e);
			} finally {
				lock.unlock();
				lock = null;
			}
		}
		if (minmunSkipTime >= 0) {
			minmunSkipTime = minmunSkipTime - (Instant.now().toEpochMilli() - now.toEpochMilli());
			if (minmunSkipTime >= 0) {
				try {
					Thread.sleep(minmunSkipTime);
				} catch (InterruptedException e) {
				}
			}
		}

		return result;
	}
}
