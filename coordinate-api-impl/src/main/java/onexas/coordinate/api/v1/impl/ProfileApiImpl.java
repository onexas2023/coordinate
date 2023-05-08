package onexas.coordinate.api.v1.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import onexas.coordinate.api.RequestContext;
import onexas.coordinate.api.security.GrantAuthentication;
import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.ProfileApi;
import onexas.coordinate.api.v1.model.UPasswordUpdate;
import onexas.coordinate.api.v1.model.UProfileUpdate;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.NoPermissionException;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.User;
import onexas.coordinate.model.UserUpdate;
import onexas.coordinate.service.AuthenticationTokenService;
import onexas.coordinate.service.UserService;
import onexas.coordinate.web.api.impl.ApiImplBase;
import onexas.coordinate.web.api.model.Response;
/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "ProfileApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantAuthentication
public class ProfileApiImpl extends ApiImplBase implements ProfileApi {

	@Autowired
	UserService userService;

	@Autowired
	AuthenticationTokenService authTokenService;

	@Autowired
	RequestContext reqContext;
	
	

	public ProfileApiImpl() {
		super(ProfileApi.API_NAME, V1, ProfileApi.API_URI);
	}
	
	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response updatePassword(UPasswordUpdate passwordUpdate) {

		String userUid = reqContext.grantUserUid();

		if (userService.verifyPassword(userUid, passwordUpdate.getOldPassword())) {
			userService.update(userUid, new UserUpdate().withPassword(passwordUpdate.getNewPassword()));
		} else {
			User user = userService.get(userUid);
			if(user!=null && !Domain.LOCAL.equals(user.getDomain())) {
				throw new NoPermissionException("not allow to update a non-local domain user's password");
			}
			throw new NoPermissionException("invalid old password");
		}
		return new Response();
	}

	@Override
	public onexas.coordinate.api.v1.model.UProfile getProfile() {
		
		String userUid = reqContext.grantUserUid();
		User user = userService.get(userUid);
		Map<String,String> properties = userService.getProperties(user.getUid(), UserService.PROP_CAT_PROFILE);
		return wrapProfile(user, properties);
	}

	private onexas.coordinate.api.v1.model.UProfile wrapProfile(User user, Map<String, String> properties) {
		onexas.coordinate.api.v1.model.UProfile profile = new onexas.coordinate.api.v1.model.UProfile();
		profile.setAccount(user.getAccount());
		profile.setDomain(user.getDomain());
		profile.setDisplayName(user.getDisplayName());
		profile.setEmail(user.getEmail());
		profile.setAddress(properties.get("address"));
		profile.setPhone(properties.get("phone"));
		return profile;
	}

	@Override
	@GrantPermissions(@GrantPermission(target = ProfileApi.API_PERMISSION_TARGET, action = {
			ProfileApi.ACTION_MODIFY}))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public onexas.coordinate.api.v1.model.UProfile updateProfile(UProfileUpdate profileUpdate) {
		String userUid = reqContext.grantUserUid();
		
		UserUpdate userUpdate = new UserUpdate();
		if(profileUpdate.getDisplayName()!=null) {
			userUpdate.setDisplayName(profileUpdate.getDisplayName());
		}
		if(profileUpdate.getEmail()!=null) {
			userUpdate.setEmail(profileUpdate.getEmail());
		}
		User user = userService.update(userUid, userUpdate);
		
		Map<String,String> properties = userService.getProperties(userUid, UserService.PROP_CAT_PROFILE);		
		if(profileUpdate.getAddress()!=null) {
			properties.put("address",profileUpdate.getAddress());
		}
		if(profileUpdate.getPhone()!=null) {
			properties.put("phone",profileUpdate.getPhone());
		}
		userService.setProperties(userUid, properties, UserService.PROP_CAT_PROFILE);
		
		return wrapProfile(user, properties);
	}

}