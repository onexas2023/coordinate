package onexas.coordinate.api.v1.admin;

import static onexas.coordinate.web.api.Api.NAME_AUTH_TOKEN;

import java.util.List;
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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import onexas.coordinate.api.v1.admin.model.ADomain;
import onexas.coordinate.api.v1.admin.model.ADomainConfigCheck;
import onexas.coordinate.api.v1.admin.model.ADomainCreate;
import onexas.coordinate.api.v1.admin.model.ADomainProviderFactory;
import onexas.coordinate.api.v1.admin.model.ADomainUpdate;
import onexas.coordinate.api.v1.admin.model.ADomainUserFilter;
import onexas.coordinate.api.v1.admin.model.ADomainUserListPage;
import onexas.coordinate.common.model.SchemaMap;
import onexas.coordinate.web.api.Api;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Tag(description = "Domain Api V1", name = AdminDomainApi.API_TAG)

@RequestMapping(AdminDomainApi.API_URI)
@SecurityScheme(name = NAME_AUTH_TOKEN, type = SecuritySchemeType.APIKEY, paramName = NAME_AUTH_TOKEN, in = SecuritySchemeIn.HEADER)
@ApiResponses(value = {
		@ApiResponse(responseCode = Api.HTTP_BAD_REQUEST, description = Api.MSG_BAD_REQUEST, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_UNAUTHORIZED, description = Api.MSG_UNAUTHORIZED, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_FORBIDDEN, description = Api.MSG_FORBIDDEN, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))),
		@ApiResponse(responseCode = Api.HTTP_NOT_FOUND, description = Api.MSG_NOT_FOUND, content = @Content(mediaType = Api.TYPE_APP_JSON, schema = @Schema(implementation = Response.class))) })
public interface AdminDomainApi extends Api {

	static public final String API_NAME = "coordinate-admin-domain";
	static public final String API_URI = "/api/coordinate/v1/admin/domain";
	static public final String API_TAG = API_NAME;
	static public final String API_PERMISSION_TARGET = API_NAME;

	@Operation(summary = "Get domain list", description = "return all domains's information", tags = {
			AdminDomainApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = ADomain.class)))) })
	@RequestMapping(value = "/domains", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public List<ADomain> listDomain();

	@Operation(summary = "Get a domain by it's uid", description = "Return the the domain's brief information", tags = {
			AdminDomainApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = ADomain.class))) })
	@RequestMapping(value = "/domain/{code}", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public ADomain getDomain(
			@Parameter(description = "uid of domain", required = true) @PathVariable("code") String code,
			@Parameter(description = "find mode, return null when not found") @RequestParam(name = "find", required = false) Boolean find);

	@Operation(summary = "Create a domain", description = "you must't set a uid of domain, uid will be generated automatically", tags = {
			AdminDomainApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "domain")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = ADomain.class))) })
	@RequestMapping(value = "/domain", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public ADomain createDomain(
			@Parameter(description = "domain-create object", required = true) @Valid @NotNull @RequestBody ADomainCreate domainCreate);

	@Operation(summary = "Update a domain", description = "a null field of domain means keep the old value, the domain's uid is always required", tags = {
			AdminDomainApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "domain")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = ADomain.class))) })
	@RequestMapping(value = "/domain/{code}", method = RequestMethod.PUT, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public ADomain updateDomain(
			@Parameter(description = "code of a domain", required = true) @PathVariable("code") String code,
			@Parameter(description = "domain-update object", required = true) @Valid @NotNull @RequestBody ADomainUpdate domainUpdate);

	@Operation(summary = "Delete domain by it's code", tags = {
			AdminDomainApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/domain/{code}", method = RequestMethod.DELETE, produces = { TYPE_APP_JSON })
	public Response deleteDomain(
			@Parameter(description = "code of a domain", required = true) @PathVariable("code") String code,
			@Parameter(description = "be quiet, don't cause error if not found") @RequestParam(name = "quiet", required = false) Boolean quiet);

	@Operation(summary = "Get config object of the domain", tags = {
			AdminDomainApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = SchemaMap.class))) })
	@RequestMapping(value = "/domain/{code}/config", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public Map<String,Object> getDomainConfig(
			@Parameter(description = "code of a domain", required = true) @PathVariable("code") String code);

	@Operation(summary = "Get config yaml string of the domain", tags = {
			AdminDomainApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = String.class))) })
	@RequestMapping(value = "/domain/{code}/config-yaml", method = RequestMethod.GET, produces = { TYPE_TEXT_PLAIN })
	public String getDomainConfigYaml(
			@Parameter(description = "code of a domain", required = true) @PathVariable("code") String code);

	@Operation(summary = "Get domain user list with a filter", description = "return filtered domain user's information", tags = {
			AdminDomainApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "filter")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = ADomainUserListPage.class))) })
	@RequestMapping(value = "/domain/{code}/users", method = RequestMethod.POST, consumes = {
			TYPE_APP_JSON }, produces = { TYPE_APP_JSON })
	public ADomainUserListPage listDomainUser(
			@Parameter(description = "code of a domain", required = true) @PathVariable("code") String code,
			@Parameter(description = "domain-user-filter object") @Valid @RequestBody(required = false) ADomainUserFilter filter);

	@Operation(summary = "Get domain provider factory list", description = "return domain DomainProviderFactory information", tags = {
			AdminDomainApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN))
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = ADomainProviderFactory.class)))) })
	@RequestMapping(value = "/factories", method = RequestMethod.GET, produces = { TYPE_APP_JSON })
	public List<ADomainProviderFactory> listDomainProviderFactory();

	@Operation(summary = "Check a domain config", tags = {
			AdminDomainApi.API_TAG }, security = @SecurityRequirement(name = NAME_AUTH_TOKEN), extensions = {
					@Extension(properties = @ExtensionProperty(name = CODE_GEN_REQ_BODY_NAME, value = "config")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = HTTP_OK, description = MSG_OK, content = @Content(schema = @Schema(implementation = Response.class))) })
	@RequestMapping(value = "/config-check", method = RequestMethod.POST, consumes = { TYPE_APP_JSON }, produces = {
			TYPE_APP_JSON })
	public Response checkDomainConfig(
			@Parameter(description = "domain-config-check object", required = true) @NotNull @Valid @NotNull @RequestBody ADomainConfigCheck domainConfigCheck);

}