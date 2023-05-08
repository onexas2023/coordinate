package onexas.coordinate.common.util;

import java.beans.PropertyEditorSupport;

/**
 * 
 * @author Dennis Chen
 *
 */
public class JsonPropertyEditorSupport extends PropertyEditorSupport {
	Object value;
	Class<?> clz;

	public JsonPropertyEditorSupport(Class<?> clz) {
		this.clz = clz;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		value = null;//clean up
		try {
			value = Jsons.objectify(text, clz);
		}catch(Exception x) {
			throw new IllegalArgumentException(x.getMessage(), x);
		}
	}
}
