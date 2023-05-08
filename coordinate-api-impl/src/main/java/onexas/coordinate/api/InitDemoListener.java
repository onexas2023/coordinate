package onexas.coordinate.api;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import onexas.coordinate.api.v1.JobApi;
import onexas.coordinate.api.v1.OrganizationApi;
import onexas.coordinate.api.v1.ProfileApi;
import onexas.coordinate.api.v1.SettingApi;
import onexas.coordinate.api.v1.UserApi;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Randoms;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.HookCreate;
import onexas.coordinate.model.Organization;
import onexas.coordinate.model.OrganizationCreate;
import onexas.coordinate.model.OrganizationUserRelation;
import onexas.coordinate.model.OrganizationUserRelationType;
import onexas.coordinate.model.PrincipalPermission;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.RoleCreate;
import onexas.coordinate.model.User;
import onexas.coordinate.model.UserCreate;
import onexas.coordinate.service.HookService;
import onexas.coordinate.service.JobService;
import onexas.coordinate.service.OrganizationService;
import onexas.coordinate.service.PermissionService;
import onexas.coordinate.service.RoleService;
import onexas.coordinate.service.UserService;
import onexas.coordinate.service.event.InitDemoEvent;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "InitDemoListener")
public class InitDemoListener {

	private static final Logger logger = LoggerFactory.getLogger(InitDemoListener.class);

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@Autowired
	OrganizationService orgService;

	@Autowired
	JobService jobService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	Environment environment;

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@Autowired
	HookService hookService;

	@EventListener
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void handleInitDemo(InitDemoEvent event) {

		event.reportDemo("ping", "demo1", "stress1", "testdata");

		switch (event.getDemo()) {
		default:
			return;
		case "ping":
			logger.warn("pong");
			break;
		case "demo1":
			initDemo1();
			eventPublisher.publishEvent(new InitDemoEvent("demo1-post"));
			break;
		case "stress1":
			initStress1();
			eventPublisher.publishEvent(new InitDemoEvent("stress1-post"));
			break;
		case "testdata":
			initTestdata();
			break;
		}
		event.report();
	}

	private void initTestdata() {
		logger.warn("testdata");

		for (int i = 0; i < 50; i++) {
			HookCreate hookCreate = new HookCreate().withZone(HookService.ZONE_PUBLIC);
			String p = (i <= 9 ? "0" : "") + i;
			switch (i % 2) {
			case 0:
				hookCreate.setZone("test1");
				hookCreate.setOwnerUid(Strings.randomUid());
				hookCreate.setOwnerType("demo-owner");
				hookCreate.setSubjectUid(Strings.randomUid());
				hookCreate.setSubjectType("demo-subject");
				hookCreate.setDescription("HookA " + p);
				hookCreate.setData(Strings.randomName(30));
				hookCreate.withTriggerLife(3);
				
				break;
			case 1:
				hookCreate.setZone("test2");
				hookCreate.setDescription("HookB " + p);
				break;
			}
			hookService.create(hookCreate);

		}
	}

