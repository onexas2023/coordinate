package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class ServerSettingUpdate implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String adminEmail;

	protected String consoleUrl;

	protected String apiBaseUrl;

	protected String apiInternalBaseUrl;

	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	public ServerSettingUpdate withAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
		return this;
	}

	public String getConsoleUrl() {
		return consoleUrl;
	}

	public void setConsoleUrl(String consoleUrl) {
		this.consoleUrl = consoleUrl;
	}

	public String getApiBaseUrl() {
		return apiBaseUrl;
	}

	public void setApiBaseUrl(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
	}

	public String getApiInternalBaseUrl() {
		return apiInternalBaseUrl;
	}

	public void setApiInternalBaseUrl(String apiInternalBaseUrl) {
		this.apiInternalBaseUrl = apiInternalBaseUrl;
	}

	public ServerSettingUpdate withConsoleUrl(String consoleUrl) {
		this.consoleUrl = consoleUrl;
		return this;
	}

	public ServerSettingUpdate withApiBaseUrl(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
		return this;
	}

	public ServerSettingUpdate withApiInternalBaseUrl(String apiInternalBaseUrl) {
		this.apiInternalBaseUrl = apiInternalBaseUrl;
		return this;
	}
}
