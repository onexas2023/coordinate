package onexas.coordinate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.data.Locker;
import onexas.coordinate.model.Log;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "Schedule")
@Profile(Env.PROFILE_TIMER_NODE)
public class Schedule {

	@Autowired
	Locker locker;

	@Autowired
	LogService logService;

	@Autowired
	AuthenticationTokenService authenticationTokenService;

	@Autowired
	InfoCacheService infoCacheService;
	
	@Autowired
	GlobalCacheEvictService cacheEvictService;

	@Autowired
	JobService jobService;

	@Value("${coordinate.timeout.finishedJobPruneTimeout}")
	String finishedJobPruneTimeout;

	@Value("${coordinate.timeout.debugLogPruneTimeout}")
	String debugLogPruneTimeout;

	private String getLockPrefix() {
		return "coordinate.schedule";
	}

	@Value("${coordinate.schedule.minimumLockTime}")
	String minimumLockTime;

	// second, minute, hour, day of month, month, day(s) of week
	@Scheduled(cron = "${coordinate.schedule.pruneJob}")
	public void pruneJob() {
		locker.runOrSkip(getLockPrefix() + ".pruneJob", () -> {
			long timeBefore = Strings.parseMillisecond(finishedJobPruneTimeout);

			jobService.prune(System.currentTimeMillis() - timeBefore);
			return null;
		}, Strings.parseMillisecond(minimumLockTime));
	}

	@Scheduled(cron = "${coordinate.schedule.pruneAuthenticationToken}")
	public void pruneAuthenticationToken() {
		locker.runOrSkip(getLockPrefix() + ".pruneAuthenticationToken", () -> {
			authenticationTokenService.prune(System.currentTimeMillis());
			return null;
		}, Strings.parseMillisecond(minimumLockTime));
	}

	@Scheduled(cron = "${coordinate.schedule.pruneLog}")
	public void pruneLog() {
		locker.runOrSkip(getLockPrefix() + ".pruneLog", () -> {
			long timeBefore = Strings.parseMillisecond(debugLogPruneTimeout);
			logService.prune(Log.DEBUG, System.currentTimeMillis() - timeBefore);
			return null;
		}, Strings.parseMillisecond(minimumLockTime));
	}

	@Scheduled(cron = "${coordinate.schedule.pruneInfoCache}")
	public void pruneInfoCache() {
		locker.runOrSkip(getLockPrefix() + ".pruneInfoCache", () -> {
			infoCacheService.prune(System.currentTimeMillis());
			return null;
		}, Strings.parseMillisecond(minimumLockTime));
	}
	
	@Scheduled(cron = "${coordinate.schedule.clearCache}")
	public void clearCache() {
		locker.runOrSkip(getLockPrefix() + ".clearCache", () -> {
			System.out.println(">>>>daily clear cache");
			cacheEvictService.clear();
			return null;
		}, Strings.parseMillisecond(minimumLockTime));
	}
}