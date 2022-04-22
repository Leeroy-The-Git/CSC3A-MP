package model.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import com.jwetherell.algorithms.data_structures.Graph;
import com.jwetherell.algorithms.data_structures.Graph.Edge;
import com.jwetherell.algorithms.data_structures.Graph.Vertex;

import model.marvel.MarvelNode;

public class BreadthFirstSearch<T extends Comparable<T>> {

	public List<Vertex<T>> search(Vertex<T> start, T value) {
		StringBuilder res = new StringBuilder();
		Queue<Vertex<T>> queue = new LinkedList<>();
		Stack<Vertex<T>> path = new Stack<>();
		ArrayList<Vertex<T>> visited = new ArrayList<>();
		queue.add(start);
		Vertex<T> current;
		
		int count = 0;
		while (!queue.isEmpty()) {
		    current = queue.remove();
		    path.push(current);
		    count++;
		    if (count % 100 == 0)
		    	System.out.printf("Visited %d nodes\n", count);
		    if (current.getValue().equals(value)) {
		    	System.out.println("");
		    	System.out.println("FOUND HIM!!");
		    	System.out.println("");
		        return path;
		    } else {
		        visited.add(current);
		        queue.addAll(getNeighbours(current));
		        queue.removeAll(visited);
		    }
		}
		return path;
	}
	
	private List<Vertex<T>> getNeighbours(Vertex<T> vertex) {
		List<Edge<T>> edges = vertex.getEdges();
		List<Vertex<T>> neighbours = new ArrayList<>();
		for (Edge<T> edge : edges) {
			if (edge.getFromVertex().equals(vertex)) {
				neighbours.add(edge.getToVertex());
			} else if (edge.getToVertex().equals(vertex)) {
				neighbours.add(edge.getFromVertex());
			}
		}
		return neighbours;
	}
}
