package onexas.axes.web;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.web.api.Api;

/**
 * 
 * @author Dennis Chen
 *
 */
@Configuration(Env.NS_BEAN + "AxesWebConfiguration")
@PropertySource("classpath:axes-web.properties")
public class AxesWebConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(AxesWebConfiguration.class);

	@Autowired
	PageEntryRegistory peRegistory;

	@Autowired
	PrincipalPermissionRegistory ppRegistory;

	@Value("${coordinate.api-base-path}")
	String apiBasePath;
	@Value("${coordinate.api-proxy-host:#{null}}")
	String apiProxyHost;
	@Value("${coordinate.api-proxy-port:#{null}}")
	Integer apiProxyPort;

	@PostConstruct
		public void postConstruct() {
			
			peRegistory.register(new PageEntry("mySettings", "~@/axes/mySettings.zul",
					"l:axes.page.mySettings", ""));
			
			peRegistory.register(new PageEntry("userMgnt", "~@/axes/admin/userMgnt.zul", "l:axes.page.userMgnt",
					"coordinate-admin-user:*"));
			peRegistory.register(new PageEntry("roleMgnt", "~@/axes/admin/roleMgnt.zul", "l:axes.page.roleMgnt",
					"coordinate-admin-role:*"));
			peRegistory.register(new PageEntry("organizationMgnt", "~@/axes/admin/organizationMgnt.zul",
					"l:axes.page.organizationMgnt", "coordinate-admin-organization:*"));
			peRegistory.register(new PageEntry("domainMgnt", "~@/axes/admin/domainMgnt.zul", "l:axes.page.domainMgnt",
					"coordinate-admin-domain:*"));
			peRegistory.register(new PageEntry("secretMgnt", "~@/axes/admin/secretMgnt.zul", "l:axes.page.secretMgnt",
					"coordinate-admin-secret:*"));
			peRegistory.register(new PageEntry("logMgnt", "~@/axes/admin/logMgnt.zul", "l:axes.page.logMgnt",
					"coordinate-admin-log:*"));
			peRegistory.register(new PageEntry("jobMgnt", "~@/axes/admin/jobMgnt.zul", "l:axes.page.jobMgnt",
					"coordinate-admin-job:*"));
			peRegistory.register(new PageEntry("hookMgnt", "~@/axes/admin/hookMgnt.zul", "l:axes.page.hookMgnt",
					"coordinate-admin-hook:*"));
			peRegistory.register(new PageEntry("settingMgnt", "~@/axes/admin/settingMgnt.zul", "l:axes.page.settingMgnt",
					"coordinate-admin-setting:*"));
			
			ppRegistory.register(new PrincipalPermissionBundle("axes",new String[] {Api.ACTION_VIEW}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-admin-user",new String[] {Api.ACTION_VIEW, Api.ACTION_MODIFY}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-admin-role",new String[] {Api.ACTION_VIEW, Api.ACTION_MODIFY}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-admin-domain",new String[] {Api.ACTION_VIEW, Api.ACTION_MODIFY}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-admin-secret",new String[] {Api.ACTION_VIEW, Api.ACTION_MODIFY}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-admin-organization",new String[] {Api.ACTION_VIEW, Api.ACTION_MODIFY}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-admin-log",new String[] {Api.ACTION_VIEW, Api.ACTION_MODIFY}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-admin-job",new String[] {Api.ACTION_VIEW}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-admin-hook",new String[] {Api.ACTION_VIEW, Api.ACTION_MODIFY}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-admin-setting",
					new String[] { Api.ACTION_VIEW, Api.ACTION_MODIFY }));
			
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-profile",new String[] {Api.ACTION_MODIFY}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-user",new String[] {Api.ACTION_VIEW}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-organization",new String[] {Api.ACTION_VIEW}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-job",new String[] {Api.ACTION_VIEW}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-hook",new String[] {Api.ACTION_VIEW}));
			ppRegistory.register(new PrincipalPermissionBundle("coordinate-setting",
					new String[] { Api.ACTION_VIEW}));
			
			
			if(apiProxyHost!=null && apiProxyPort!=null) {
				logger.info("Api Base-path: {}, using proxy: {}:{}", apiBasePath, apiProxyHost, apiProxyPort);	
			}else {
				logger.info("Api base path: {}", apiBasePath);	
			}
		}
}