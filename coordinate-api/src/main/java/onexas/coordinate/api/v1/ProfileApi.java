package onexas.coordinate.api.v1;

import static onexas.coordinate.web.api.Api.NAME_AUTH_TOKEN;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
import onexas.coordinate.api.v1.model.UPasswordUpdate;
import onexas.coordinate.api.v1.model.UProfile;
import onexas.coordinate.api.v1.model.UProfileUpdate;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "Profile Api V1", name = ProfileApi.API_TAG)

@RequestMapping(ProfileApi.API_URI)
@SecurityScheme(name = NAME_AUTH_TOKEN, type = SecuritySchemeType.APIKEY, paramName = NAME_AUTH_TOKEN, in = SecuritySchemeIn.HEADER)
public interface ProfileApi extends Api {

	static public final String API_NAME = "coordinate-profile";
	static public final String API_URI = "/api/coordinate/v1/profile";
	static public final String API_FUNCTION = API_NAME;
	static public final String API_TAG = API_NAME;
	
	static public final String API_PERMISSION_TARGET = API_NAME;

	@Operation(summary = "update password of current user", tags = {
			API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "password")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_BAD_REQUEST, description = MSG_BAD_REQUEST, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_UNAUTHORIZED, description = MSG_UNAUTHORIZED, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_FORBIDDEN, description = MSG_FORBIDDEN, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/password", method = RequestMethod.PUT, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public Response updatePassword(
			@Parameter(description = "password update") @Valid @NotNull @RequestBody(required = false) UPasswordUpdate passwordUpdate);

	@Operation(summary = "get profile of current user", tags = {
			API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = UProfile.class))),
			@ApiResponse(responseCode = HTTP_UNAUTHORIZED, description = MSG_UNAUTHORIZED, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_FORBIDDEN, description = MSG_FORBIDDEN, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/profile", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public UProfile getProfile();

	@Operation(summary = "update profile of current user", tags = {
			API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "profile")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = UProfile.class))),
			@ApiResponse(responseCode = HTTP_BAD_REQUEST, description = MSG_BAD_REQUEST, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_UNAUTHORIZED, description = MSG_UNAUTHORIZED, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_FORBIDDEN, description = MSG_FORBIDDEN, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/profile", method = RequestMethod.PUT, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public UProfile updateProfile(
			@Parameter(description = "profile update") @Valid @NotNull @RequestBody(required = false) UProfileUpdate profileUpdate);

}
