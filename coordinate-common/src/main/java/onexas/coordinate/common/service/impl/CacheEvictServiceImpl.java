package onexas.coordinate.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.service.CacheEvictService;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "CacheEvictServiceImpl")
public class CacheEvictServiceImpl implements CacheEvictService {

	@Autowired
	CacheManager cacheManager;

	@Override
	public void clear() {
		for (String name : cacheManager.getCacheNames()) {
			cacheManager.getCache(name).clear();
		}
	}

	@Override
	public void clear(String... names) {
		for (String name : names) {
			Cache cache = cacheManager.getCache(name);
			if (cache != null) {
				cache.clear();
			}
		}
	}

	@Override
	public void evict(Object key, String... names) {
		for (String name : names) {
			Cache cache = cacheManager.getCache(name);
			if (cache != null) {
				cache.evict(key);
			}
		}
	}

}