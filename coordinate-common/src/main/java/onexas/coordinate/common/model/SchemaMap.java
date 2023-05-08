package onexas.coordinate.common.model;

import java.util.LinkedHashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Hidden;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SchemaMap<T> extends LinkedHashMap<String, T> {

	public static final String SCHEMA_NAME = "SchemaMap";

	private static final long serialVersionUID = 1L;

	public SchemaMap() {
		super();
	}

	public SchemaMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}

	public SchemaMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public SchemaMap(int initialCapacity) {
		super(initialCapacity);
	}

	public SchemaMap(Map<String, ? extends T> m) {
		super(m);
	}

	@Override
	@Hidden
	public boolean isEmpty() {
		return super.isEmpty();
	}
	
	public SchemaMap<T> with(String key, T value) {
		this.put(key, value);
		return this;
	}
}
