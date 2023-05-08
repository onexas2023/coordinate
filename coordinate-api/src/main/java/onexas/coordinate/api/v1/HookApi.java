package onexas.coordinate.api.v1;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import onexas.coordinate.api.v1.admin.AdminHookApi;
import onexas.coordinate.api.v1.model.UHook;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "Hook Api V1", name = HookApi.API_TAG)
@RequestMapping(HookApi.API_URI)
@ApiResponses(value = {
		@ApiResponse(responseCode = Api.HTTP_BAD_REQUEST, description = Api.MSG_BAD_REQUEST, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_UNAUTHORIZED, description = Api.MSG_UNAUTHORIZED, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_FORBIDDEN, description = Api.MSG_FORBIDDEN, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_NOT_FOUND, description = Api.MSG_NOT_FOUND, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))) })
public interface HookApi extends Api {

	static public final String API_NAME = "coordinate-hook";
	static public final String API_URI = "/api/coordinate/v1/hook";
	static public final String API_FUNCTION = API_NAME;
	static public final String API_TAG = API_NAME;

	// the type here is to make it more easy to filter by proxy for any security
	// reason
	@Operation(summary = "Trigger hook by it's type and uid", tags = {
			AdminHookApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = UHook.class))) })
	@RequestMapping(value = "/trigger/{zone}/{uid}", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public UHook triggerHook(
			@Parameter(description = "zone of a hook", required = true) @PathVariable("zone") String zone,
			@Parameter(description = "uid of a hook", required = true) @PathVariable("uid") String uid);

	@Operation(summary = "Trigger hook by it's type and uid", tags = {
			AdminHookApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "args")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = UHook.class))) })
	@RequestMapping(value = "/trigger/{zone}/{uid}", method = RequestMethod.PUT, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public UHook triggerHookWithArgs(
			@Parameter(description = "type of a hook", required = true) @PathVariable("zone") String zone,
			@Parameter(description = "uid of a hook", required = true) @PathVariable("uid") String uid,
			@Parameter(description = "hook-args object", required = true, schema = @Schema(implementation = Object.class)) @Valid @RequestBody Map<String, Object> args);

}