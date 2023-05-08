package onexas.coordinate.web.api;

import org.springframework.http.MediaType;

/**
 * the basic restful class which handle common processing, such as
 * exceptions(from {@link onexas.coordinate.common.err}) to response-entity
 * 
 * @author Dennis Chen
 *
 */
public interface Api {

	public static final String TYPE_ALL_VALUE = MediaType.ALL_VALUE;
	public static final String TYPE_APP_JSON = MediaType.APPLICATION_JSON_VALUE;
	public static final String TYPE_APP_XML = MediaType.APPLICATION_XML_VALUE;
	public static final String TYPE_TEXT_PLAIN = MediaType.TEXT_PLAIN_VALUE;
	public static final String TYPE_MULTIPART_FORM_DATA_VALUE = MediaType.MULTIPART_FORM_DATA_VALUE;
	
	

	public static final int CODE_OK = 200;
	public static final int CODE_BAD_REQUEST = 400;
	public static final int CODE_UNAUTHORIZED = 401;
	public static final int CODE_FORBIDDEN = 403;
	public static final int CODE_NOT_FOUND = 404;

	public static final String HTTP_OK = "200";
	public static final String HTTP_BAD_REQUEST = "400";
	public static final String HTTP_UNAUTHORIZED = "401";
	public static final String HTTP_FORBIDDEN = "403";
	public static final String HTTP_NOT_FOUND = "404";

	public static final String MSG_OK = "Query successfully";

	public static final String MSG_UNAUTHORIZED = "Require authentication";
	public static final String MSG_FORBIDDEN = "Has no permission to access";
	public static final String MSG_BAD_REQUEST = "Has wrong request arguments";
	public static final String MSG_NOT_FOUND = "Resource not found";

	public static final String NAME_AUTH_TOKEN = "Coordinate-Auth-Token";
	
	public static final String NAME_AUTH_COOKIE_PARAMETER = "c-auth";

	public static final String ACTION_VIEW = "view";
	public static final String ACTION_MODIFY = "modify";
	public static final String ACTION_ADMIN = "admin";

	public static final String V1 = "v1";

	public static final String CODE_GEN_REQ_BODY_NAME = /* x- */"codegen-request-body-name";
}
