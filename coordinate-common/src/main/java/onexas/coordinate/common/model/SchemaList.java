package onexas.coordinate.common.model;

import java.util.Collection;
import java.util.LinkedList;

import io.swagger.v3.oas.annotations.Hidden;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SchemaList<T> extends LinkedList<T> {

	public static final String SCHEMA_NAME = "SchemaList";

	private static final long serialVersionUID = 1L;

	public SchemaList() {
		super();
	}

	public SchemaList(Collection<? extends T> c) {
		super(c);
	}

	@Override
	@Hidden
	public boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	@Hidden
	public T getFirst() {
		return super.getFirst();
	}

	@Override
	@Hidden
	public T getLast() {
		return super.getLast();
	}

	public SchemaList<T> with(T value) {
		this.add(value);
		return this;
	}
}
