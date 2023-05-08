package onexas.coordinate.api.v1.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.api.RequestContext;
import onexas.coordinate.api.security.GrantAuthentication;
import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.OrganizationApi;
import onexas.coordinate.api.v1.model.UOrganizationUser;
import onexas.coordinate.api.v1.model.UOrganizationUserFilter;
import onexas.coordinate.api.v1.model.UOrganizationUserListPage;
import onexas.coordinate.api.v1.model.UOrganizationUserRelation;
import onexas.coordinate.api.v1.model.UUserOrganization;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.Organization;
import onexas.coordinate.model.OrganizationUser;
import onexas.coordinate.model.OrganizationUserFilter;
import onexas.coordinate.model.OrganizationUserRelation;
import onexas.coordinate.model.OrganizationUserRelationType;
import onexas.coordinate.model.User;
import onexas.coordinate.service.OrganizationService;
import onexas.coordinate.service.UserService;
import onexas.coordinate.web.api.impl.ApiImplBase;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "OrganizationApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantAuthentication
@GrantPermissions(@GrantPermission(target = OrganizationApi.API_PERMISSION_TARGET, action = {
		OrganizationApi.ACTION_VIEW, OrganizationApi.ACTION_MODIFY, OrganizationApi.ACTION_ADMIN }))
public class OrganizationApiImpl extends ApiImplBase implements OrganizationApi {

	@Autowired
	UserService userService;

	@Autowired
	OrganizationService organizationService;

	@Autowired
	RequestContext reqContext;

	@Autowired
	ApiImplHelper apiHelper;

	public OrganizationApiImpl() {
		super(OrganizationApi.API_NAME, V1, OrganizationApi.API_URI);
	}

	@Override
	public List<UUserOrganization> listOrganization() {
		String userUid = reqContext.grantUserUid();
		return Jsons.transform(userService.listOrganization(userUid), new TypeReference<List<UUserOrganization>>() {
		});
	}

	@Override
	public UUserOrganization getOrganization(String code, Boolean find) {
		Organization org = organizationService.findByCode(code);
		if (org == null) {
			if (Boolean.TRUE.equals(find)) {
				return null;
			}
			throw new NotFoundException("organization {} not found", code);
		}
		return Jsons.transform(apiHelper.grantUserOrganization(org, null), UUserOrganization.class);
	}

	@Override
	public UOrganizationUserListPage listOrganizationUser(String code, UOrganizationUserFilter filter) {
		if (filter != null && Boolean.TRUE.equals(filter.getMatchAny())) {
			throw new BadArgumentException("doesn't allow match any in user query");
		}

		Organization org = organizationService.findByCode(code);
		if (org == null) {
			throw new NotFoundException("organization {} not found", code);
		}

		apiHelper.grantUserOrganization(org, null);

		OrganizationUserFilter f = Jsons.transform(filter, OrganizationUserFilter.class);

		if (f != null && filter.getCriteria() != null) {
			f.setMatchAny(Boolean.TRUE);
			f.setAccount(filter.getCriteria());
			f.setEmail(filter.getCriteria());
			f.setDisplayName(filter.getCriteria());
		}

		ListPage<OrganizationUser> r = organizationService.listUser(org.getUid(), f);
		return new UOrganizationUserListPage(
				Jsons.transform(r.getItems(), new TypeReference<List<UOrganizationUser>>() {
				}), r.getPageIndex(), r.getPageSize(), r.getPageTotal(), r.getItemTotal());
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response addOrganizationUser(String code, List<UOrganizationUserRelation> userRelationList) {
		Organization org = organizationService.findByCode(code);
		if (org == null) {
			throw new NotFoundException("organization {} not found", code);
		}

		apiHelper.grantUserOrganization(org,
				Collections.asSet(OrganizationUserRelationType.ADVANCED_MEMBER, OrganizationUserRelationType.SUPERVISOR));

		Set<OrganizationUserRelation> set = new LinkedHashSet<>();

		for (UOrganizationUserRelation r : userRelationList) {
			User u = userService.findByAliasUid(r.getAliasUid());
			if (u == null) {
				throw new BadArgumentException("user not found, alias uid {}", r.getAliasUid());
			}
			set.add(new OrganizationUserRelation(u.getUid(), r.getType()));
		}

		organizationService.addUsers(org.getUid(), set);

		return new Response();
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response setOrganizationUser(String code, List<UOrganizationUserRelation> userRelationList) {
		Organization org = organizationService.findByCode(code);
		if (org == null) {
			throw new NotFoundException("organization {} not found", code);
		}

		apiHelper.grantUserOrganization(org,
				Collections.asSet(OrganizationUserRelationType.ADVANCED_MEMBER, OrganizationUserRelationType.SUPERVISOR));

		Set<OrganizationUserRelation> set = new LinkedHashSet<>();

		for (UOrganizationUserRelation r : userRelationList) {
			User u = userService.findByAliasUid(r.getAliasUid());
			if (u == null) {
				throw new BadArgumentException("user not found, alias uid {}", r.getAliasUid());
			}
			set.add(new OrganizationUserRelation(u.getUid(), r.getType()));
		}

		organizationService.setUsers(org.getUid(), set);
		return new Response();
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response removeOrganizationUsers(String code, List<String> userAliasUidList) {
		Organization org = organizationService.findByCode(code);
		if (org == null) {
			throw new NotFoundException("organization {} not found", code);
		}

		apiHelper.grantUserOrganization(org,
				Collections.asSet(OrganizationUserRelationType.ADVANCED_MEMBER, OrganizationUserRelationType.SUPERVISOR));

		Set<String> set = new LinkedHashSet<>();

		for (String aliasUid : userAliasUidList) {
			User u = userService.findByAliasUid(aliasUid);
			if (u == null) {
				continue;
			}
			set.add(u.getUid());
		}

		organizationService.removeUsers(org.getUid(), set);
		return new Response();
	}

}