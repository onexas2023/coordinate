package onexas.coordinate.service.impl.domain;

import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.service.domain.DomainAuthenticator;
import onexas.coordinate.service.domain.DomainProvider;
import onexas.coordinate.service.domain.DomainUserFinder;
/**
 * 
 * @author Dennis Chen
 *
 */
public class LocalDomainProvider implements DomainProvider {
	String domainCode;
	DomainConfig domainConfig;
	public LocalDomainProvider(String domainCode, DomainConfig domainConfig) {
		this.domainCode = domainCode;
		this.domainConfig = domainConfig;
	}

	@Override
	public DomainAuthenticator getAuthenticator() {
		return new LocalDomainAuthenticator(domainCode, domainConfig);
	}

	@Override
	public DomainUserFinder getUserFinder() {
		return new LocalDomainUserFinder(domainCode, domainConfig);
	}

}
