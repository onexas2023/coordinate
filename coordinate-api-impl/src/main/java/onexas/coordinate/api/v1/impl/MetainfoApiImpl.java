package onexas.coordinate.api.v1.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

import onexas.coordinate.api.v1.MetainfoApi;
import onexas.coordinate.api.v1.model.UDomain;
import onexas.coordinate.api.v1.model.UMetainfo;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.model.Domain;
import onexas.coordinate.service.DomainService;
import onexas.coordinate.web.api.impl.ApiImplBase;
/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "MetainfoApiImpl")
@Profile({ Env.PROFILE_API_NODE })
public class MetainfoApiImpl extends ApiImplBase implements MetainfoApi {

	@Autowired
	DomainService domainService;

	public MetainfoApiImpl() {
		super(MetainfoApi.API_NAME, V1, MetainfoApi.API_URI);
	}
	
	@Override
	public List<UDomain> listDomain() {
		List<UDomain> list = new LinkedList<>();
		for(Domain d:domainService.list()) {
			if(Boolean.FALSE.equals(d.getDisabled())) {
				list.add(Jsons.transform(d, UDomain.class));
			}
		}
		return list;
	}

	@Override
	public UMetainfo getMetainfo() {
		UMetainfo metadata = new UMetainfo();
		metadata.setDomains(listDomain());
		return metadata;
	}
}