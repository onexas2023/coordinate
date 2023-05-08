package onexas.coordinate.common.err;

import onexas.coordinate.common.lang.Strings;

/**
 * Indicate a io exception of the server
 * @author Dennis Chen
 *
 */
public class IORuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IORuntimeException() {
		super("IOException");
	}

	public IORuntimeException(String message) {
		super(message);
	}
	
	public IORuntimeException(String message, Object... args) {
		super(Strings.format(message, args));
	}

	public IORuntimeException(String message, Throwable x) {
		super(message, x);
	}
}
