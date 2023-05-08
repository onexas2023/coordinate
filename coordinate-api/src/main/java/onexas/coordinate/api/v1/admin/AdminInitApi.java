package onexas.coordinate.api.v1.admin;

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
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import onexas.coordinate.api.v1.admin.model.AAdmin;
import onexas.coordinate.api.v1.admin.model.AInitDemoRequest;
import onexas.coordinate.api.v1.admin.model.AInitRequest;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "Init Api V1", name = AdminInitApi.API_TAG)

@RequestMapping(AdminInitApi.API_URI)
@SecurityScheme(name = NAME_AUTH_TOKEN, type = SecuritySchemeType.APIKEY, paramName = NAME_AUTH_TOKEN, in = SecuritySchemeIn.HEADER)
public interface AdminInitApi extends Api {

	static public final String API_NAME = "coordinate-admin-init";
	static public final String API_URI = "/api/coordinate/v1/admin/init";
	static public final String API_FUNCTION = API_NAME;
	static public final String API_TAG = API_NAME;

	@Operation(summary = "Create admin user and administrators role at the first time only", description = "Allowed only when there is no user, no role. the password is in the return messages", tags = {
			AdminInitApi.API_TAG }, extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "request")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AAdmin.class))),
			@ApiResponse(responseCode = HTTP_BAD_REQUEST, description = MSG_BAD_REQUEST, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_UNAUTHORIZED, description = MSG_UNAUTHORIZED, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/admin", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AAdmin initAdmin(
			@Parameter(description = "init request object", required = true) @Valid @NotNull @RequestBody AInitRequest initRequest);

	@Operation(summary = "Create demo data by given case", tags = { AdminInitApi.API_TAG }, extensions = {
			@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "request")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_BAD_REQUEST, description = MSG_BAD_REQUEST, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_UNAUTHORIZED, description = MSG_UNAUTHORIZED, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/demo", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public Response initDemo(
			@Parameter(description = "init request object", required = true) @Valid @NotNull @RequestBody AInitDemoRequest initDemoRequest);
}