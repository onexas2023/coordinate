package onexas.coordinate.common.app;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */
public class  ApplicationEvent<T extends Serializable> extends org.springframework.context.ApplicationEvent {
	private static final long serialVersionUID = 1L;
	T data;

	public ApplicationEvent(T data) {
		super("");
		this.data = data;
	}

	public T getData() {
		return data;
	}

}
