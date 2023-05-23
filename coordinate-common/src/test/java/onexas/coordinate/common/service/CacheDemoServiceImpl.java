package onexas.coordinate.common.service;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.err.NotFoundException;

@Component
@CacheConfig(cacheNames = "cache-test")
public class CacheDemoServiceImpl implements CacheDemoService{
	
	int callGet;

	Map<String, Item> items = new LinkedHashMap<>();
	
	
	public Item add(String code, String name) {
		Item item;
		items.put(code, item = new Item(code, name));
		return item;
	}
	
	@CacheEvict
	public Item remove(String code) {
		return items.remove(code);
	}
	
	@Cacheable(key = "#p0", unless = CacheEvictService.UNLESS_RESULT_NULL)
	public Item getByArg0(String code, String uselesskey) {
		return get(code);
		
	}
	
	@Cacheable(key = "#p0 + #p1", unless = CacheEvictService.UNLESS_RESULT_NULL)
	public Item getBy2Key(String co, String de) {
		return get(co+de);
		
	}
	
	@Cacheable(unless = CacheEvictService.UNLESS_RESULT_NULL)
	public Item get(String code) {
		System.out.println(">>>>get "+code);
		callGet++;
		return items.get(code);
	}
	
	public int getCallGet() {
		return callGet;
	}
	
	@CacheEvict(key = "#p0")
	public Item update(String code, String name) {
		Item item = items.get(code);
		if(item!=null) {
			item.name = name;
		}
		return item;
	}
	
	@CacheEvict(allEntries = true)
	public void clearAll(String unlesskey) {
	}
	
	@CacheEvict(key = "#result.code")
	public Item clearByResult(String uselesskey, String code) {
		System.out.println(">>>>called "+code);
		Item item = items.get(code);
		if(item==null) {
			throw new NotFoundException("no "+code);
		}
		return item;
	}
	
	public void resetCallGet() {
		callGet = 0;
	}
	
	public List<Item> list() {
		return new LinkedList<>(items.values());
	}
	
}
