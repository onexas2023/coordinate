package onexas.coordinate.api.v1;

import static onexas.coordinate.web.api.Api.NAME_AUTH_TOKEN;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import onexas.coordinate.api.v1.model.UDomain;
import onexas.coordinate.api.v1.model.UMetainfo;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "Metainfo Api V1", name = MetainfoApi.API_TAG)
@RequestMapping(MetainfoApi.API_URI)
@SecurityScheme(name = NAME_AUTH_TOKEN, type = SecuritySchemeType.APIKEY, paramName = NAME_AUTH_TOKEN, in = SecuritySchemeIn.HEADER)
@ApiResponses(value = {
		@ApiResponse(responseCode = Api.HTTP_BAD_REQUEST, description = Api.MSG_BAD_REQUEST, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_UNAUTHORIZED, description = Api.MSG_UNAUTHORIZED, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_FORBIDDEN, description = Api.MSG_FORBIDDEN, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_NOT_FOUND, description = Api.MSG_NOT_FOUND, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))) })
public interface MetainfoApi extends Api {

	static public final String API_NAME = "coordinate-metainfo";
	static public final String API_URI = "/api/coordinate/v1/metainfo";
	static public final String API_FUNCTION = API_NAME;
	static public final String API_TAG = API_NAME;

	@Operation(summary = "Get domain list", description = "return all domain's information", tags = {
			MetainfoApi.API_TAG })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = UDomain.class)))) })
	@RequestMapping(value = "/domains", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public List<UDomain> listDomain();

	@Operation(summary = "Get metainfo", description = "return public metadata information", tags = {
			MetainfoApi.API_TAG })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = UMetainfo.class))) })
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public UMetainfo getMetainfo();
}