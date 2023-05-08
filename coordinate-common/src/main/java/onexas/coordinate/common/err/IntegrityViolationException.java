package onexas.coordinate.common.err;

import onexas.coordinate.common.lang.Strings;

/**
 * Indicate the current action is not allowed because of some system integrity reason
 * @author Dennis Chen
 *
 */
public class IntegrityViolationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IntegrityViolationException() {
		super("Integrity Violation");
	}

	public IntegrityViolationException(String message) {
		super(message);
	}
	
	public IntegrityViolationException(String message, Object... args) {
		super(Strings.format(message, args));
	}

	public IntegrityViolationException(String message, Throwable x) {
		super(message, x);
	}
}
