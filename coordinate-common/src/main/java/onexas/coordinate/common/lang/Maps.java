package onexas.coordinate.common.lang;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author Dennis Chen
 * 
 */
public class Maps {

	public static final Map<String, String> toMap(String str) {
		return toMap(str, ',', '=');
	}

	public static final Map<String, String> toMap(String str, char entrySplit, char valueSplit) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (str != null) {
			String es = Character.toString(entrySplit);
			str = str.trim();
			for (String item : str.split(es)) {
				item = item.trim();
				int i = item.indexOf(valueSplit);
				if (i < 0) {
					map.put(item, "");
				} else {
					map.put(item.substring(0, i).trim(), item.substring(i + 1).trim());
				}
			}
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static void put(Map<String, Object> map, String propPath, Object value) {
		String[] pp = propPath.split("\\.");
		for (int i = 0; i < pp.length; i++) {
			if (i == pp.length - 1) {
				map.put(pp[i], value);
				return;
			}
			Object item = map.get(pp[i]);
			if (item == null) {
				map.put(pp[i], item = new LinkedHashMap<String, Object>());
			} else if (!(item instanceof Map)) {
				StringBuilder path = new StringBuilder();
				for (int j = 0; j <= i; j++) {
					if (path.length() != 0) {
						path.append(".");
					}
					path.append(pp[j]);
				}
				throw new IllegalStateException("can put to " + path + " which is not a map, is "+item.getClass());
			}
			map = (Map<String, Object>) item;
		}
	}
}