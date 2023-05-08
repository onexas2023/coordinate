package onexas.coordinate.api.v1.admin;

import static onexas.coordinate.web.api.Api.NAME_AUTH_TOKEN;

import java.util.Map;

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
import onexas.coordinate.api.v1.admin.model.AHook;
import onexas.coordinate.api.v1.admin.model.AHookCreate;
import onexas.coordinate.api.v1.admin.model.AHookFilter;
import onexas.coordinate.api.v1.admin.model.AHookListPage;
import onexas.coordinate.api.v1.admin.model.AHookUpdate;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "Hook Api V1", name = AdminHookApi.API_TAG)

@RequestMapping(AdminHookApi.API_URI)
@SecurityScheme(name = NAME_AUTH_TOKEN, type = SecuritySchemeType.APIKEY, paramName = NAME_AUTH_TOKEN, in = SecuritySchemeIn.HEADER)
@ApiResponses(value = {
		@ApiResponse(responseCode = Api.HTTP_BAD_REQUEST, description = Api.MSG_BAD_REQUEST, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_UNAUTHORIZED, description = Api.MSG_UNAUTHORIZED, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_FORBIDDEN, description = Api.MSG_FORBIDDEN, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_NOT_FOUND, description = Api.MSG_NOT_FOUND, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))) })
public interface AdminHookApi extends Api {

	static public final String API_NAME = "coordinate-admin-hook";
	static public final String API_URI = "/api/coordinate/v1/admin/hook";
	public static final String API_TAG = API_NAME;
	public static final String API_PERMISSION_TARGET = API_NAME;

	@Operation(summary = "Get hook list with a hook-filter", description = "return filtered hook's information", tags = {
			AdminHookApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "filter")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AHookListPage.class))) })
	@RequestMapping(value = "/hooks", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AHookListPage listHook(
			@Parameter(description = "hook-filter object") @Valid @RequestBody(required = false) AHookFilter filter);

	@Operation(summary = "Get a hook by it's uid", description = "Return the the hook's brief information", tags = {
			AdminHookApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AHook.class))) })
	@RequestMapping(value = "/hook/{uid}", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public AHook getHook(@Parameter(description = "uid of hook", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "find mode, return null when not found") @RequestParam(name = "find", required = false) Boolean find);

	@Operation(summary = "Create a hook", description = "you must't set a uid of hook, uid will be generated automatically", tags = {
			AdminHookApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "hook")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AHook.class))) })
	@RequestMapping(value = "/hook", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AHook createHook(
			@Parameter(description = "hook-create object", required = true) @Valid @NotNull @RequestBody AHookCreate hookCreate);

	@Operation(summary = "Update a hook", description = "a null field of hook means keep the old value, the hook's uid is always required", tags = {
			AdminHookApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "hook")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AHook.class))) })
	@RequestMapping(value = "/hook/{uid}", method = RequestMethod.PUT, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AHook updateHook(@Parameter(description = "uid of a hook", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "hook-update object", required = true) @Valid @NotNull @RequestBody AHookUpdate hookUpdate);

	@Operation(summary = "Delete hook by it's uid", tags = {
			AdminHookApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/hook/{uid}", method = RequestMethod.DELETE, produces = { TYPE_APP_JSON })
	public Response deleteHook(
			@Parameter(description = "uid of a hook", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "be quiet, don't cause error if not found") @RequestParam(name = "quiet", required = false) Boolean quiet);

	@Operation(summary = "Trigger hook by it's uid", tags = {
			AdminHookApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AHook.class))) })
	@RequestMapping(value = "/trigger/{uid}", method = RequestMethod.GET, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AHook triggerHook(
			@Parameter(description = "uid of a hook", required = true) @PathVariable("uid") String uid);

	@Operation(summary = "Trigger hook by it's uid", tags = {
			AdminHookApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "args")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AHook.class))) })
	@RequestMapping(value = "/trigger/{uid}", method = RequestMethod.PUT, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AHook triggerHookWithArgs(
			@Parameter(description = "uid of a hook", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "hook-args object", required = true, schema = @Schema(implementation = Object.class)) @Valid @RequestBody Map<String, Object> args);
}