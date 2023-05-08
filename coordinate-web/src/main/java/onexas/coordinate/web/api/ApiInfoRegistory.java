package onexas.coordinate.web.api;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.util.ValueII;
import onexas.coordinate.web.api.v1.model.ApiInfo;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "ApiInfoRegistory")
public class ApiInfoRegistory {

	private static final Logger logger = LoggerFactory.getLogger(ApiInfoRegistory.class);

	private Map<ValueII<String, String>, ApiInfo> infoMap = new LinkedHashMap<>();

	synchronized public ApiInfo register(String name, String version, String uri) {
		ApiInfo info = new ApiInfo();
		info.setName(name);
		info.setVersion(version);
		info.setUri(uri);
		infoMap.put(new ValueII<String, String>(name, version), info);
		logger.info("Register {}:{} {}", name, version, uri);
		return info;
	}

	synchronized public List<ApiInfo> list() {
		LinkedList<ApiInfo> l = new LinkedList<>(infoMap.values());
		Collections.sort(l, new Comparator<ApiInfo>() {

			@Override
			public int compare(ApiInfo o1, ApiInfo o2) {
				if(o1.getName().equals(o2.getName())) {
					return o1.getVersion().compareTo(o2.getVersion());
				}
				return o1.getName().compareTo(o2.getName());
			}
		});
		return l;
	}
}
