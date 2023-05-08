package onexas.coordinate.common.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.lang.Locales;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Jsons {

	private static final ObjectMapper defaultObjectMapper = new ObjectMapper();
	private static final SimpleModule defaultModule = new SimpleModule();
	static {
		defaultObjectMapper.setSerializationInclusion(Include.NON_NULL);
		defaultObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		defaultObjectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		defaultModule.addDeserializer(TimeZone.class, new TimeZoneDeserializer());
		defaultModule.addSerializer(TimeZone.class, new TimeZoneSerializer());
		defaultModule.addDeserializer(Locale.class, new LocaleDeserializer());
		defaultModule.addSerializer(Locale.class, new LocaleSerializer());

		defaultObjectMapper.registerModule(defaultModule);
	}

	public static ObjectMapper newObjectMapper() {
		return defaultObjectMapper.copy();
	}

	public static ObjectMapper prettyObjectMapper() {
		ObjectMapper m = newObjectMapper();
		m.enable(SerializationFeature.INDENT_OUTPUT);
		return m;
	}

	public static String jsonify(Object obj) {
		return jsonify(obj, false);
	}

	public static String jsonify(Object obj, boolean pretty) {
		if (obj == null) {
			return null;
		}
		try (StringWriter w = new StringWriter()) {
			if (pretty) {
				prettyObjectMapper().writeValue(w, obj);
			} else {
				defaultObjectMapper.writeValue(w, obj);
			}
			return w.toString();
		} catch (JsonGenerationException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (IOException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

	public static String pretty(String json) {
		if (json == null) {
			return null;
		}
		try (StringWriter w = new StringWriter()) {
			ObjectMapper m = prettyObjectMapper();
			JsonNode n = m.readValue(json, JsonNode.class);
			m.writeValue(w, n);
			return w.toString();
		} catch (JsonGenerationException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (IOException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

	public static String plain(String json) {
		if (json == null) {
			return null;
		}
		try (StringWriter w = new StringWriter()) {
			ObjectMapper m = defaultObjectMapper;
			JsonNode n = m.readValue(json, JsonNode.class);
			m.writeValue(w, n);
			return w.toString();
		} catch (JsonGenerationException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (IOException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

	public static <T> T objectify(String json, Class<T> clz) {
		if (json == null) {
			return null;
		}
		// JsonMappingException: No content to map due to end-of-input
		if (Strings.isBlank(json)) {
			json = "{}";
		}
		try {
			T obj = defaultObjectMapper.readValue(json, clz);
			return obj;
		} catch (JsonGenerationException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (IOException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

	public static <T> T objectify(String json, Class<T> clz, boolean cleanOnError) {
		try {
			return objectify(json, clz);
		} catch (RuntimeException x) {
			if (cleanOnError) {
				return objectify("{}", clz);
			}
			throw x;
		}
	}

	public static <T> T objectify(String json, TypeReference<T> type) {
		if (json == null) {
			return null;
		}
		try {
			T obj = defaultObjectMapper.readValue(json, type);
			return obj;
		} catch (JsonGenerationException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (IOException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

	public static JsonNode nodify(String json) {
		if (json == null) {
			return null;
		}
		try {
			JsonNode obj = defaultObjectMapper.readTree(json);
			return obj;
		} catch (JsonGenerationException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (IOException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

	public static <T> T transform(Object obj, Class<T> clz) {
		return transform(obj, clz, false);
	}

	@SuppressWarnings("unchecked")
	public static <T> T transform(Object obj, Class<T> clz, boolean reuse) {
		if (obj == null) {
			return null;
		}

		if (reuse && clz.isAssignableFrom(obj.getClass())) {
			return ((T) obj);
		}

		try (StringWriter w = new StringWriter()) {
			defaultObjectMapper.writeValue(w, obj);
			String json = w.toString();
			return defaultObjectMapper.readValue(json, clz);
		} catch (JsonGenerationException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (IOException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

	public static <T> T transform(Object obj, TypeReference<T> type) {
		if (obj == null) {
			return null;
		}

		try (StringWriter w = new StringWriter()) {
			defaultObjectMapper.writeValue(w, obj);
			String json = w.toString();
			return defaultObjectMapper.readValue(json, type);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(Object obj) {
		return transform(obj, Map.class);
	}

	@SuppressWarnings("unchecked")
	public static <T> T copy(Object obj) {
		return transform(obj, (Class<T>) obj.getClass());
	}

	public static JSONConfiguration toConfiguration(String json) throws ConfigurationException {
		if (json == null) {
			json = "{}";
		}
		JSONConfiguration cfg = new JSONConfiguration();
		cfg.read(new StringReader(json));
		return cfg;
	}

	public static class TimeZoneSerializer extends StdSerializer<TimeZone> {
		private static final long serialVersionUID = 1L;

		protected TimeZoneSerializer() {
			super(TimeZone.class);
		}

		@Override
		public void serialize(TimeZone value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			if (value == null) {
				gen.writeNull();
			} else {
				gen.writeString(value.getID());
			}
		}

	}

	public static class TimeZoneDeserializer extends StdDeserializer<TimeZone> {
		private static final long serialVersionUID = 1L;

		protected TimeZoneDeserializer() {
			super(TimeZone.class);
		}

		@Override
		public TimeZone deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			String value = p.getValueAsString();
			if (value == null) {
				return null;
			}
			return TimeZone.getTimeZone(value);
		}
	}

	public static class LocaleSerializer extends StdSerializer<Locale> {
		private static final long serialVersionUID = 1L;

		protected LocaleSerializer() {
			super(Locale.class);
		}

		@Override
		public void serialize(Locale value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			if (value == null) {
				gen.writeNull();
			} else {
				gen.writeString(Locales.toString(value));
			}
		}

	}

	public static class LocaleDeserializer extends StdDeserializer<Locale> {
		private static final long serialVersionUID = 1L;

		protected LocaleDeserializer() {
			super(Locale.class);
		}

		@Override
		public Locale deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			String value = p.getValueAsString();
			if (value == null) {
				return null;
			}
			return Locales.getLocale(value);
		}
	}
}
