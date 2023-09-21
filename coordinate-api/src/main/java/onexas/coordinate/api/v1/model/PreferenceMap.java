package onexas.coordinate.api.v1.model;

import java.util.Map;

import onexas.coordinate.common.model.SchemaMap;

/**
 * schema helper
 * 
 * @author Dennis Chen
 *
 */

public class PreferenceMap extends SchemaMap<String> {
	private static final long serialVersionUID = 1L;

	public PreferenceMap() {
	}

	public PreferenceMap(Map<String, String> map) {
		super(map);
	}
}
