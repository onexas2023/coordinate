package onexas.coordinate.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;

import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.BadConfigurationException;
import onexas.coordinate.common.lang.Objects;

/**
 * A property source implementation that provide getValue(key, defaultValue)
 * method and getLength(key), length(key) to determinate if a key is a array
 * value.
 * 
 * @author Dennis Chen
 *
 */
public class BetterPropertySource extends EnumerablePropertySource<String> {

	private MapPropertySource mapPropertySource;
	private PropertiesPropertySource propertiesPropertySource;

	public BetterPropertySource(String name, String yaml) throws BadConfigurationException {
		super(name, yaml);
		propertiesPropertySource = Yamls.toPropertiesPropertySource(getSource());
		mapPropertySource = Yamls.toMapPropertySource(getSource());
	}

	@Override
	public Object getProperty(String name) {
		return propertiesPropertySource.getProperty(name);
	}

	@Override
	public String[] getPropertyNames() {
		return propertiesPropertySource.getPropertyNames();
	}

	@SuppressWarnings("rawtypes")
	public Integer getLength(String name) {
		String[] items = name.split("\\.");
		Object val = null;
		String fullname = null;
		String chainname = null;
		Map chainmap = null;
		for (int i = 0; i < items.length; i++) {
			fullname = (fullname == null ? "" : fullname + ".") + items[i];
			chainname = (chainname == null ? "" : chainname + ".") + items[i];

			if (chainmap == null) {
				val = mapPropertySource.getSource().get(fullname);
			} else {
				val = chainmap.get(chainname);
				if (val == null) {
					val = mapPropertySource.getSource().get(fullname);
					if (val == null) {
						continue;
					}
				}

			}

			if (val instanceof Map) {
				chainmap = (Map) val;
				chainname = null;
			}

		}
		return (val instanceof List) ? ((List) val).size() : null;
	}

	public int length(String name) {
		Integer l = getLength(name);
		return l == null ? 0 : l.intValue();
	}

	public String getString(String name, String defVal) {
		Object val = getProperty(name);
		if (val == null) {
			return defVal;
		}
		if (val instanceof String) {
			return (String) val;
		}
		return val.toString();
	}

	public MapPropertySource getMapPropertySource() {
		return mapPropertySource;
	}

	public String getString(String name) {
		return getString(name, null);
	}

	public List<String> getStringList(String name, List<String> defVal) {
		Integer l = getLength(name);
		if (l == null) {
			String val = getString(name);
			if (val != null) {
				List<String> list = new ArrayList<>();
				for (String s : val.split(",")) {
					list.add(s.trim());
				}
				return list;
			}
			return defVal;
		}
		List<String> list = new ArrayList<>();
		for (int i = 0; i < l.intValue(); i++) {
			list.add(getString(name + "[" + i + "]"));
		}
		return list;
	}

	public List<String> getStringList(String name) {
		return getStringList(name, null);
	}

	public Integer getInteger(String name, Integer defVal) {
		Object val = getProperty(name);
		if (val == null) {
			return defVal;
		}
		return Objects.coerceToInteger(val);
	}

	public Integer getInteger(String name) {
		return getInteger(name, null);
	}

	public int getInt(String name, int defVal) {
		return getInteger(name, defVal).intValue();
	}

	public Long getLong(String name, Long defVal) {
		Object val = getProperty(name);
		if (val == null) {
			return defVal;
		}
		return Objects.coerceToLong(val);
	}

	public Long getLong(String name) {
		return getLong(name, null);
	}

	public Float getFloat(String name, Float defVal) {
		Object val = getProperty(name);
		if (val == null) {
			return defVal;
		}
		return Objects.coerceToFloat(val);
	}

	public Float getFloat(String name) {
		return getFloat(name, null);
	}

	public Double getDouble(String name, Double defVal) {
		Object val = getProperty(name);
		if (val == null) {
			return defVal;
		}
		return Objects.coerceToDouble(val);
	}

	public Double getDouble(String name) {
		return getDouble(name, null);
	}

	public Boolean getBoolean(String name, Boolean defVal) {
		Object val = getProperty(name);
		if (val == null) {
			return defVal;
		}
		return Objects.coerceToBoolean(val);
	}

	public Boolean getBoolean(String name) {
		return getBoolean(name, null);
	}

	public BigDecimal getBigDecimal(String name, BigDecimal defVal) {
		Object val = getProperty(name);
		if (val == null) {
			return defVal;
		}
		return Objects.coerceToBigDecimal(val);
	}

	public BigDecimal getBigDecimal(String name) {
		return getBigDecimal(name, null);
	}

	public BigInteger getBigInteger(String name, BigInteger defVal) {
		Object val = getProperty(name);
		if (val == null) {
			return defVal;
		}
		return Objects.coerceToBigInteger(val);
	}

	public BigInteger getBigInteger(String name) {
		return getBigInteger(name, null);
	}
	
	
	public void checkAllRequired(String... keys) throws BadArgumentException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			if (getProperty(keys[i]) == null) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(keys[i]);
			}
		}
		if (sb.length() > 0) {
			throw new BadArgumentException("all [{}] are required",sb.toString());
		}
	}

	public void checkAnyRequired(String... keys) throws BadArgumentException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			if (getProperty(keys[i]) != null) {
				return;
			}
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(keys[i]);
		}
		if (sb.length() > 0) {
			throw new BadArgumentException("one of [{}] is required", sb.toString());
		}
	}
	
	public boolean isAnyReiqured(String ...keys) {
		if(keys.length==0) {
			return true;
		}
		for (int i = 0; i < keys.length; i++) {
			if (getProperty(keys[i]) != null) {
				return true;
			}
		}
		return false;
	}

}