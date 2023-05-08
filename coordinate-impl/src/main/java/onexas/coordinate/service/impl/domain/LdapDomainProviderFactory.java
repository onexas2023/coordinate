package onexas.coordinate.service.impl.domain;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.lang.Classes;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.model.YamlDomainConfig;
import onexas.coordinate.service.domain.DomainProvider;
import onexas.coordinate.service.domain.DomainProviderFactory;
import onexas.coordinate.service.domain.DomainProviderFactoryRegistory;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "LdapDomainProviderFactory")
public class LdapDomainProviderFactory implements DomainProviderFactory {

	private static final Logger logger = LoggerFactory.getLogger(LdapDomainProviderFactory.class);

	@Autowired
	DomainProviderFactoryRegistory registory;

	@Override
	public String getProviderCode() {
		return "ldap";
	}

	@Override
	public DomainProvider getProvider(String domainCode, DomainConfig domainConfig) {
		return new LdapDomainProvider(domainCode, domainConfig);
	}

	@Override
	public DomainConfig getConfigTemplate() {
		String yaml = "";
		try {
			yaml = Classes.getResourceAsString(getClass(), "LdapDomainConfigTemplate.yaml");
		} catch (IOException x) {
			logger.warn(x.getMessage());
		}
		return new YamlDomainConfig(yaml);
	}

	@PostConstruct
	public void postConstruct() {
		registory.registerFactory(this);
	}

	@Override
	public String checkConfig(DomainConfig domainConfig) throws BadArgumentException {
		return LdapDomainProvider.checkConfig(domainConfig);
	}

}
