package onexas.coordinate.api.v1;

import static onexas.coordinate.web.api.Api.NAME_AUTH_TOKEN;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PathVariable;
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
import onexas.coordinate.api.v1.model.PreferenceMap;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "Preference Api V1", name = PreferenceApi.API_TAG)

@RequestMapping(PreferenceApi.API_URI)
@SecurityScheme(name = NAME_AUTH_TOKEN, type = SecuritySchemeType.APIKEY, paramName = NAME_AUTH_TOKEN, in = SecuritySchemeIn.HEADER)
public interface PreferenceApi extends Api {

	static public final String API_NAME = "coordinate-preference";
	static public final String API_URI = "/api/coordinate/v1/preference";
	static public final String API_FUNCTION = API_NAME;
	static public final String API_TAG = API_NAME;

	static public final String API_PERMISSION_TARGET = API_NAME;

	@Operation(summary = "get preferences of current user", tags = {
			API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = PreferenceMap.class))),
			@ApiResponse(responseCode = HTTP_UNAUTHORIZED, description = MSG_UNAUTHORIZED, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_FORBIDDEN, description = MSG_FORBIDDEN, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/preferences", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public Map<String, String> getPreferences();
	
	@Operation(summary = "find preferences of current user", tags = {
			API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = HTTP_UNAUTHORIZED, description = MSG_UNAUTHORIZED, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_FORBIDDEN, description = MSG_FORBIDDEN, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/preference/{key}", method = RequestMethod.GET, produces = { TYPE_TEXT_PLAIN })
	public String findPreference(
			@Parameter(description = "kef of a user preference", required = true) @PathVariable("key") String key);

	@Operation(summary = "update preferences of current user", tags = {
			API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "preferences")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = PreferenceMap.class))),
			@ApiResponse(responseCode = HTTP_BAD_REQUEST, description = MSG_BAD_REQUEST, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_UNAUTHORIZED, description = MSG_UNAUTHORIZED, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_FORBIDDEN, description = MSG_FORBIDDEN, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/preferences", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public Map<String, String> updatePreferences(
			@Parameter(description = "preference update", content = @Content(schema = @Schema(implementation = PreferenceMap.class))) 
			@Valid @NotNull @RequestBody(required = true) Map<String, String> preferenceUpdate);
	
	
	@Operation(summary = "update a preference of current user by key", tags = {
			API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "value")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = HTTP_BAD_REQUEST, description = MSG_BAD_REQUEST, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_UNAUTHORIZED, description = MSG_UNAUTHORIZED, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_FORBIDDEN, description = MSG_FORBIDDEN, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/preference/{key}", method = RequestMethod.PUT, consumes = { TYPE_TEXT_PLAIN }, produces = {
			TYPE_TEXT_PLAIN })
	public String updatePreference(
			@Parameter(description = "key of the preference", required = true) @PathVariable("key") String key,
			@Parameter(description = "value of the preference") 
			@Valid @NotNull @RequestBody(required = true) String value);

	@Operation(summary = "reset preferences of current user", tags = {
			API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "preferences")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = PreferenceMap.class))),
			@ApiResponse(responseCode = HTTP_BAD_REQUEST, description = MSG_BAD_REQUEST, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_UNAUTHORIZED, description = MSG_UNAUTHORIZED, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_FORBIDDEN, description = MSG_FORBIDDEN, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/preferences", method = RequestMethod.PUT, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public Map<String, String> resetPreferences(
			@Parameter(description = "preference reset", content = @Content(schema = @Schema(implementation = PreferenceMap.class))) 
			@Valid @NotNull @RequestBody(required = true) Map<String, String> preferenceReset);

}
