package onexas.coordinate.web.api;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Config;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.app.RefreshableConfigLoader;
import onexas.coordinate.common.lang.Classes;
import onexas.coordinate.common.util.JsonPropertyEditorSupport;
import onexas.coordinate.common.util.ValueII;

/**
 * a generic data binder to register class and editor for WebDataBinder
 * to transfer a request param string to a class
 * inspired by https://stackoverflow.com/questions/20622359/automatic-conversion-of-json-form-parameter-in-spring-mvc-4-0
 * @author Dennis Chen
 *
 */
@ControllerAdvice
@Component(Env.NS_BEAN + "JsonWebDataBinderInit")
public class JsonWebDataBinderInit {

	private static final Logger logger = LoggerFactory.getLogger(JsonWebDataBinderInit.class);
	
	private static RefreshableConfigLoader<List<ValueII<Class<?>, JsonPropertyEditorSupport>>> editorConfigLoader = new RefreshableConfigLoader<List<ValueII<Class<?>, JsonPropertyEditorSupport>>>() {

		@Override
		protected List<ValueII<Class<?>, JsonPropertyEditorSupport>> load(Config config) {
			List<ValueII<Class<?>, JsonPropertyEditorSupport>> editors = new LinkedList<>();
			
			for(String className: AppContext.config().getStringList("coordinateWeb.api.additionalModelClass", Collections.emptyList())) {
				try {
					Class<?> clz = Classes.forName(className);
					editors.add(new ValueII<>(clz, new JsonPropertyEditorSupport(clz)));
					logger.info("Register WebDataBinder class {}", className);
				}catch(Exception x) {
					logger.error(x.getMessage(), x);
				}
			}
			return Collections.unmodifiableList(editors);
		}
	};
	
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		for(ValueII<Class<?>, JsonPropertyEditorSupport> vii:editorConfigLoader.load()) {			
			binder.registerCustomEditor(vii.getValue1(), vii.getValue2());
		}
	}

}