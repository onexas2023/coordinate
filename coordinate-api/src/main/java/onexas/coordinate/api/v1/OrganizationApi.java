package onexas.coordinate.api.v1;

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
import onexas.coordinate.api.v1.model.UOrganizationUserFilter;
import onexas.coordinate.api.v1.model.UOrganizationUserListPage;
import onexas.coordinate.api.v1.model.UOrganizationUserRelation;
import onexas.coordinate.api.v1.model.UUserOrganization;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "Organization Api V1", name = OrganizationApi.API_TAG)

@RequestMapping(OrganizationApi.API_URI)
@SecurityScheme(name = NAME_AUTH_TOKEN, type = SecuritySchemeType.APIKEY, paramName = NAME_AUTH_TOKEN, in = SecuritySchemeIn.HEADER)
@ApiResponses(value = {
		@ApiResponse(responseCode = Api.HTTP_BAD_REQUEST, description = Api.MSG_BAD_REQUEST, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_UNAUTHORIZED, description = Api.MSG_UNAUTHORIZED, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_FORBIDDEN, description = Api.MSG_FORBIDDEN, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_NOT_FOUND, description = Api.MSG_NOT_FOUND, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))) })
public interface OrganizationApi extends Api {

	static public final String API_NAME = "coordinate-organization";
	static public final String API_URI = "/api/coordinate/v1/organization";
	public static final String API_TAG = API_NAME;
	public static final String API_PERMISSION_TARGET = API_NAME;

	@Operation(summary = "Get current user's organization list", description = "return user organizations's information", tags = {
			OrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = UUserOrganization.class)))) })
	@RequestMapping(value = "organizations", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public List<UUserOrganization> listOrganization();

	@Operation(summary = "Get organization of user by code", description = "return user organizations's information", tags = {
			OrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = UUserOrganization.class))) })
	@RequestMapping(value = "/organization/{code}", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public UUserOrganization getOrganization(
			@Parameter(description = "code of a organization", required = true) @PathVariable("code") String code,
			@Parameter(description = "find mode, return null when not found") @RequestParam(name = "find", required = false) Boolean find);

	/*
	 * Organization User Api
	 */
	@Operation(summary = "Get user list of the organization", description = "Return all user's information of the organization. The current user must be a admin of the organization", tags = {
			OrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "filter")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = UOrganizationUserListPage.class))) })
	@RequestMapping(value = "/organization/{code}/users", method = RequestMethod.POST, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public UOrganizationUserListPage listOrganizationUser(
			@Parameter(description = "code of a organization", required = true) @PathVariable("code") String code,
			@Parameter(description = "organization-filter object") @Valid @RequestBody(required = false) UOrganizationUserFilter filter);

	@Operation(summary = "Add users to the organization", description = "Add users to the organization by giving a user uid list. The current user must be a admin of the organization", tags = {
			OrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/organization/{code}/user", method = RequestMethod.POST, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public Response addOrganizationUser(
			@Parameter(description = "code of a organization", required = true) @PathVariable("code") String code,
			@Parameter(description = "user relation list") @Valid @NotNull @RequestBody List<UOrganizationUserRelation> userRelationList);

	@Operation(summary = "Set users of the organization", description = "Set users to the organization by giving a user uid list, other user of the organization will be removed. The current user must be a admin of the organization", tags = {
			OrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/organization/{code}/user", method = RequestMethod.PUT, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public Response setOrganizationUser(
			@Parameter(description = "code of a organization", required = true) @PathVariable("code") String code,
			@Parameter(description = "user relation list") @Valid @NotNull @RequestBody List<UOrganizationUserRelation> userRelationList);

	@Operation(summary = "remove users of the organization", description = "Remove users of the organization by giving a user aliasUid list, The current user must be a admin of the organization", tags = {
			OrganizationApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "list")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/organization/{code}/user", method = RequestMethod.DELETE, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public Response removeOrganizationUsers(
			@Parameter(description = "code of a organization", required = true) @PathVariable("code") String code,
			@Parameter(description = "user uid list") @Valid @NotNull @RequestBody List<String> userAliasUidList);
}