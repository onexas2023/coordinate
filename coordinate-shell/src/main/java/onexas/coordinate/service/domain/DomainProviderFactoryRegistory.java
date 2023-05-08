package onexas.coordinate.service.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.DomainConfig;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "DomainProviderFactoryRegistory")
public class DomainProviderFactoryRegistory {

	private static final Logger logger = LoggerFactory.getLogger(DomainProviderFactoryRegistory.class);

	Map<String, DomainProviderFactory> factories = Collections.newConcurrentMap();

	public DomainProvider getProvider(Domain domain, DomainConfig config) {
		return getFactory(domain.getProvider()).getProvider(domain.getCode(), config);
	}

	public DomainProviderFactory getFactory(String provider) {
		DomainProviderFactory f = factories.get(provider);
		if (f == null) {
			throw new NotFoundException("facotry {} not found", provider);
		}
		return f;
	}

	public List<DomainProviderFactory> listFactories() {
		List<DomainProviderFactory> fs = new LinkedList<>();
		fs.addAll(factories.values());
		java.util.Collections.sort(fs, (o1, o2) -> {
			return o1.getProviderCode().compareTo(o2.getProviderCode());
		});
		return fs;
	}

	public void registerFactory(DomainProviderFactory factory) {
		factories.put(factory.getProviderCode(), factory);
		logger.info("Register domain provider factory {}", factory.getProviderCode());
	}

	public void unregisterFactory(String providerCode) {
		if (factories.remove(providerCode) != null) {
			logger.info("Unregister domain provider factory {}", providerCode);
		}
	}
}
