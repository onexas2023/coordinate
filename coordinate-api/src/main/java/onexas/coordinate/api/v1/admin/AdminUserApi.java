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
import onexas.coordinate.api.v1.admin.model.ADomainUserCreate;
import onexas.coordinate.api.v1.admin.model.ARole;
import onexas.coordinate.api.v1.admin.model.AUser;
import onexas.coordinate.api.v1.admin.model.AUserCreate;
import onexas.coordinate.api.v1.admin.model.AUserFilter;
import onexas.coordinate.api.v1.admin.model.AUserListPage;
import onexas.coordinate.api.v1.admin.model.AUserOrganization;
import onexas.coordinate.api.v1.admin.model.AUserUpdate;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "User Api V1", name = AdminUserApi.API_TAG)

@RequestMapping(AdminUserApi.API_URI)
@SecurityScheme(name = NAME_AUTH_TOKEN, type = SecuritySchemeType.APIKEY, paramName = NAME_AUTH_TOKEN, in = SecuritySchemeIn.HEADER)
@ApiResponses(value = {
		@ApiResponse(responseCode = Api.HTTP_BAD_REQUEST, description = Api.MSG_BAD_REQUEST, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_UNAUTHORIZED, description = Api.MSG_UNAUTHORIZED, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_FORBIDDEN, description = Api.MSG_FORBIDDEN, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_NOT_FOUND, description = Api.MSG_NOT_FOUND, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))) })
public interface AdminUserApi extends onexas.coordinate.web.api.Api {

	static public final String API_NAME = "coordinate-admin-user";
	static public final String API_URI = "/api/coordinate/v1/admin/user";
	static public final String API_TAG = API_NAME;
	static public final String API_PERMISSION_TARGET = API_NAME;

	@Operation(summary = "Get user list with a filter", description = "return filtered user's information", tags = {
			AdminUserApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "filter")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AUserListPage.class))) })
	@RequestMapping(value = "/users", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AUserListPage listUser(
			@Parameter(description = "user-filter object") @Valid @RequestBody(required = false) AUserFilter filter);

	@Operation(summary = "Get a user by it's uid", description = "Return the the user's brief information", tags = {
			AdminUserApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AUser.class))) })
	@RequestMapping(value = "/user/{uid}", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public AUser getUser(@Parameter(description = "uid of user", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "find mode, return null when not found") @RequestParam(name = "find", required = false) Boolean find);

	@Operation(summary = "Create a user", description = "you must't set a uid of user, uid will be generated automatically", tags = {
			AdminUserApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "user")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AUser.class))) })
	@RequestMapping(value = "/user", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AUser createUser(
			@Parameter(description = "user-create object", required = true) @Valid @NotNull @RequestBody AUserCreate userCreate);

	@Operation(summary = "Update a user", description = "a null field of user means keep the old value, the user's uid is always required", tags = {
			AdminUserApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "user")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AUser.class))) })
	@RequestMapping(value = "/user/{uid}", method = RequestMethod.PUT, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AUser updateUser(@Parameter(description = "uid of a user", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "user-update object", required = true) @Valid @NotNull @RequestBody AUserUpdate userUpdate);

	@Operation(summary = "Delete user by it's uid", tags = {
			AdminUserApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/user/{uid}", method = RequestMethod.DELETE, produces = { TYPE_APP_JSON })
	public Response deleteUser(
			@Parameter(description = "uid of a user", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "be quiet, don't cause error if not found") @RequestParam(name = "quiet", required = false) Boolean quiet);

	@Operation(summary = "Get role list of the user", description = "return all role's information of the user", tags = {
			AdminUserApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = ARole.class)))) })
	@RequestMapping(value = "/user/{uid}/roles", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public List<ARole> listUserRole(
			@Parameter(description = "uid of a user", required = true) @PathVariable("uid") String uid);

	@Operation(summary = "Add roles to the user", description = "add roles to the user by giving a role uid list", tags = {
			AdminUserApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/user/{uid}/role", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public Response addUserRole(
			@Parameter(description = "uid of a user", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "role uid list") @Valid @NotNull @RequestBody List<String> roleUidList);

	@Operation(summary = "Set roles of the user", description = "set roles to the user by giving a role uid list, other role of the user will be removed", tags = {
			AdminUserApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/user/{uid}/role", method = RequestMethod.PUT, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public Response setUserRole(
			@Parameter(description = "uid of a user", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "role uid list") @Valid @NotNull @RequestBody List<String> roleUidList);

	@Operation(summary = "remove roles of the user", description = "remove roles of the user by giving a role uid list", tags = {
			AdminUserApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/user/{uid}/role", method = RequestMethod.DELETE, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public Response removeUserRole(
			@Parameter(description = "uid of a user", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "role uid list") @Valid @NotNull @RequestBody List<String> roleUidList);

	@Operation(summary = "Get organization list of the user", description = "return all organization's information of the user", tags = {
			AdminUserApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = AUserOrganization.class)))) })
	@RequestMapping(value = "/user/{uid}/orgs", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public List<AUserOrganization> listUserOrganization(
			@Parameter(description = "uid of a user", required = true) @PathVariable("uid") String uid);

	@Operation(summary = "Create a user by domain user", description = "provide the domain user's account and domain to create it in db", tags = {
			AdminUserApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "user")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AUser.class))) })
	@RequestMapping(value = "/create-by-domain-user", method = RequestMethod.POST, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public AUser createUserByDomainUser(
			@Parameter(description = "domain-user-create object", required = true) @Valid @NotNull @RequestBody ADomainUserCreate userCreate);
	
	
	@Operation(summary = "reset a user's preferences", tags = {
			AdminUserApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/user/{uid}/preferences", method = RequestMethod.DELETE, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public Response resetUserPreferences(
			@Parameter(description = "uid of a user", required = true) @PathVariable("uid") String uid);
}