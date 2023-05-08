package onexas.coordinate.service.domain;

import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.model.DomainConfig;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface DomainProviderFactory {

	String getProviderCode();

	DomainProvider getProvider(String domainCode, DomainConfig domainConfig);

	DomainConfig getConfigTemplate();

	String checkConfig(DomainConfig domainConfig) throws BadArgumentException;
}
