package onexas.coordinate.web.api.v1.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.web.api.ApiInfoRegistory;
import onexas.coordinate.web.api.impl.ApiImplBase;
import onexas.coordinate.web.api.v1.CoordinateApi;
import onexas.coordinate.web.api.v1.model.ApiInfo;

@RestController(Env.NS_BEAN + "CoordinateApiImpl")
@Profile({ Env.PROFILE_API_NODE })
public class CoordinateApiImpl extends ApiImplBase implements CoordinateApi {

	protected CoordinateApiImpl() {
		super(API_NAME, V1, API_URI);
	}

	@Autowired
	ApiInfoRegistory infoRegister;

	public List<ApiInfo> listApiInfos() {
		return infoRegister.list();
	}
}