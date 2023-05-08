package onexas.coordinate.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.scanner.ScannerException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.BadConfigurationException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.Jsons.LocaleDeserializer;
import onexas.coordinate.common.util.Jsons.LocaleSerializer;
import onexas.coordinate.common.util.Jsons.TimeZoneDeserializer;
import onexas.coordinate.common.util.Jsons.TimeZoneSerializer;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Yamls {

	private static final ObjectMapper defaultObjectMapper = new ObjectMapper(new YAMLFactory());
	private static final SimpleModule defaultModule = new SimpleModule();

	static {
		defaultObjectMapper.setSerializationInclusion(Include.NON_NULL);
		defaultObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		defaultObjectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		defaultObjectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

		defaultModule.addDeserializer(TimeZone.class, new TimeZoneDeserializer());
		defaultModule.addSerializer(TimeZone.class, new TimeZoneSerializer());
		defaultModule.addDeserializer(Locale.class, new LocaleDeserializer());
		defaultModule.addSerializer(Locale.class, new LocaleSerializer());

		defaultObjectMapper.registerModule(defaultModule);
	}

	public static ObjectMapper newObjectMapper() {
		return defaultObjectMapper.copy();
	}

//	public static ObjectMapper prettyObjectMapper() {
//		ObjectMapper m = newObjectMapper();
//		m.enable(SerializationFeature.INDENT_OUTPUT);
//		return m;
//	}

	public static String yamlify(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Map && ((Map<?, ?>) obj).size() == 0) {
			return "";
		}
		try (StringWriter w = new StringWriter()) {
			defaultObjectMapper.writeValue(w, obj);
			String r = w.toString();
			// trim ---\n
			if (r.startsWith("---")) {
				r = r.substring(3).trim();
			}
			return r;
		} catch (JsonGenerationException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (IOException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

	public static <T> T objectify(String yaml, Class<T> clz) {
		if (yaml == null) {
			return null;
		}
		try {
			T obj = defaultObjectMapper.readValue(yaml, clz);
			return obj;
		} catch (JsonGenerationException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (MismatchedInputException e) {
			// when there is only comment in yaml
			// No content to map due to end-of-input, 
			if (isBlankOrCommentOnly(yaml)) {
				return objectify("{}", clz);
			}
			throw new BadArgumentException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (IOException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

	private static boolean isBlankOrCommentOnly(String yaml) {
		BufferedReader r = new BufferedReader(new StringReader(yaml));
		return r.lines().map(l -> l.trim()).allMatch((l) -> l.isEmpty() || l.startsWith("#"));
	}

	public static <T> T objectify(String yaml, Class<T> clz, boolean cleanOnError) {
		try {
			return objectify(yaml, clz);
		} catch (RuntimeException x) {
			if (cleanOnError) {
				return objectify("{}", clz);
			}
			throw x;
		}
	}

	public static <T> T transform(Object obj, Class<T> clz) {
		if (obj == null) {
			return null;
		}
		ObjectMapper m = newObjectMapper();
		m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try (StringWriter w = new StringWriter()) {
			m.writeValue(w, obj);
			String yaml = w.toString();
			return m.readValue(yaml, clz);
		} catch (JsonGenerationException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new BadArgumentException(e.getMessage(), e);
		} catch (IOException e) {
			throw new BadArgumentException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T copy(Object obj) {
		return transform(obj, (Class<T>) obj.getClass());
	}

	public static YAMLConfiguration toConfiguration(String yaml) throws BadConfigurationException {
		if (Strings.isBlank(yaml)) {
			yaml = "{}";
		}
		YAMLConfiguration cfg = new YAMLConfiguration();
		try {
			cfg.read(new StringReader(yaml));
			return cfg;
		} catch (ConfigurationException x) {
			throw new BadConfigurationException(x.getMessage(), x);
		}
	}

	static PropertiesPropertySource toPropertiesPropertySource(String yaml) throws BadConfigurationException {
		if (Strings.isBlank(yaml)) {
			yaml = "";
		}
		try {
			YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean() {
				@Override
				protected Yaml createYaml() {
					LoaderOptions loaderOptions = new LoaderOptions();
					loaderOptions.setAllowDuplicateKeys(true);
					return new Yaml(loaderOptions);
				}
			};
			Resource resource = new ByteArrayResource(yaml.getBytes());
			factory.setResources(resource);

			Properties properties = factory.getObject();

			PropertiesPropertySource conf = new PropertiesPropertySource(Securitys.md5String(yaml), properties);
			return conf;
		} catch (ScannerException x) {
			throw new BadConfigurationException(x.getMessage(), x);
		}
	}

	static MapPropertySource toMapPropertySource(String yaml) throws BadConfigurationException {
		if (Strings.isBlank(yaml)) {
			yaml = "";
		}
		try {
			YamlMapFactoryBean factory = new YamlMapFactoryBean() {
				@Override
				protected Yaml createYaml() {
					LoaderOptions loaderOptions = new LoaderOptions();
					loaderOptions.setAllowDuplicateKeys(true);
					return new Yaml(loaderOptions);
				}
			};
			Resource resource = new ByteArrayResource(yaml.getBytes());
			factory.setResources(resource);

			Map<String, Object> map = factory.getObject();

			MapPropertySource conf = new MapPropertySource(Securitys.md5String(yaml), map);
			return conf;
		} catch (ScannerException x) {
			throw new BadConfigurationException(x.getMessage(), x);
		}
	}

	public static BetterPropertySource toPropertySource(String yaml) throws BadConfigurationException {
		if (Strings.isBlank(yaml)) {
			yaml = "";
		}
		return new BetterPropertySource(Securitys.md5String(yaml), yaml);
	}
}
