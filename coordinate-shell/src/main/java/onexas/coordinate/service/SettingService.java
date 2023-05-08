package onexas.coordinate.service;

import onexas.coordinate.model.ServerSetting;
import onexas.coordinate.model.ServerSettingUpdate;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface SettingService {
	
	public ServerSetting getServerSetting();

	public ServerSetting updateServerSetting(ServerSettingUpdate serverSettingUpdate);
	
	public ServerSetting resetServerSetting();
	
	public void cleanCache();
}
