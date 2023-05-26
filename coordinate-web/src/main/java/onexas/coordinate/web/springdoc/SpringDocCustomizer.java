package onexas.coordinate.web.springdoc;

import static org.springdoc.core.SpringDocUtils.getConfig;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.OpenAPIService;
import org.springdoc.core.SpringDocUtils;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.tags.Tag;
import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Config;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Classes;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.model.SchemaMap;
import onexas.coordinate.web.api.Api;

@Component
@Profile(Env.PROFILE_API_DOC)
@DependsOn(AppContext.BEAN_NAME)
public class SpringDocCustomizer implements OpenApiCustomiser, OpenApiBuilderCustomizer, OperationCustomizer {

	private static final Logger logger = LoggerFactory.getLogger(SpringDocCustomizer.class);

	private Set<String> includeTags;
	private Set<String> excludeTags;

	@Autowired
	Environment env;

	@Autowired
	private ApplicationContext appContext;

	@Override
	public void customise(OpenAPI openApi) {

		openApi.info(apiInfo());

		Components components = openApi.getComponents();

		if (components == null) {
			openApi.setComponents(components = new Components());
		}

		components.setSecuritySchemes(securitySchemes());

		List<Pattern> ignorePattern = new LinkedList<>();

		for (String pattern : AppContext.config().getStringList("coordinateWeb.springDocApi.model.additionalSchemaClassIgnorePattern",
				Collections.emptyList())) {
			try {
				pattern.replace(".", "\\.");
				pattern.replace("*", ".*");
				ignorePattern.add(Pattern.compile(pattern));
			} catch (Exception x) {
				logger.error(x.getMessage(), x);
			}
		}
		for (String regex : AppContext.config().getStringList("coordinateWeb.springDocApi.model.additionalSchemaClassIgnoreRegex",
				Collections.emptyList())) {
			try {
				ignorePattern.add(Pattern.compile(regex));
			} catch (Exception x) {
				logger.error(x.getMessage(), x);
			}
		}

		List<Class<?>> enumClzs = new LinkedList<>();
		for (Config sub : AppContext.config().getSubConfigList("coordinateWeb.springDocApi.model.additionalSchemaClass","",true)) {
			try {

				String className = sub.getString("");
				Boolean force = sub.getBoolean("[@force]");
				if(!Boolean.TRUE.equals(force)) {
					if (ignorePattern.size() > 0 && ignorePattern.stream().anyMatch((p) -> {
						return p.matcher(className).matches();
					})) {
						continue;
					}
				}

				Class<?> clz = Classes.forName(className);
				if (clz.isEnum()) {
					enumClzs.add(clz);
					continue;
				}

				AnnotationsUtils.resolveSchemaFromType(clz, components, null);
				logger.info("Register swagger additional schema class {}", className);
			} catch (Exception x) {
				logger.error(x.getMessage(), x);
			}
		}

		for (Class<?> clz : enumClzs) {
			try {

				io.swagger.v3.oas.annotations.media.Schema schemaAnnoWrokaround = SchemaAnnoWorkaround.class
						.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);

				@SuppressWarnings("rawtypes")
				Optional<? extends Schema> o = AnnotationsUtils.getSchema(schemaAnnoWrokaround, null, false, clz,
						components, null);
				if (o.isPresent()) {
					@SuppressWarnings("rawtypes")
					Schema schema = o.get();
					String name = schema.getName();
					if (name == null) {
						name = clz.getSimpleName();
					}
					// only when schema has any ref(mean it is refered by a class in current swaager
					// impl) to it
					if (components.getSchemas() != null && components.getSchemas().containsKey(name)
							&& !Strings.isEmpty(components.getSchemas().get(name).get$ref())) {
						components.addSchemas(name, schema);
						logger.info("Register swagger additional schema enum {}", clz.getName());
					}
				} else {
					logger.warn("fail to register spring doc additional schema enum {}", clz.getName());
					continue;
				}

			} catch (Exception x) {
				logger.error(x.getMessage(), x);
			}
		}

		Schema<?> schema = components.getSchemas() != null ? components.getSchemas().get(SchemaMap.SCHEMA_NAME) : null;
		if (schema != null) {
			// set additionalProperties to true will cause code gen ignore to gen anything
			// for enable object schema
			schema.additionalProperties(new ObjectSchema());
		}

		// sort for predictable result
		List<Tag> tags = openApi.getTags();
		if (tags != null) {
			List<Tag> sortedTags = new LinkedList<>();
			sortedTags.addAll(tags);
			Collections.sort(sortedTags, (e1, e2) -> {
				return e1.getName().compareTo(e2.getName());
			});
			openApi.setTags(sortedTags);
		}
		Paths paths = openApi.getPaths();
		if (paths != null) {
			Paths sortedPaths = new Paths();
			List<String> keys = new LinkedList<>(paths.keySet());
			Collections.sort(keys);
			for (String k : keys) {
				PathItem item = paths.get(k);
				sortedPaths.put(k, item);

				for (Operation op : item.readOperations()) {
					ApiResponses resps = op.getResponses();
					if (resps != null) {
						ApiResponses sortedResps = new ApiResponses();
						List<String> respsKeys = new LinkedList<>(resps.keySet());
						Collections.sort(respsKeys);
						for (String rk : respsKeys) {
							ApiResponse resp = resps.get(rk);
							sortedResps.put(rk, resp);
						}
						sortedResps.setExtensions(resps.getExtensions());
						op.setResponses(sortedResps);
					}
				}

			}
			sortedPaths.setExtensions(paths.getExtensions());
			openApi.setPaths(sortedPaths);
		}

