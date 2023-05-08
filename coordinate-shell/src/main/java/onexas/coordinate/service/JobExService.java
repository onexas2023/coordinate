package onexas.coordinate.service;

import onexas.coordinate.service.jms.JobQueueEventMessage;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface JobExService {

	public void onReceive(JobQueueEventMessage message);
}
