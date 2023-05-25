package onexas.coordinate.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Dennis Chen
 *
 */
public class CacheEvict implements Serializable {

	private static final long serialVersionUID = 1L;
	
	List<String> names;
	
	Serializable key;
	
	public CacheEvict(Serializable key, List<String> names) {
		this.names = names;
	}
	
	public CacheEvict(List<String> names) {
		this.names = names;
	}
	
	public CacheEvict() {
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public Serializable getKey() {
		return key;
	}

	public void setKey(Serializable key) {
		this.key = key;
	}
	
	

}
