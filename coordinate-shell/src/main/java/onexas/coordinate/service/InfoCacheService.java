package onexas.coordinate.service;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface InfoCacheService {

	public String put(String info, long timeoutAt);

	public String acquire(String token, boolean remove);

	public void prune(long timeoutBefore);
	
	public long count();

}
