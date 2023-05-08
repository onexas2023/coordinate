package onexas.coordinate.common.err;

import onexas.coordinate.common.lang.Strings;

/**
 * Indicate the requesting instance is not found
 * @author Dennis Chen
 *
 */
public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NotFoundException() {
		super("Not found");
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, Object... args) {
		super(Strings.format(message, args));
	}
	
	public NotFoundException(String message, Throwable x) {
		super(message, x);
	}
}
