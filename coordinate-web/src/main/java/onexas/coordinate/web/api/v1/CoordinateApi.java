package onexas.coordinate.web.api.v1;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import onexas.coordinate.web.api.model.Response;
import onexas.coordinate.web.api.v1.model.ApiInfo;

@Tag(name = CoordinateApi.API_TAG, description = "Coordinate Api Infomation")
@RequestMapping(CoordinateApi.API_URI)
public interface CoordinateApi extends onexas.coordinate.web.api.Api {

	static public final String API_NAME = "coordinate";
	static public final String API_URI = "/api/coordinate/v1";
	static public final String API_TAG = API_NAME;

	@Operation(summary = "List supported api infos", description = "return available api info list of coordinate", tags = {
			API_TAG })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiInfo.class)))),
			@ApiResponse(responseCode = HTTP_BAD_REQUEST, description = MSG_BAD_REQUEST, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_UNAUTHORIZED, description = MSG_UNAUTHORIZED, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_FORBIDDEN, description = MSG_FORBIDDEN, content = @Content(schema = @Schema(implementation = Response.class))),
			@ApiResponse(responseCode = HTTP_NOT_FOUND, description = MSG_NOT_FOUND, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(produces = { TYPE_APP_JSON }, method = RequestMethod.GET)
	public List<ApiInfo> listApiInfos();
}