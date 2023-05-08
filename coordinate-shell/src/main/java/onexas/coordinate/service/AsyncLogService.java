package onexas.coordinate.service;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface AsyncLogService {

//	public void debug(String reporter, String objUid, String objType, String subjectUid, String subjectType,
//			String content, Object... contentArgs);

	public void debug(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs);

//	public void info(String reporter, String objUid, String objType, String subjectUid, String subjectType,
//			String content, Object... contentArgs);

	public void info(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs);

//	public void warn(String reporter, String objUid, String objType, String subjectUid, String subjectType,
//			String content, Object... contentArgs);

	public void warn(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs);

//	public void error(String reporter, String objUid, String objType, String subjectUid, String subjectType,
//			String content, Object... contentArgs);

	public void error(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs);
}
