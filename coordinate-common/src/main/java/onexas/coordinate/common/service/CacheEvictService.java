package onexas.coordinate.common.service;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface CacheEvictService {
	
	public static final String UNLESS_RESULT_NULL = "#result == null";

	public void clear();
	
	public void clear(String... names);
	
	public void evict(Object key, String... names);
}
