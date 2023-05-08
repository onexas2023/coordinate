package onexas.coordinate.service;

import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.Log;
import onexas.coordinate.model.LogCreate;
import onexas.coordinate.model.LogFilter;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface LogService {

	public ListPage<Log> list(LogFilter filter);

	public Log get(Long id);

	public Log find(Long id);

	public void delete(Long id, boolean quiet);

	public Log create(LogCreate logCreate);

	public void prune(int levelLe, long dateTimeBefore);

//	public Log debug(String reporter, String objUid, String objType, String subjectUid, String subjectType,
//			String content, Object... contentArgs);

	public Log debug(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs);

//	public Log info(String reporter, String objUid, String objType, String subjectUid, String subjectType,
//			String content, Object... contentArgs);

	public Log info(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs);

//	public Log warn(String reporter, String objUid, String objType, String subjectUid, String subjectType,
//			String content, Object... contentArgs);

	public Log warn(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs);

//	public Log error(String reporter, String objUid, String objType, String subjectUid, String subjectType,
//			String content, Object... contentArgs);

	public Log error(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs);
}
