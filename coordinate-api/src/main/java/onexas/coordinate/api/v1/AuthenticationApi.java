package onexas.coordinate.api.v1;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
import io.swagger.v3.oas.annotations.tags.Tag;
import onexas.coordinate.api.v1.model.Authentication;
import onexas.coordinate.api.v1.model.AuthenticationRequest;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "Authentication Api V1", name = AuthenticationApi.API_TAG)
@RequestMapping(AuthenticationApi.API_URI)
public interface AuthenticationApi extends Api {

	static public final String API_NAME = "coordinate-auth";
	static public final String API_URI = "/api/coordinate/v1/auth";
	static public final String API_FUNCTION = API_NAME;
	static public final String API_TAG = API_NAME;

	@Operation(summary = "authenticate by account/password/domain or token", description = "return the valid token back and user's permission", tags = {
			API_TAG }, extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "request")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Authentication.class))),
			@ApiResponse(responseCode = HTTP_BAD_REQUEST, description = MSG_BAD_REQUEST, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_UNAUTHORIZED, description = "wrong account/password/domain or invalid token", content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_FORBIDDEN, description = "relates user is disabled", content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/auth", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public Authentication authenticate(
			@Parameter(description = "authentication request") @Valid @NotNull @RequestBody(required = false) AuthenticationRequest request);
}
