package onexas.coordinate.common.util;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.lang.Locales;
import onexas.coordinate.common.lang.Strings;


/**
 * 
 * @author Dennis Chen
 *
 */
public class Gsons {

	private static final GsonBuilder defaultBuilder = new GsonBuilder();
	static {
		// for the class that can't be override by simple type mapping
		defaultBuilder.registerTypeAdapterFactory(new HighPriorityTypeAdapterFactory());

		// fix number always decode to double string (e.g. 1.0, 33.0) in map object
		defaultBuilder.registerTypeAdapter(Map.class, new IntegerFixTypeAdapter());
		defaultBuilder.registerTypeAdapter(new TypeToken<Map <String, Object>>(){}.getType(), new IntegerFixTypeAdapter());
		defaultBuilder.registerTypeAdapter(List.class, new IntegerFixTypeAdapter());
		defaultBuilder.registerTypeAdapter(new TypeToken<List <Object>>(){}.getType(), new IntegerFixTypeAdapter());
		
		//process to base64 just like jackson
		defaultBuilder.registerTypeAdapter(byte[].class, new ByteArrayAdapter());
		//dont' escape html(<>=) just like jackson
		defaultBuilder.disableHtmlEscaping();
	}

	private static final Gson defaultGson = defaultBuilder.create();

	public static Gson newGson() {
		return defaultGson.newBuilder().create();
	}

	public static Gson prettyGson() {
		GsonBuilder m = defaultGson.newBuilder();
		m.setPrettyPrinting();
		return m.create();
	}

	public static String jsonify(Object obj) {
		return jsonify(obj, false);
	}

	public static String jsonify(Object obj, boolean pretty) {
		if (obj == null) {
			return null;
		}
		try {
			if (pretty) {
				return prettyGson().toJson(obj);
			} else {
				return defaultGson.toJson(obj);
			}
		} catch (RuntimeException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

	public static String pretty(String json) {
		if (json == null) {
			return null;
		}
		try {
			Gson m = prettyGson();
			return m.toJson(JsonParser.parseString(json));
		} catch (JsonParseException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

	public static String plain(String json) {
		if (json == null) {
			return null;
		}
		try {
			Gson m = defaultGson;
			return m.toJson(JsonParser.parseString(json));
		} catch (JsonParseException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

	public static <T> T objectify(String json, Class<T> clz) {
		if (json == null) {
			return null;
		}
		if (Strings.isBlank(json)) {
			json = "{}";
		}
		try {
			T obj = defaultGson.fromJson(json, clz);
			return obj;
		} catch (JsonParseException e) {
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

	public static <T> T objectify(String json, Type type) {
		if (json == null) {
			return null;
		}
		try {
			T obj = defaultGson.fromJson(json, type);
			return obj;
		} catch (JsonParseException e) {
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

		return objectify(jsonify(obj), clz);
	}

	public static <T> T transform(Object obj, Type type) {
		if (obj == null) {
			return null;
		}

		return objectify(jsonify(obj), type);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(Object obj) {
		return transform(obj, Map.class);
	}

	@SuppressWarnings("unchecked")
	public static <T> T copy(Object obj) {
		return transform(obj, (Class<T>) obj.getClass());
	}

	public static Configuration toConfiguration(String json) throws ConfigurationException {
		if (json == null) {
			json = "{}";
		}
		JSONConfiguration cfg = new JSONConfiguration();
		cfg.read(new StringReader(json));
		return cfg;
	}

	public static class HighPriorityTypeAdapterFactory implements TypeAdapterFactory {
		@SuppressWarnings("unchecked")
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			if (TimeZone.class.isAssignableFrom(type.getRawType())) {
				return (TypeAdapter<T>) new TimeZoneTypeAdapter();
			} else if (Locale.class.isAssignableFrom(type.getRawType())) {
				return (TypeAdapter<T>) new LocaleTypeAdapter();
			}
			return null;
		}
	}

	public static class TimeZoneTypeAdapter extends TypeAdapter<TimeZone> {

		public TimeZone read(JsonReader reader) throws IOException {
			if (reader.peek() == JsonToken.NULL) {
				reader.nextNull();
				return null;
			}
			String value = reader.nextString();
			return TimeZone.getTimeZone(value);

		}

		public void write(JsonWriter writer, TimeZone value) throws IOException {
			if (value == null) {
				writer.nullValue();
			} else {
				writer.value(value.getID());
			}
		}

	}

	public static class LocaleTypeAdapter extends TypeAdapter<Locale> {

		public Locale read(JsonReader reader) throws IOException {
			if (reader.peek() == JsonToken.NULL) {
				reader.nextNull();
				return null;
			}
			String value = reader.nextString();
			return Locales.getLocale(value);

		}

		public void write(JsonWriter writer, Locale value) throws IOException {
			if (value == null) {
				writer.nullValue();
			} else {
				writer.value(Locales.toString(value));
			}
		}
	}

	public static class IntegerFixTypeAdapter extends TypeAdapter<Object> {

		@Override
		public void write(JsonWriter out, Object value) throws IOException {
			if (value == null) {
				out.nullValue();
				return;
			} else if (value instanceof Number) {
				double d = ((Number) value).doubleValue();
				if (d % 1 == 0) {
					out.value(((Number) value).longValue());
				} else {
					out.value(d);
				}
			} else {
				@SuppressWarnings("unchecked")
				TypeAdapter<Object> typeAdapter = (TypeAdapter<Object>) defaultGson.getAdapter(value.getClass());
				if (typeAdapter instanceof ObjectTypeAdapter) {
					out.beginObject();
					out.endObject();
					return;
				}
				typeAdapter.write(out, value);
			}
		}

		@Override
		public Object read(JsonReader in) throws IOException {
			JsonToken token = in.peek();
			switch (token) {
			case NUMBER:
				double d = in.nextDouble();
				if (d % 1 == 0) {
					return Long.valueOf((long) d);
				} else {
					return Double.valueOf(d);
				}
			case BEGIN_ARRAY:
				List<Object> list = new ArrayList<Object>();
				in.beginArray();
				while (in.hasNext()) {
					list.add(read(in));
				}
				in.endArray();
				return list;

			case BEGIN_OBJECT:
				Map<String, Object> map = new LinkedTreeMap<String, Object>();
				in.beginObject();
				while (in.hasNext()) {
					map.put(in.nextName(), read(in));
				}
				in.endObject();
				return map;

			case STRING:
				return in.nextString();

			case BOOLEAN:
				return in.nextBoolean();

			case NULL:
				in.nextNull();
				return null;

			default:
				throw new IllegalStateException();
			}
		}
	}
	
	public static class ByteArrayAdapter extends TypeAdapter<byte[]> {

        @Override
        public void write(JsonWriter out, byte[] value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(Base64.encodeBase64String(value));
            }
        }

        @Override
        public byte[] read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case NULL:
                    in.nextNull();
                    return null;
                default:
                    String bytesAsBase64 = in.nextString();
                    return Base64.decodeBase64(bytesAsBase64);
            }
        }
    }

	public static GsonBuilder fixInteger(GsonBuilder builder) {
		builder.registerTypeAdapter(Map.class, new IntegerFixTypeAdapter());
		builder.registerTypeAdapter(new TypeToken<Map <String, Object>>(){}.getType(), new IntegerFixTypeAdapter());
		builder.registerTypeAdapter(List.class, new IntegerFixTypeAdapter());
		builder.registerTypeAdapter(new TypeToken<List <Object>>(){}.getType(), new IntegerFixTypeAdapter());
		return builder;
	}
}
