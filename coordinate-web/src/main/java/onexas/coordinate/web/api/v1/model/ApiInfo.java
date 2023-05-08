package onexas.coordinate.web.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */
public class ApiInfo {

	@Schema(description = "Api's name")
	String name;

	@Schema(description = "Api's version")
	String version;

	@Schema(description = "Api's uri")
	String uri;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
