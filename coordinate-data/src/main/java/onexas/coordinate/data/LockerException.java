package onexas.coordinate.data;
/**
 * 
 * @author Dennis Chen
 *
 */
public class LockerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public LockerException() {
		super();
	}

	public LockerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LockerException(String message, Throwable cause) {
		super(message, cause);
	}

	public LockerException(String message) {
		super(message);
	}

	public LockerException(Throwable cause) {
		super(cause);
	}

	
}
