package onexas.coordinate.api.v1.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.api.RequestContext;
import onexas.coordinate.api.security.GrantAuthentication;
import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.UserApi;
import onexas.coordinate.api.v1.model.UUser;
import onexas.coordinate.api.v1.model.UUserFilter;
import onexas.coordinate.api.v1.model.UUserListPage;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.model.User;
import onexas.coordinate.model.UserFilter;
import onexas.coordinate.service.OrganizationService;
import onexas.coordinate.service.UserService;
import onexas.coordinate.web.api.impl.ApiImplBase;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "UserApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantAuthentication
@GrantPermissions(@GrantPermission(target = UserApi.API_PERMISSION_TARGET,
		action = { UserApi.ACTION_VIEW, UserApi.ACTION_MODIFY, UserApi.ACTION_ADMIN }))
public class UserApiImpl extends ApiImplBase implements UserApi {

	@Autowired
	UserService userService;

	@Autowired
	OrganizationService organizationService;

	@Autowired
	RequestContext reqContext;

	private static final UUserListPage EMPTY_USER_LIST = new UUserListPage(java.util.Collections.emptyList());

	public UserApiImpl() {
		super(UserApi.API_NAME, V1, UserApi.API_URI);
	}

	@Override
	public UUserListPage listUser(UUserFilter filter) {
		if (filter == null) {
			return EMPTY_USER_LIST;
		}
		String c = filter.getCriteria();
		if (Strings.isBlank(c)) {
			return EMPTY_USER_LIST;
		}
		UserFilter f = Jsons.transform(filter, UserFilter.class);

		f.setMatchAny(Boolean.TRUE);
		f.setAccount(filter.getCriteria());
		f.setEmail(filter.getCriteria());
		f.setDisplayName(filter.getCriteria());

		ListPage<User> r = userService.list(f, Boolean.FALSE);
		return new UUserListPage(Jsons.transform(r.getItems(), new TypeReference<List<UUser>>() {
		}), r.getPageIndex(), r.getPageSize(), r.getPageTotal(), r.getItemTotal());
	}

}