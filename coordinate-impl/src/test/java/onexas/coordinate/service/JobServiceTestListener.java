package onexas.coordinate.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import onexas.coordinate.service.event.JobQueueEvent;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service
public class JobServiceTestListener {
	
	void sleep(long l) {
		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {}
	}
	
	@EventListener
	public void handleDisabledUser(Event1 event) {
		sleep(1000);
		if(event.isError()) {
			throw new RuntimeException("error X");
		}
	}
	
	public static class Event1 extends JobQueueEvent{
		private static final long serialVersionUID = 1L;
		
		boolean error;
		public Event1(boolean error) {
			super("event1");
			this.error = error;
		}
		public boolean isError() {
			return error;
		}
		public void setError(boolean error) {
			this.error = error;
		}
	}
}