		@SuppressWarnings("rawtypes")
		Map<String, Schema> schemas = components.getSchemas();

		if (schemas != null) {
			@SuppressWarnings("rawtypes")
			Map<String, Schema> sortedSchemas = new LinkedHashMap<>();

			List<String> keys = new LinkedList<>(schemas.keySet());
			Collections.sort(keys);
			for (String k : keys) {
				schema = schemas.get(k);
				sortedSchemas.put(k, schema);

				if (schema != null) {
					// sort properties
					@SuppressWarnings("rawtypes")
					Map<String, io.swagger.v3.oas.models.media.Schema> properties = schema.getProperties();
					if (properties != null) {
						@SuppressWarnings("rawtypes")
						Map<String, io.swagger.v3.oas.models.media.Schema> sortedProperties = new LinkedHashMap<>();
						List<String> pkeys = new LinkedList<>(properties.keySet());
						Collections.sort(pkeys);
						for (String pk : pkeys) {
							sortedProperties.put(pk, properties.get(pk));
						}
						schema.setProperties(sortedProperties);
					}
				}
			}
			components.setSchemas(sortedSchemas);
		}

		logger.info("OpenApi doc includes {} paths, {} schemas", paths == null ? 0 : paths.size(),
				schemas == null ? 0 : schemas.size());

	}

	private Map<String, SecurityScheme> securitySchemes() {
		Map<String, SecurityScheme> securitySchemes = new LinkedHashMap<>();
		securitySchemes.put(Api.NAME_AUTH_TOKEN,
				new SecurityScheme().type(SecurityScheme.Type.APIKEY).name(Api.NAME_AUTH_TOKEN).in(In.HEADER));
		return securitySchemes;
	}

	private Info apiInfo() {
		String appName = AppContext.config().getString("app.name");
		String appVer = AppContext.config().getString("app.version");
		return new Info().title("OneXas Product API").version("1.0")
				.description(Strings.format("APIs for OneXas Coordinate base products, hosted by application {} version {}",appName, appVer))
				.termsOfService("").license(new License().name("OneXas Private License").url(""));
	}

	@io.swagger.v3.oas.annotations.media.Schema()
	static class SchemaAnnoWorkaround {

	}

	@Override
	public void customise(OpenAPIService openApiService) {

		// OpenAPIService build these by define, we will try to hide them
		Set<Object> beans = new LinkedHashSet<>();
		beans.addAll(appContext.getBeansWithAnnotation(RestController.class).values());
		beans.addAll(appContext.getBeansWithAnnotation(RequestMapping.class).values());
		beans.addAll(appContext.getBeansWithAnnotation(Controller.class).values());

		filter(beans);
	}

	private void filter(Collection<Object> beans) {
		SpringDocUtils utils = getConfig();

		synchronized (this) {
			if (includeTags == null || excludeTags == null) {
				List<String> l = AppContext.config().getStringList("coordinateWeb.springDocApi.include.tag");
				includeTags = l == null ? Collections.emptySet() : new LinkedHashSet<>(l);
				if (includeTags.size() > 0) {
					logger.info("include springDocApi {}", includeTags);
				}
				l = AppContext.config().getStringList("coordinateWeb.springDocApi.exclude.tag");
				excludeTags = l == null ? Collections.emptySet() : new LinkedHashSet<>(l);
				if (excludeTags.size() > 0) {
					logger.info("exclude springDocApi {}", excludeTags);
				}
			}
		}
		for (Object bean : beans) {
			Object handler = bean;

			Set<String> tags = new HashSet<>();
			io.swagger.v3.oas.annotations.tags.Tag tagAnno = ReflectionUtils.getAnnotation(handler.getClass(),
					io.swagger.v3.oas.annotations.tags.Tag.class);
			if (tagAnno != null) {
				tags.add(tagAnno.name());
			}
			boolean hitInclude = true;
			if (includeTags.size() > 0) {
				hitInclude = false;
				// tags must in include
				for (String t : tags) {
					if (includeTags.contains(t)) {
						hitInclude = true;
						break;
					}
				}
			}
			boolean hitExclude = false;
			if (excludeTags.size() > 0) {
				for (String t : tags) {
					if (excludeTags.contains(t)) {
						hitExclude = true;
						break;
					}
				}
			}
			if (hitExclude || (includeTags.size() > 0 && !hitInclude)) {
				utils.addHiddenRestControllers(bean.getClass());
			}

		}
	}

	@Override
	public Operation customize(Operation operation, HandlerMethod handlerMethod) {
		Class<?> clz = handlerMethod.getBeanType();
		Method method = handlerMethod.getMethod();

		if (Strings.isBlank(operation.getOperationId()) || method.getName().equals(operation.getOperationId())) {

			String newId = null;

			if (newId == null) {
				List<String> tags = operation.getTags();
				if (tags != null && tags.size() == 1) {
					newId = tags.get(0) + "#" + method.getName();
				}
			}

			if (newId == null) {
				Class<?>[] ifs = clz.getInterfaces();
				if (ifs.length == 1) {
					newId = ifs[0] + "#" + method.getName();
				}
			}

			if (newId == null && Strings.isBlank(operation.getOperationId())) {
				newId = clz.getSimpleName() + "#" + method.getName();
			}
			operation.setOperationId(newId);
		}

		return operation;
	}

}