package onexas.coordinate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.service.AsyncLogService;
import onexas.coordinate.service.LogService;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "AsyncLogServiceImpl")
public class AsyncLogServiceImpl implements AsyncLogService {

	@Autowired
	LogService logService;

	@Override
	@Async
	public void debug(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs) {
		logService.debug(reporter, objUid, objType, subjectUid, subjectType, content, contentArgs);
	}


	@Override
	@Async
	public void info(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs) {
		logService.info(reporter, objUid, objType, subjectUid, subjectType, content, contentArgs);
	}


	@Override
	@Async
	public void warn(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs) {
		logService.warn(reporter, objUid, objType, subjectUid, subjectType, content, contentArgs);
	}

	@Override
	@Async
	public void error(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs) {
		logService.error(reporter, objUid, objType, subjectUid, subjectType, content, contentArgs);
	}

}