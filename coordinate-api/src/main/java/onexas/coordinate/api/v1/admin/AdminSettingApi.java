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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import onexas.coordinate.api.v1.admin.model.AServerSetting;
import onexas.coordinate.api.v1.admin.model.AServerSettingUpdate;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "Setting Api V1", name = AdminSettingApi.API_TAG)

@RequestMapping(AdminSettingApi.API_URI)
@SecurityScheme(name = NAME_AUTH_TOKEN, type = SecuritySchemeType.APIKEY, paramName = NAME_AUTH_TOKEN, in = SecuritySchemeIn.HEADER)
@ApiResponses(value = {
		@ApiResponse(responseCode = Api.HTTP_BAD_REQUEST, description = Api.MSG_BAD_REQUEST, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_UNAUTHORIZED, description = Api.MSG_UNAUTHORIZED, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_FORBIDDEN, description = Api.MSG_FORBIDDEN, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_NOT_FOUND, description = Api.MSG_NOT_FOUND, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))) })
public interface AdminSettingApi extends Api {

	static public final String API_NAME = "coordinate-admin-setting";
	static public final String API_URI = "/api/coordinate/v1/admin/setting";
	public static final String API_TAG = API_NAME;
	public static final String API_PERMISSION_TARGET = API_NAME;

	@Operation(summary = "Get server setting", tags = {
			AdminSettingApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AServerSetting.class))) })
	@RequestMapping(value = "/server", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public AServerSetting getServerSetting();

	@Operation(summary = "Update server setting", tags = {
			AdminSettingApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "setting")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AServerSetting.class))) })
	@RequestMapping(value = "/server", method = RequestMethod.PUT, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AServerSetting updateServerSetting(
			@Parameter(description = "server-setting-update object", required = true) @NotNull @Valid @NotNull @RequestBody AServerSettingUpdate settingUpdate);

	@Operation(summary = "Reset server setting", tags = {
			AdminSettingApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = AServerSetting.class))) })
	@RequestMapping(value = "/server", method = RequestMethod.DELETE, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public AServerSetting resetServerSetting();

	@Operation(summary = "clean cache", tags = {
			AdminSettingApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/clean-cache", method = RequestMethod.GET, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public Response cleanCache();

}