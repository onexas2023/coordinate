package onexas.coordinate.api.v1.admin.impl;

import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.admin.AdminOrganizationApi;
import onexas.coordinate.api.v1.admin.model.AOrganization;
import onexas.coordinate.api.v1.admin.model.AOrganizationCreate;
import onexas.coordinate.api.v1.admin.model.AOrganizationFilter;
import onexas.coordinate.api.v1.admin.model.AOrganizationListPage;
import onexas.coordinate.api.v1.admin.model.AOrganizationUpdate;
import onexas.coordinate.api.v1.admin.model.AOrganizationUser;
import onexas.coordinate.api.v1.admin.model.AOrganizationUserFilter;
import onexas.coordinate.api.v1.admin.model.AOrganizationUserListPage;
import onexas.coordinate.api.v1.admin.model.AOrganizationUserRelation;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.Organization;
import onexas.coordinate.model.OrganizationUser;
import onexas.coordinate.service.OrganizationService;
import onexas.coordinate.web.api.impl.ApiImplBase;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "AdminOrganizationApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantPermissions(@GrantPermission(target = AdminOrganizationApi.API_PERMISSION_TARGET, action = {
		AdminOrganizationApi.ACTION_VIEW, AdminOrganizationApi.ACTION_MODIFY, AdminOrganizationApi.ACTION_ADMIN }))
public class AdminOrganizationApiImpl extends ApiImplBase implements AdminOrganizationApi {

	@Autowired
	OrganizationService organizationService;

	public AdminOrganizationApiImpl() {
		super(AdminOrganizationApi.API_NAME, V1, AdminOrganizationApi.API_URI);
	}

	@Override
	public AOrganization getOrganization(String uid, Boolean find) {
		return Jsons.transform(Boolean.TRUE.equals(find) ? organizationService.find(uid) : organizationService.get(uid),
				AOrganization.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminOrganizationApi.API_PERMISSION_TARGET, action = {
			AdminOrganizationApi.ACTION_MODIFY, AdminOrganizationApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AOrganization createOrganization(AOrganizationCreate organizationCreate) {
		Organization m = organizationService.create(organizationCreate);
		return Jsons.transform(m, AOrganization.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminOrganizationApi.API_PERMISSION_TARGET, action = {
			AdminOrganizationApi.ACTION_MODIFY, AdminOrganizationApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AOrganization updateOrganization(String uid, AOrganizationUpdate organizationUpdate) {
		Organization m = organizationService.update(uid, organizationUpdate);
		return Jsons.transform(m, AOrganization.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminOrganizationApi.API_PERMISSION_TARGET, action = {
			AdminOrganizationApi.ACTION_MODIFY, AdminOrganizationApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response deleteOrganization(String uid, Boolean quiet) {
		organizationService.delete(uid, Boolean.TRUE.equals(quiet) ? true : false);
		return new Response();
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminOrganizationApi.API_PERMISSION_TARGET, action = {
			AdminOrganizationApi.ACTION_MODIFY, AdminOrganizationApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response addOrganizationUser(String uid, List<AOrganizationUserRelation> userRelationList) {

		organizationService.addUsers(uid, new LinkedHashSet<>(userRelationList));

		return new Response();
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminOrganizationApi.API_PERMISSION_TARGET, action = {
			AdminOrganizationApi.ACTION_MODIFY, AdminOrganizationApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response setOrganizationUser(String uid, List<AOrganizationUserRelation> userRelationList) {

		organizationService.setUsers(uid, new LinkedHashSet<>(userRelationList));

		return new Response();
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminOrganizationApi.API_PERMISSION_TARGET, action = {
			AdminOrganizationApi.ACTION_MODIFY, AdminOrganizationApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response removeOrganizationUser(String uid, List<String> userUidList) {

		organizationService.removeUsers(uid, new LinkedHashSet<>(userUidList));

		return new Response();
	}

	@Override
	public AOrganizationListPage listOrganization(AOrganizationFilter filter) {
		ListPage<Organization> r = organizationService.list(filter);
		return new AOrganizationListPage(Jsons.transform(r.getItems(), new TypeReference<List<AOrganization>>() {
		}), r.getPageIndex(), r.getPageSize(), r.getPageTotal(), r.getItemTotal());
	}

	@Override
	public AOrganizationUserListPage listOrganizationUser(String uid, AOrganizationUserFilter filter) {
		ListPage<OrganizationUser> r = organizationService.listUser(uid, filter);
		return new AOrganizationUserListPage(
				Jsons.transform(r.getItems(), new TypeReference<List<AOrganizationUser>>() {
				}), r.getPageIndex(), r.getPageSize(), r.getPageTotal(), r.getItemTotal());
	}
}