package onexas.coordinate.api.v1.admin.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.admin.AdminDomainApi;
import onexas.coordinate.api.v1.admin.model.ADomain;
import onexas.coordinate.api.v1.admin.model.ADomainConfigCheck;
import onexas.coordinate.api.v1.admin.model.ADomainCreate;
import onexas.coordinate.api.v1.admin.model.ADomainProviderFactory;
import onexas.coordinate.api.v1.admin.model.ADomainUpdate;
import onexas.coordinate.api.v1.admin.model.ADomainUser;
import onexas.coordinate.api.v1.admin.model.ADomainUserFilter;
import onexas.coordinate.api.v1.admin.model.ADomainUserListPage;
import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.IntegrityViolationException;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.model.DomainUser;
import onexas.coordinate.model.YamlDomainConfig;
import onexas.coordinate.service.DomainService;
import onexas.coordinate.service.domain.DomainProviderFactory;
import onexas.coordinate.service.domain.DomainProviderFactoryRegistory;
import onexas.coordinate.web.api.impl.ApiImplBase;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "AdminDomainApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantPermissions(@GrantPermission(target = AdminDomainApi.API_PERMISSION_TARGET, action = { AdminDomainApi.ACTION_VIEW,
		AdminDomainApi.ACTION_MODIFY, AdminDomainApi.ACTION_ADMIN }))
public class AdminDomainApiImpl extends ApiImplBase implements AdminDomainApi {

	@Autowired
	DomainService domainService;

	@Autowired
	DomainProviderFactoryRegistory dpfReg;

	public AdminDomainApiImpl() {
		super(AdminDomainApi.API_NAME, V1, AdminDomainApi.API_URI);
	}

	@Override
	public List<ADomain> listDomain() {
		List<Domain> list = domainService.list();
		return Jsons.transform(list, new TypeReference<List<ADomain>>() {
		});
	}

	@Override
	public ADomain getDomain(String code, Boolean find) {
		return Jsons.transform(Boolean.TRUE.equals(find) ? domainService.find(code) : domainService.get(code),
				ADomain.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminDomainApi.API_PERMISSION_TARGET, action = {
			AdminDomainApi.ACTION_MODIFY, AdminDomainApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public ADomain createDomain(ADomainCreate domainCreate) {
		Domain m = domainService.create(domainCreate);
		return Jsons.transform(m, ADomain.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminDomainApi.API_PERMISSION_TARGET, action = {
			AdminDomainApi.ACTION_MODIFY, AdminDomainApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public ADomain updateDomain(String code, ADomainUpdate domainUpdate) {
		if (Domain.LOCAL.equals(code) && Boolean.TRUE.equals(domainUpdate.getDisabled())
				&& !AppContext.config().getBoolean("app.allowDisableLocalDomain", false)) {
			throw new IntegrityViolationException("not allow to disable domain {}", Domain.LOCAL);
		}
		
		Domain m = domainService.update(code, domainUpdate);
		return Jsons.transform(m, ADomain.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminDomainApi.API_PERMISSION_TARGET, action = {
			AdminDomainApi.ACTION_MODIFY, AdminDomainApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response deleteDomain(String code, Boolean quiet) {
		domainService.delete(code, Boolean.TRUE.equals(quiet) ? true : false);
		return new Response();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Object> getDomainConfig(String code) {
		return domainService.getConfig(code).toObject(Map.class);
	}

	@Override
	public String getDomainConfigYaml(String code) {
		return domainService.getConfigYaml(code);
	}

	@Override
	public ADomainUserListPage listDomainUser(String code, ADomainUserFilter filter) {
		Domain domain = domainService.get(code);
		DomainConfig domainConfig = domainService.getConfig(code);
		ListPage<DomainUser> users = dpfReg.getProvider(domain, domainConfig).getUserFinder().list(filter);
		return new ADomainUserListPage(Jsons.transform(users.getItems(), new TypeReference<List<ADomainUser>>() {
		}), users.getPageIndex(), users.getPageSize(), users.getPageTotal(), users.getItemTotal());
	}

	@Override
	public List<ADomainProviderFactory> listDomainProviderFactory() {
		LinkedList<ADomainProviderFactory> list = new LinkedList<>();

		for (onexas.coordinate.service.domain.DomainProviderFactory f : dpfReg.listFactories()) {
			ADomainProviderFactory factory = new ADomainProviderFactory();
			factory.setProviderCode(f.getProviderCode());
			factory.setConfigYamlTemplate(f.getConfigTemplate().toYaml());
			list.add(factory);
		}
		return list;
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminDomainApi.API_PERMISSION_TARGET, action = {
			AdminDomainApi.ACTION_MODIFY, AdminDomainApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response checkDomainConfig(ADomainConfigCheck domainConfigCheck) {
		DomainProviderFactory provider = dpfReg.getFactory(domainConfigCheck.getProvider());
		DomainConfig moduleConfig = new YamlDomainConfig(domainConfigCheck.getConfigYaml());
		try {
			String msg = provider.checkConfig(moduleConfig);
			return new Response(msg);
		} catch (RuntimeException x) {
			return new Response(true, x.getMessage());
		}
	}

}