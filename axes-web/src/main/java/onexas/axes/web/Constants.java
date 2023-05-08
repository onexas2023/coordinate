package onexas.axes.web;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Constants {
	
	public static final String DOMAIN_LOCAL = onexas.coordinate.api.v1.sdk.Constants.DOMAIN_LOCAL;
	
	public static final String AXES_PERMISSION_TARGET = "axes";
	public static final String AXES_PERMISSION_REQUEST = "axes:view";
	
	public static final String ENTRY_PAGE_INITIAL_PARAM_AUTH = "auth";
	
	public static final String REQ_PARAM_GO_BACK = "goback";
	public static final String REQ_PATH_PAGE = "page";
	public static final String REQ_PATH_PAGE_ARGS = "page-args";
	
	public static final String INCLUDE_ARGS = "args";

	public static final String EVENT_QUEUE_WORKSPACE = "axes.workspace";
	
	public static final String EVENT_QUEUE_INTERNAL = "axes.internal";

	public static final String ATTR_PAGE_CONTROLLER = "page.controller";
	public static final String ATTR_PAGE_CONTAINER = "page.container";
	public static final String ATTR_MAIN_CTRL = "mainCtrl";
	public static final String ATTR_MAIN_COMP = "mainComp";

	public static final String ARG_EDITING_OBJ = "editingObj";
	
	public static final String ARG_REQUEST_ID = "requestId";
	
	public static final String URI_ROLE_SELECTOR_POPUP = "~@/axes/admin/role/roleSelectorPopup.zul";
	public static final String URI_USER_SELECTOR_POPUP = "~@/axes/admin/user/userSelectorPopup.zul";
	public static final String URI_ORGANIZATION_SELECTOR_POPUP = "~@/axes/admin/organization/organizationSelectorPopup.zul";
	public static final String URI_PRINCIPAL_PERMISSION_SELECTOR_POPUP = "~@/axes/admin/security/principalPermissionSelectorPopup.zul";
	public static final String ARG_SELECTOR_SELECTED_SET = "selectedList";
	public static final String ARG_SELECTOR_MULTIPLE = "multiple";
}
