package onexas.coordinate.api.v1.admin;

import static onexas.coordinate.web.api.Api.NAME_AUTH_TOKEN;

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
import onexas.coordinate.api.v1.admin.model.ASecret;
import onexas.coordinate.api.v1.admin.model.ASecretCreate;
import onexas.coordinate.api.v1.admin.model.ASecretFilter;
import onexas.coordinate.api.v1.admin.model.ASecretListPage;
import onexas.coordinate.api.v1.admin.model.ASecretUpdate;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "Secret Api V1", name = AdminSecretApi.API_TAG)

@RequestMapping(AdminSecretApi.API_URI)
@SecurityScheme(name = NAME_AUTH_TOKEN, type = SecuritySchemeType.APIKEY, paramName = NAME_AUTH_TOKEN, in = SecuritySchemeIn.HEADER)
@ApiResponses(value = {
		@ApiResponse(responseCode = Api.HTTP_BAD_REQUEST, description = Api.MSG_BAD_REQUEST, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_UNAUTHORIZED, description = Api.MSG_UNAUTHORIZED, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_FORBIDDEN, description = Api.MSG_FORBIDDEN, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_NOT_FOUND, description = Api.MSG_NOT_FOUND, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))) })
public interface AdminSecretApi extends Api {

	static public final String API_NAME = "coordinate-admin-secret";
	static public final String API_URI = "/api/coordinate/v1/admin/secret";
	static public final String API_TAG = API_NAME;
	static public final String API_PERMISSION_TARGET = API_NAME;

	@Operation(summary = "Get secret list with a filter", description = "return filtered secret's information", tags = {
			AdminSecretApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "filter")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = ASecretListPage.class))) })
	@RequestMapping(value = "/secrets", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public ASecretListPage listSecret(
			@Parameter(description = "secret-filter object") @Valid @RequestBody(required = false) ASecretFilter filter);

	@Operation(summary = "Get a secret by it's uid", description = "Return the the secret's brief information", tags = {
			AdminSecretApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = ASecret.class))) })
	@RequestMapping(value = "/secret/{uid}", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public ASecret getSecret(@Parameter(description = "uid of secret", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "find mode, return null when not found") @RequestParam(name = "find", required = false) Boolean find);

	@Operation(summary = "Create a secret", description = "you must't set a uid of secret, uid will be generated automatically", tags = {
			AdminSecretApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "secret")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = ASecret.class))) })
	@RequestMapping(value = "/secret", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public ASecret createSecret(
			@Parameter(description = "secret-create object", required = true) @Valid @NotNull @RequestBody ASecretCreate secretCreate);

	@Operation(summary = "Update a secret", description = "a null field of secret means keep the old value, the secret's uid is always required", tags = {
			AdminSecretApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "secret")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = ASecret.class))) })
	@RequestMapping(value = "/secret/{uid}", method = RequestMethod.PUT, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public ASecret updateSecret(@Parameter(description = "uid of a secret", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "secret-update object", required = true) @Valid @NotNull @RequestBody ASecretUpdate secretUpdate);

	@Operation(summary = "Delete secret by it's uid", tags = {
			AdminSecretApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/secret/{uid}", method = RequestMethod.DELETE, produces = { TYPE_APP_JSON })
	public Response deleteSecret(
			@Parameter(description = "uid of a secret", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "be quiet, don't cause error if not found") @RequestParam(name = "quiet", required = false) Boolean quiet);
	
	//don't provide secret content to api, it is for internal service only
}