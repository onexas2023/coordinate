package onexas.coordinate.api.v1.admin;

import static onexas.coordinate.web.api.Api.NAME_AUTH_TOKEN;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import onexas.coordinate.api.v1.admin.model.APrincipalPermission;
import onexas.coordinate.api.v1.admin.model.ARole;
import onexas.coordinate.api.v1.admin.model.ARoleCreate;
import onexas.coordinate.api.v1.admin.model.ARoleFilter;
import onexas.coordinate.api.v1.admin.model.ARoleListPage;
import onexas.coordinate.api.v1.admin.model.ARoleUpdate;
import onexas.coordinate.api.v1.admin.model.ARoleUserFilter;
import onexas.coordinate.api.v1.admin.model.AUserListPage;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "Role Api V1", name = AdminRoleApi.API_TAG)

@RequestMapping(AdminRoleApi.API_URI)
@SecurityScheme(name = NAME_AUTH_TOKEN, type = SecuritySchemeType.APIKEY, paramName = NAME_AUTH_TOKEN, in = SecuritySchemeIn.HEADER)
@ApiResponses(value = {
		@ApiResponse(responseCode = Api.HTTP_BAD_REQUEST, description = Api.MSG_BAD_REQUEST, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_UNAUTHORIZED, description = Api.MSG_UNAUTHORIZED, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_FORBIDDEN, description = Api.MSG_FORBIDDEN, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_NOT_FOUND, description = Api.MSG_NOT_FOUND, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))) })
public interface AdminRoleApi extends Api {

	static public final String API_NAME = "coordinate-admin-role";
	static public final String API_URI = "/api/coordinate/v1/admin/role";
	static public final String API_TAG = API_NAME;
	static public final String API_PERMISSION_TARGET = API_NAME;

	@Operation(summary = "Get role list with a role-filter", description = "return filtered role's information", tags = {
			AdminRoleApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "filter")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = ARoleListPage.class))) })
	@RequestMapping(value = "/roles", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public ARoleListPage listRole(
			@Parameter(description = "role-filter object") @Valid @RequestBody(required = false) ARoleFilter filter);

	@Operation(summary = "Get a role by it's uid", description = "Return the the role's brief information", tags = {
			AdminRoleApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = ARole.class))) })
	@RequestMapping(value = "/role/{uid}", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public ARole getRole(@Parameter(description = "uid of role", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "find mode, return null when not found") @RequestParam(name = "find", required = false) Boolean find);

	@Operation(summary = "Create a role", description = "you must't set a uid of role, uid will be generated automatically", tags = {
			AdminRoleApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "role")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = ARole.class))) })
	@RequestMapping(value = "/role", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public ARole createRole(
			@Parameter(description = "role-create object", required = true) @Valid @NotNull @RequestBody ARoleCreate roleCreate);

	@Operation(summary = "Update a role", description = "a null field of role means keep the old value, the role's uid is always required", tags = {
			AdminRoleApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "role")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = ARole.class))) })
	@RequestMapping(value = "/role/{uid}", method = RequestMethod.PUT, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public ARole updateRole(@Parameter(description = "uid of a role", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "role-update object", required = true) @Valid @NotNull @RequestBody ARoleUpdate roleUpdate);

	@Operation(summary = "Delete role by it's uid", tags = {
			AdminRoleApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/role/{uid}", method = RequestMethod.DELETE, produces = { TYPE_APP_JSON })
	public Response deleteRole(
			@Parameter(description = "uid of a role", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "be quiet, don't cause error if not found") @RequestParam(name = "quiet", required = false) Boolean quiet);
	/*
	 * Role User Api
	 */

	@Operation(summary = "Get user list of the role with a page-filter ", description = "return filtered user's information of the role", tags = {
			AdminRoleApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "filter")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AUserListPage.class))) })
	@RequestMapping(value = "/role/{uid}/users", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AUserListPage listRoleUser(
			@Parameter(description = "uid of a role", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "role-filter object") @Valid @RequestBody(required = false) ARoleUserFilter filter);

	@Operation(summary = "Add users to the role", description = "add users to the role by giving a user uid list", tags = {
			AdminRoleApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/role/{uid}/user", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public Response addRoleUser(
			@Parameter(description = "uid of a role", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "user uid list") @Valid @NotNull @RequestBody List<String> userUidList);

	@Operation(summary = "Set users of the role", description = "set users to the role by giving a user uid list, other user of the role will be removed", tags = {
			AdminRoleApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/role/{uid}/user", method = RequestMethod.PUT, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public Response setRoleUser(
			@Parameter(description = "uid of a role", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "user uid list") @Valid @NotNull @RequestBody List<String> userUidList);

	@Operation(summary = "remove users of the role", description = "remove users of the role by giving a user uid list", tags = {
			AdminRoleApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/role/{uid}/user", method = RequestMethod.DELETE, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public Response removeRoleUser(
			@Parameter(description = "uid of a role", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "user uid list") @Valid @NotNull @RequestBody List<String> userUidList);

	/*
	 * Role Permission API
	 */
	@Operation(summary = "Get permission list of the role", description = "return all permission's information of the role", tags = {
			AdminRoleApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = APrincipalPermission.class)))) })
	@RequestMapping(value = "/role/{uid}/permissions", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public List<APrincipalPermission> listRolePermission(
			@Parameter(description = "uid of a role", required = true) @PathVariable("uid") String uid);

	@Operation(summary = "Add permissions to the role", description = "add permissions to the role by giving permission <function, action> list", tags = {
			AdminRoleApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/role/{uid}/permission", method = RequestMethod.POST, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public Response addRolePermission(
			@Parameter(description = "uid of a role", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "user uid list") @Valid @NotNull @RequestBody List<APrincipalPermission> rolePermissionList);

	@Operation(summary = "Set permissions of the role", description = "set permissions to the role by giving a permission<function, action> list, other permissions of the role will be removed", tags = {
			AdminRoleApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/role/{uid}/permission", method = RequestMethod.PUT, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public Response setRolePermission(
			@Parameter(description = "uid of a role", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "user uid list") @Valid @NotNull @RequestBody List<APrincipalPermission> rolePermissionList);

	@Operation(summary = "remove permissions of the role", description = "remove permissions of the role by giving a permission<function, action> list", tags = {
			AdminRoleApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/role/{uid}/permission", method = RequestMethod.DELETE, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public Response removeRolePermission(
			@Parameter(description = "uid of a role", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "user uid list") @Valid @NotNull @RequestBody List<APrincipalPermission> rolePermissionList);

}