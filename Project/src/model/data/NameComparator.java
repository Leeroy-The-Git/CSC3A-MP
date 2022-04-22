package model.data;

import java.util.Comparator;

import com.jwetherell.algorithms.data_structures.Graph.Vertex;

public class NameComparator<T extends Vertex<E>, E extends Comparable<E>> implements Comparator<T>{

	@Override
	public int compare(T o1, T o2) {
		return o1.getValue().compareTo(o2.getValue());
	}

}
