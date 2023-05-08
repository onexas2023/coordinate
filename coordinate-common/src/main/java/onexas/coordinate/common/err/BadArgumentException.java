package onexas.coordinate.common.err;

import onexas.coordinate.common.lang.Strings;

/**
 * Indicate the given argument of a action is not correct
 * @author Dennis Chen
 *
 */
public class BadArgumentException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BadArgumentException() {
		super("Bad Argument");
	}

	public BadArgumentException(String message) {
		super(message);
	}
	
	public BadArgumentException(String message, Object... args) {
		super(Strings.format(message, args));
	}

	public BadArgumentException(String message, Throwable x) {
		super(message, x);
	}
}
