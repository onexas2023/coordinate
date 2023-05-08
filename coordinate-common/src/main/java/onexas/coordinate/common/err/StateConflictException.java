package onexas.coordinate.common.err;

import onexas.coordinate.common.lang.Strings;

/**
 * Indicate the current state of a domain object is not allow to do something 
 * @author Dennis Chen
 *
 */
public class StateConflictException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public StateConflictException() {
		super("State Conflict");
	}

	public StateConflictException(String message) {
		super(message);
	}

	public StateConflictException(String message, Object... args) {
		super(Strings.format(message, args));
	}
	
	public StateConflictException(String message, Throwable x) {
		super(message, x);
	}
}
