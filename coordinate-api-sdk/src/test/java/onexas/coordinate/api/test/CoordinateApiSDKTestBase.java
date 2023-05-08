package onexas.coordinate.api.test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import okhttp3.OkHttpClient;
import onexas.api.invoker.ApiClient;
import onexas.coordinate.api.v1.sdk.CoordinateAuthApi;
import onexas.coordinate.api.v1.sdk.model.Authentication;
import onexas.coordinate.api.v1.sdk.model.AuthenticationRequest;
import onexas.coordinate.app.test.CoordinateAppTestBase;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.PrincipalPermission;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.RoleCreate;
import onexas.coordinate.model.User;
import onexas.coordinate.model.UserCreate;
import onexas.coordinate.model.UserUpdate;
import onexas.coordinate.service.PermissionService;
import onexas.coordinate.service.RoleService;
import onexas.coordinate.service.UserService;

/**
 * 
 * @author Dennis Chen
 *
 */
@TestPropertySource(locations = "classpath:coordinate-api-sdk-test.properties")
public class CoordinateApiSDKTestBase extends CoordinateAppTestBase {

	@LocalServerPort
	protected int port;

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@Autowired
	PermissionService permissionService;
	
	@Value("${coordinate.apiSDKProxyHost:127.0.0.1}")
	String proxyHost;

	@Value("${coordinate.apiSDKProxyPort:8888}")
	Integer proxyPort;
	
	public Set<User> createdAuthUsers = new LinkedHashSet<>();
	public Set<Role> createdAuthRoles = new LinkedHashSet<>();

	protected ApiClient getApiClient() {
		ApiClient client = new ApiClient();
		client.setBasePath("http://127.0.0.1:" + port);

		OkHttpClient httpClient = client.getHttpClient();
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		Collections.stream(httpClient.networkInterceptors()).forEach((e)->{builder.addNetworkInterceptor(e);});
        
		
		
		if (isUseProxy()) {
			builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
		}
		
		//for debug step break
		builder.readTimeout(10, TimeUnit.MINUTES);

		httpClient = builder.build();
		
		client.setHttpClient(httpClient);
		return client;
	}
	
	protected boolean isUseProxy() {
		return false;
	}

	protected ApiClient getApiClientWithAuth(String account, String password) {
		ApiClient client = getApiClient();

		CoordinateAuthApi api = new CoordinateAuthApi(client);
		Authentication auth = api.authenticate(new AuthenticationRequest().account(account).password(password));
		client.setApiKey(auth.getToken());
		return client;
	}
	protected ApiClient getApiClientWithAuthCreate() {
		return getApiClientWithAuthCreate(new PrincipalPermission("*", "*"));
	}

	protected ApiClient getApiClientWithAuthCreate(PrincipalPermission... permissions) {
		return getApiClientWithAuthCreate(Strings.randomName(10).toLowerCase(), Strings.randomPassword(10),
				Strings.randomName(10).toLowerCase(), permissions);
	}

	protected ApiClient getApiClientWithAuthCreate(String account, String password, String roleCode,
			PrincipalPermission... permissions) {
		ApiClient client = getApiClient();
		User user = userService.findByAccountDomain(account, Domain.LOCAL);
		if (user == null) {
			user = userService
					.create(new UserCreate().withAccount(account).withDisplayName(account).withPassword(password));
			createdAuthUsers.add(user);
		} else {
			user = userService.update(user.getUid(), new UserUpdate().withPassword(password));
		}
		Role role = roleService.findByCode(roleCode);
		if (role == null) {
			role = roleService.create(new RoleCreate().withCode(roleCode).withName(roleCode));
			createdAuthRoles.add(role);
		}

		userService.setRoles(user.getUid(), Collections.asSet(role.getUid()));

		// reset permission
		permissionService.deleteByPricipal(role.getUid());
		for (PrincipalPermission pp : permissions) {
			permissionService.create(role.getUid(), pp.getTarget(), pp.getAction(), null);
		}

		CoordinateAuthApi authApi = new CoordinateAuthApi(client);
		Authentication auth = authApi
				.authenticate(new AuthenticationRequest().account(account).domain(Domain.LOCAL).password(password));
		client.setApiKey(auth.getToken());
		return client;
	}

	@After
	@Override
	public void after() {
		for (User u : createdAuthUsers) {
			userService.delete(u.getUid(), false);
		}
		createdAuthUsers.clear();
		for (Role r : createdAuthRoles) {
			roleService.delete(r.getUid(), false);
		}
		createdAuthRoles.clear();
		super.after();
	}

	protected void deleteRoleByCode(String code) {
		Role role = roleService.findByCode(code);
		if (role != null) {
			roleService.delete(role.getUid(), false);
		}
	}

	protected void deleteUserByAccount(String account) {
		User user = userService.findByAccountDomain(account, Domain.LOCAL);
		if (user != null) {
			userService.delete(user.getUid(), false);
		}
	}
}
