package onexas.coordinate.common.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * 
 * A simple not thread-safe stack implement from linked-list
 * 
 * @author Dennis Chen
 */
public class Stack<E> implements Serializable {
	private static final long serialVersionUID = 1L;

	private final ArrayList<E> inter;

	public Stack() {
		inter = new ArrayList<E>();
	}

	public Stack(int initialCapability) {
		inter = new ArrayList<E>(initialCapability);
	}

	public E push(E obj) {
		inter.add(obj);
		return obj;
	}

	public E pop() {
		E obj;
		int len = inter.size();
		obj = peek();
		inter.remove(len - 1);
		return obj;
	}

	public E peek() {
		int s = inter.size();
		if (s == 0) {
			throw new EmptyStackException();
		}
		return inter.get(s - 1);
	}

	public boolean empty() {
		return inter.size() == 0;
	}

	public void clear() {
		inter.clear();
	}
}