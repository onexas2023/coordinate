package onexas.axes.web.zk.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListSubModel;
import org.zkoss.zul.SimpleListModel;


public class ListModelList<E> extends org.zkoss.zul.ListModelList<E> implements ListSubModel<E> {

	private static final long serialVersionUID = 1L;
	

	public ListModelList() {
		super();
	}

	public ListModelList(Collection<? extends E> c) {
		super(c);
	}

	public ListModelList(E[] array) {
		super(array);
	}

	public ListModelList(int initialCapacity) {
		super(initialCapacity);
	}

	public ListModelList(List<E> list, boolean live) {
		super(list, live);
	}
	
	public E getSingleSelection(){
		Set<E> selection = getSelection();
		if(selection==null || selection.size()==0){
			return null;
		}
		return selection.iterator().next();
	}

	@Override
	public ListModel<E> getSubModel(Object userValue, int nRows) {
		final List<E> data = new LinkedList<E>();
		nRows = getMaxNumberInSubModel(nRows);
		int size  = getSize();
		for (int i = 0; i < size; i++) {
			E obj = get(i);
			if (inSubModel(userValue, obj)) {
				data.add(obj);
				if (--nRows <= 0) break; //done
			}
		}
		return new SimpleListModel<E>(data);
	}

	protected boolean inSubModel(Object userValue, E obj) {
		if(userValue==null)
			return false;
		return obj.toString().toUpperCase().indexOf(userValue.toString().toUpperCase())>=0;
	}

	protected int getMaxNumberInSubModel(int nRows) {
		return nRows < 0 ? Integer.MAX_VALUE: nRows;
	}

	

}
