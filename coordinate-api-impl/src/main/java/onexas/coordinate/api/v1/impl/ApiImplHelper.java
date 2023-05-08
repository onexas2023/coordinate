package onexas.coordinate.api.v1.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import onexas.coordinate.api.RequestContext;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.NoPermissionException;
import onexas.coordinate.model.Organization;
import onexas.coordinate.model.OrganizationUserRelationType;
import onexas.coordinate.model.UserOrganization;
import onexas.coordinate.service.OrganizationService;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "ApiImplHelper")
public class ApiImplHelper {

	@Autowired
	OrganizationService organizationService;

	@Autowired
	RequestContext reqContext;

	public boolean hasUserOrganization(Organization organization,
			Set<OrganizationUserRelationType> typeMatchs) {
		String userUid = reqContext.grantUserUid();
		UserOrganization u = organizationService.findUserOrganization(organization.getUid(), userUid);
		if (u == null) {
			return false;
		}
		if (typeMatchs != null && !typeMatchs.contains(u.getRelationType())) {
			return false;
		}
		return true;
	}
	
	public UserOrganization grantUserOrganization(Organization organization,
			Set<OrganizationUserRelationType> typeMatchs) {
		String userUid = reqContext.grantUserUid();
		UserOrganization u = organizationService.findUserOrganization(organization.getUid(), userUid);
		if (u == null) {
			throw new NoPermissionException("doesn't has membership of organization {}", organization.getCode());
		}
		if (typeMatchs != null && !typeMatchs.contains(u.getRelationType())) {
			throw new NoPermissionException("doesn't has member type {} of organization {}", typeMatchs,
					organization.getCode());
		}
		return u;
	}

}