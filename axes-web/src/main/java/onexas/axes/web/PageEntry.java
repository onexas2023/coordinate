package onexas.axes.web;

/**
 * 
 * @author Dennis Chen
 *
 */
public class PageEntry {
	String page;
	String permissionRequest;
	String viewUri;
	String title;

	public PageEntry() {
	}

	public PageEntry(String page, String viewUri, String title, String permissionRequest) {
		this.page = page;
		this.viewUri = viewUri;
		this.title = title;
		this.permissionRequest = permissionRequest;
	}

	public String getPage() {
		return page;
	}

	public String getPermissionRequest() {
		return permissionRequest;
	}

	public void setPermissionRequest(String permissionRequest) {
		this.permissionRequest = permissionRequest;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getViewUri() {
		return viewUri;
	}

	public void setViewUri(String viewUri) {
		this.viewUri = viewUri;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}