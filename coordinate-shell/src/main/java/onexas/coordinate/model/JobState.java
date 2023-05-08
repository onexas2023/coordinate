package onexas.coordinate.model;

/**
 * 
 * @author Dennis Chen
 *
 */

public enum JobState {
	QUEUING, PROCESSING, FINISHED;
	
	public static final String SCHEMA_NAME = "JobState"; 
}