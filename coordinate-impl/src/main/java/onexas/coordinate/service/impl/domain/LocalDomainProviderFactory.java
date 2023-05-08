package onexas.coordinate.service.impl.domain;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.model.MapDomainConfig;
import onexas.coordinate.service.domain.DomainProvider;
import onexas.coordinate.service.domain.DomainProviderFactory;
import onexas.coordinate.service.domain.DomainProviderFactoryRegistory;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN+"LocalDomainProviderFactory")
public class LocalDomainProviderFactory implements DomainProviderFactory {

	@Autowired
	DomainProviderFactoryRegistory registory;
	
	@Override
	public String getProviderCode() {
		return Domain.LOCAL;
	}

	@Override
	public DomainProvider getProvider(String domainCode, DomainConfig domainConfig) {
		return new LocalDomainProvider(domainCode, domainConfig);
	}

	@Override
	public DomainConfig getConfigTemplate() {
		MapDomainConfig domainConfig = new MapDomainConfig();
		return domainConfig;
	}
	
	@PostConstruct
	public void postConstruct() {
		registory.registerFactory(this);
	}

	@Override
	public String checkConfig(DomainConfig domainConfig) {
		return "OK";
	}

}
