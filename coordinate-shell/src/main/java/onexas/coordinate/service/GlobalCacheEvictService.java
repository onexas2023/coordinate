package onexas.coordinate.service;

import java.io.Serializable;

import onexas.coordinate.common.service.CacheEvictService;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface GlobalCacheEvictService {
	
	public static final String GLOBAL_EVENT_CACHE_EVICT = "GlobalCacheEvict";
	
	public static final String UNLESS_RESULT_NULL = CacheEvictService.UNLESS_RESULT_NULL;

	public void clear();
	
	public void clear(String... cacheNames);
	
	public void evict(Serializable cacheKey, String... cacheNames);
}
