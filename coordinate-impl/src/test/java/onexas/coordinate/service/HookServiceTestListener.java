package onexas.coordinate.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import onexas.coordinate.service.event.TriggerHookEvent;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service
public class HookServiceTestListener {
	
	static public Map<String, Object> hookDataMap = new LinkedHashMap<>();
	
	@EventListener
	public void handleHookEvent(TriggerHookEvent evt) {
		hookDataMap.put(evt.getData().getUid(), evt.getData().getData());
		if(evt.getArgs()!=null) {			
			hookDataMap.put("key", evt.getArgs().get("key"));
		}else {
			hookDataMap.remove("key");
		}
	}
}
