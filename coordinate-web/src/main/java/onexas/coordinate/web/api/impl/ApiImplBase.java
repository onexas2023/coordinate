package onexas.coordinate.web.api.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import onexas.coordinate.web.api.ApiInfoRegistory;
/**
 * 
 * @author Dennis Chen
 *
 */
public class ApiImplBase {

	@Autowired
	protected ApiInfoRegistory infoRegistory;

	final protected String name;
	final protected String version;
	final protected String uri;

	protected ApiImplBase(String name, String version, String uri) {
		this.name = name;
		this.version = version;
		this.uri = uri;
	}

	@PostConstruct
	public void postConstruct() {
		infoRegistory.register(name, version, uri);
	}

}
