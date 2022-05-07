package model.data;

import java.util.Comparator;

import com.jwetherell.algorithms.data_structures.Graph.Vertex;

/**
 * @author jared
 * Used to compare the values inside tow vertices instead of the vertices themselves
 * @param <T> Vertex class for comparison
 * @param <E> Comparable class inside the vertex
 */
public class VertexComparator<T extends Vertex<E>, E extends Comparable<E>> implements Comparator<T>{

	
	@Override
	/** 
	 * { @inheritDocs }
	 */
	public int compare(T o1, T o2) {
		return o1.getValue().compareTo(o2.getValue());
	}

}
