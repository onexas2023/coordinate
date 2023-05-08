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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import onexas.coordinate.api.v1.admin.model.AOrganization;
import onexas.coordinate.api.v1.admin.model.AOrganizationCreate;
import onexas.coordinate.api.v1.admin.model.AOrganizationFilter;
import onexas.coordinate.api.v1.admin.model.AOrganizationListPage;
import onexas.coordinate.api.v1.admin.model.AOrganizationUpdate;
import onexas.coordinate.api.v1.admin.model.AOrganizationUserFilter;
import onexas.coordinate.api.v1.admin.model.AOrganizationUserListPage;
import onexas.coordinate.api.v1.admin.model.AOrganizationUserRelation;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "Organization Api V1", name = AdminOrganizationApi.API_TAG)

@RequestMapping(AdminOrganizationApi.API_URI)
@SecurityScheme(name = NAME_AUTH_TOKEN, type = SecuritySchemeType.APIKEY, paramName = NAME_AUTH_TOKEN, in = SecuritySchemeIn.HEADER)
@ApiResponses(value = {
		@ApiResponse(responseCode = Api.HTTP_BAD_REQUEST, description = Api.MSG_BAD_REQUEST, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_UNAUTHORIZED, description = Api.MSG_UNAUTHORIZED, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_FORBIDDEN, description = Api.MSG_FORBIDDEN, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_NOT_FOUND, description = Api.MSG_NOT_FOUND, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))) })
public interface AdminOrganizationApi extends Api {

	static public final String API_NAME = "coordinate-admin-organization";
	static public final String API_URI = "/api/coordinate/v1/admin/organization";
	public static final String API_TAG = API_NAME;
	public static final String API_PERMISSION_TARGET = API_NAME;

	@Operation(summary = "Get organization list with a organization-filter", description = "return filtered organization's information", tags = {
			AdminOrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "filter")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AOrganizationListPage.class))) })
	@RequestMapping(value = "/organizations", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AOrganizationListPage listOrganization(
			@Parameter(description = "organization-filter object") @Valid @RequestBody(required = false) AOrganizationFilter filter);

	@Operation(summary = "Get a organization by it's uid", description = "Return the the organization's brief information", tags = {
			AdminOrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AOrganization.class))) })
	@RequestMapping(value = "/organization/{uid}", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public AOrganization getOrganization(
			@Parameter(description = "uid of organization", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "find mode, return null when not found") @RequestParam(name = "find", required = false) Boolean find);

	@Operation(summary = "Create a organization", description = "you must't set a uid of organization, uid will be generated automatically", tags = {
			AdminOrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "organization")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AOrganization.class))) })
	@RequestMapping(value = "/organization", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AOrganization createOrganization(
			@Parameter(description = "organization-create object", required = true) @Valid @NotNull @RequestBody AOrganizationCreate organizationCreate);

	@Operation(summary = "Update a organization", description = "a null field of organization means keep the old value, the organization's uid is always required", tags = {
			AdminOrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "organization")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AOrganization.class))) })
	@RequestMapping(value = "/organization/{uid}", method = RequestMethod.PUT, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public AOrganization updateOrganization(
			@Parameter(description = "uid of a organization", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "organization-update object", required = true) @Valid @NotNull @RequestBody AOrganizationUpdate organizationUpdate);

	@Operation(summary = "Delete organization by it's uid", tags = {
			AdminOrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/organization/{uid}", method = RequestMethod.DELETE, produces = { TYPE_APP_JSON })
	public Response deleteOrganization(
			@Parameter(description = "uid of a organization", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "be quiet, don't cause error if not found") @RequestParam(name = "quiet", required = false) Boolean quiet);

	/*
	 * Organization User Api
	 */
	@Operation(summary = "Get user list of the organization with a page-filter ", description = "return filtered user's information of the organization", tags = {
			AdminOrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "filter")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AOrganizationUserListPage.class))) })
	@RequestMapping(value = "/organization/{uid}/users", method = RequestMethod.POST, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public AOrganizationUserListPage listOrganizationUser(
			@Parameter(description = "uid of a organization", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "organization-filter object") @Valid @RequestBody(required = false) AOrganizationUserFilter filter);

	@Operation(summary = "Add users to the organization", description = "add users to the organization by giving a user uid list", tags = {
			AdminOrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/organization/{uid}/user", method = RequestMethod.POST, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public Response addOrganizationUser(
			@Parameter(description = "uid of a organization", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "user relation list") @Valid @NotNull @RequestBody List<AOrganizationUserRelation> userRelationList);

	@Operation(summary = "Set users of the organization", description = "set users to the organization by giving a user uid list, other user of the organization will be removed", tags = {
			AdminOrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/organization/{uid}/user", method = RequestMethod.PUT, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public Response setOrganizationUser(
			@Parameter(description = "uid of a organization", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "user relation list") @Valid @NotNull @RequestBody List<AOrganizationUserRelation> userRelationList);

	@Operation(summary = "remove users of the organization", description = "remove users of the organization by giving a user uid list", tags = {
			AdminOrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/organization/{uid}/user", method = RequestMethod.DELETE, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public Response removeOrganizationUser(
			@Parameter(description = "uid of a organization", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "user uid list") @Valid @NotNull @RequestBody List<String> userUidList);
}