	private void initDemo1() {
		logger.warn("initDemo1");

		// create users
		User dennis = userService
				.create(new UserCreate().withAccount("dennis").withDisplayName("Dennis").withPassword("1234"));
		User jay = userService.create(new UserCreate().withAccount("jay").withDisplayName("Jay").withPassword("1234"));
		User marissa = userService
				.create(new UserCreate().withAccount("marissa").withDisplayName("Marissa").withPassword("1234"));
		User alice = userService
				.create(new UserCreate().withAccount("alice").withDisplayName("Alice").withPassword("1234"));
		User petter = userService
				.create(new UserCreate().withAccount("petter").withDisplayName("Petter").withPassword("1234"));
		User alex = userService
				.create(new UserCreate().withAccount("alex").withDisplayName("Alex").withPassword("1234"));
		User cathy = userService
				.create(new UserCreate().withAccount("cathy").withDisplayName("Cathy").withPassword("1234"));

		// create role
		Role users = roleService.create(new RoleCreate().withCode("users").withName("Users"));
		roleService.addPermissions(users.getUid(),
				Collections.asSet(new PrincipalPermission(ProfileApi.API_PERMISSION_TARGET, ProfileApi.ACTION_MODIFY),
						new PrincipalPermission(OrganizationApi.API_PERMISSION_TARGET, OrganizationApi.ACTION_VIEW),
						new PrincipalPermission(SettingApi.API_PERMISSION_TARGET, SettingApi.ACTION_VIEW),
						new PrincipalPermission(JobApi.API_PERMISSION_TARGET, JobApi.ACTION_VIEW),
						new PrincipalPermission(UserApi.API_PERMISSION_TARGET, UserApi.ACTION_VIEW)));
		roleService.setUsers(users.getUid(), Collections.asSet(dennis.getUid(), jay.getUid(), marissa.getUid(),
				alice.getUid(), petter.getUid(), alex.getUid(), cathy.getUid()));

		Role supervisors = roleService.create(new RoleCreate().withCode("supervisors").withName("Supervisors"));
		roleService.addPermissions(supervisors.getUid(),
				Collections.asSet(new PrincipalPermission(ProfileApi.API_PERMISSION_TARGET, ProfileApi.ACTION_MODIFY),
						new PrincipalPermission(OrganizationApi.API_PERMISSION_TARGET, OrganizationApi.ACTION_VIEW),
						new PrincipalPermission(SettingApi.API_PERMISSION_TARGET, SettingApi.ACTION_VIEW),
						new PrincipalPermission(JobApi.API_PERMISSION_TARGET, JobApi.ACTION_VIEW),
						new PrincipalPermission(UserApi.API_PERMISSION_TARGET, UserApi.ACTION_VIEW)));
		roleService.setUsers(supervisors.getUid(), Collections.asSet(dennis.getUid(), jay.getUid()));

		// create org

		Organization onexas = orgService.create(new OrganizationCreate().withCode("onexas").withName("OneXas"));
		orgService.setUsers(onexas.getUid(),
				Collections.asSet(new OrganizationUserRelation(jay.getUid(), OrganizationUserRelationType.SUPERVISOR),
						new OrganizationUserRelation(dennis.getUid(), OrganizationUserRelationType.ADVANCED_MEMBER),
						new OrganizationUserRelation(marissa.getUid(), OrganizationUserRelationType.ADVANCED_MEMBER),
						new OrganizationUserRelation(alice.getUid(), OrganizationUserRelationType.MEMBER),
						new OrganizationUserRelation(petter.getUid(), OrganizationUserRelationType.MEMBER),
						new OrganizationUserRelation(alex.getUid(), OrganizationUserRelationType.MEMBER),
						new OrganizationUserRelation(cathy.getUid(), OrganizationUserRelationType.MEMBER)));

		Organization cola = orgService.create(new OrganizationCreate().withCode("colaorange").withName("ColaOrange"));
		orgService.setUsers(cola.getUid(), Collections
				.asSet(new OrganizationUserRelation(dennis.getUid(), OrganizationUserRelationType.SUPERVISOR)));
	}

	private void initStress1() {
		logger.warn("initUserStress");

		int maxUser = 500;
		int maxRole = 100;
		int c;
		if ((c = (int) userService.count()) < maxUser) {
			for (int i = c; i < maxUser; i++) {
				UserCreate userCreate = new UserCreate().withAccount("stuser-" + (i + 1))
						.withDisplayName("Stress User " + (i + 1)).withDomain(Domain.LOCAL)
						.withPassword(Strings.randomPassword(20));
				User user = userService.create(userCreate);
				logger.info("initialized stress user " + user.getDisplayName());
			}
		}

		if ((c = (int) roleService.count()) < maxRole) {
			for (int i = c; i < maxRole; i++) {
				RoleCreate roleCreate = new RoleCreate().withCode("strole-" + (i + 1))
						.withName("Stress Role " + (i + 1));
				Role role = roleService.create(roleCreate);
				logger.info("initialized stress role " + role.getName());
			}
		}

		int maxJob = 200;
		if ((c = (int) jobService.count()) < maxJob) {
			for (int i = c; i < maxJob; i++) {
				final int x = i;
				jobService.execute("admin stress jobs", new Callable<Object>() {
					@Override
					public Map<String, Object> call() throws Exception {
						int r = Randoms.random.nextInt(2000);
						if (r % 3 == 0) {
							throw new RuntimeException("exception hit " + r);
						}
						Thread.sleep(r);
						logger.info("done stress job " + x);
						Map<String, Object> map = new LinkedHashMap<>();
						int s = r % 10;
						for (int i = 0; i < s; i++) {
							map.put(Strings.randomPassword(3), Strings.randomUid());
						}
						return r % 2 == 0 ? null : map;
					}
				});
			}
		}

	}
}