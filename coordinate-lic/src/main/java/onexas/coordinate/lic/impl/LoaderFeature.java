package onexas.coordinate.lic.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import onexas.coordinate.lic.Feature;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LoaderFeature implements Feature {
	protected final String name;

	protected Map<String, String> values;

	public LoaderFeature(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Number getNumberValue(String prop) {
		if (isUnlimited(prop)) {
			// in general, caller should check isUnlimited first.
			return Double.valueOf(Double.MAX_VALUE);
		}
		String val = values == null ? null : values.get(prop);
		if (val == null) {
			return null;
		}
		try {
			BigDecimal num = new BigDecimal(val);
			return num;
		} catch (Exception x) {
			return null;
		}
	}

	public String getStringValue(String prop) {
		return values == null ? null : values.get(prop);
	}

	public Boolean getBooleanValue(String prop) {
		if (isUnlimited(prop)) {
			return Boolean.TRUE;
		}
		String val = values == null ? null : values.get(prop);
		if (val == null) {
			return null;
		}
		try {
			return Boolean.parseBoolean(val);
		} catch (Exception x) {
			return null;
		}
	}

	public Map<String, String> getValueMap() {
		return values == null ? Collections.emptyMap() : Collections.unmodifiableMap(values);
	}

	@Override
	public boolean isUnlimited(String item) {
		String val = values == null ? null : values.get(item);
		return Feature.UNLIMITED.equals(val);
	}

	@Override
	public boolean hasValue(String item) {
		return values == null ? false : values.containsKey(item);
	}

	@Override
	public List<String> listProp() {
		return values == null ? Collections.emptyList() : new ArrayList<>(values.keySet());
	}
}
