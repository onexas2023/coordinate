package onexas.coordinate.api.v1.admin.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.admin.AdminSecretApi;
import onexas.coordinate.api.v1.admin.model.ASecret;
import onexas.coordinate.api.v1.admin.model.ASecretCreate;
import onexas.coordinate.api.v1.admin.model.ASecretFilter;
import onexas.coordinate.api.v1.admin.model.ASecretListPage;
import onexas.coordinate.api.v1.admin.model.ASecretUpdate;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.Secret;
import onexas.coordinate.service.SecretService;
import onexas.coordinate.web.api.impl.ApiImplBase;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "AdminSecretApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantPermissions(@GrantPermission(target = AdminSecretApi.API_PERMISSION_TARGET, action = { AdminSecretApi.ACTION_VIEW,
		AdminSecretApi.ACTION_MODIFY, AdminSecretApi.ACTION_ADMIN }))
public class AdminSecretApiImpl extends ApiImplBase implements AdminSecretApi {

	@Autowired
	SecretService secretService;

	public AdminSecretApiImpl() {
		super(AdminSecretApi.API_NAME, V1, AdminSecretApi.API_URI);
	}

	@Override
	public ASecret getSecret(String uid, Boolean find) {
		return Jsons.transform(Boolean.TRUE.equals(find) ? secretService.find(uid) : secretService.get(uid),
				ASecret.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminSecretApi.API_PERMISSION_TARGET, action = {
			AdminSecretApi.ACTION_MODIFY, AdminSecretApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public ASecret createSecret(ASecretCreate secret) {
		Secret m = secretService.create(secret);
		return Jsons.transform(m, ASecret.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminSecretApi.API_PERMISSION_TARGET, action = {
			AdminSecretApi.ACTION_MODIFY, AdminSecretApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public ASecret updateSecret(String uid, ASecretUpdate secretUpdate) {
		Secret m = secretService.update(uid, secretUpdate);
		return Jsons.transform(m, ASecret.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminSecretApi.API_PERMISSION_TARGET, action = {
			AdminSecretApi.ACTION_MODIFY, AdminSecretApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response deleteSecret(String uid, Boolean quiet) {

		Secret secret = secretService.find(uid);
		if (secret == null) {
			if (!Boolean.TRUE.equals(quiet)) {
				throw new NotFoundException("secret {} not found", uid);
			}
			return new Response();
		}

		secretService.delete(uid, Boolean.TRUE.equals(quiet) ? true : false);
		return new Response();
	}

	@Override
	public ASecretListPage listSecret(ASecretFilter filter) {
		ListPage<Secret> r = secretService.list(filter);
		return new ASecretListPage(Jsons.transform(r.getItems(), new TypeReference<List<ASecret>>() {
		}), r.getPageIndex(), r.getPageSize(), r.getPageTotal(), r.getItemTotal());
	}
}