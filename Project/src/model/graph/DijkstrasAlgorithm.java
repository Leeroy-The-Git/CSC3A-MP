package model.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import com.jwetherell.algorithms.data_structures.Graph;

import model.marvel.MarvelNode;

/**
 * Dikstra's algorithm to find the shortest path between two nodes
 * @author JARED SWANZEN (220134523)
 *
 */
public class DijkstrasAlgorithm {
	
    /**
     * Get shortest path between two vertices
     * @param graph graph to search in
     * @param start start vertex
     * @param end end vertex
     * @return shortest path
     */
    public static Graph.CostPathPair<MarvelNode> getShortestPath(Graph<MarvelNode> graph, Graph.Vertex<MarvelNode> start, Graph.Vertex<MarvelNode> end) {
        final Map<Graph.Vertex<MarvelNode>, List<Graph.Edge<MarvelNode>>> paths = new HashMap<Graph.Vertex<MarvelNode>, List<Graph.Edge<MarvelNode>>>();
        final Map<Graph.Vertex<MarvelNode>, Graph.CostVertexPair<MarvelNode>> costs = new HashMap<Graph.Vertex<MarvelNode>, Graph.CostVertexPair<MarvelNode>>();
        for (Graph.Vertex<MarvelNode> vertex : graph.getVertices())
            paths.put(vertex, new ArrayList<Graph.Edge<MarvelNode>>());

        for (Graph.Vertex<MarvelNode> vertex : graph.getVertices()) {
            if (vertex.equals(start))
                costs.put(vertex, new Graph.CostVertexPair<MarvelNode>(0, vertex));
            else
                costs.put(vertex, new Graph.CostVertexPair<MarvelNode>(Integer.MAX_VALUE, vertex));
        }

        final Queue<Graph.CostVertexPair<MarvelNode>> unvisited = new PriorityQueue<Graph.CostVertexPair<MarvelNode>>();
        unvisited.add(costs.get(start));

        while (!unvisited.isEmpty()) {
            final Graph.CostVertexPair<MarvelNode> pair = unvisited.remove();
            final Graph.Vertex<MarvelNode> vertex = pair.getVertex();

            // Compute costs from current vertex to all unvisited vertices
            for (Graph.Edge<MarvelNode> e : vertex.getEdges()) {
                final Graph.CostVertexPair<MarvelNode> toPair = costs.get(e.getToVertex());
                final Graph.CostVertexPair<MarvelNode> lowestCostToThisVertex = costs.get(vertex);
                final int cost = lowestCostToThisVertex.getCost() + e.getCost();
                if (toPair.getCost() == Integer.MAX_VALUE) {
                    // New vertex

                	// Remove and re-insert
                    unvisited.remove(toPair);
                    toPair.setCost(cost);
                    unvisited.add(toPair);

                    // Update paths
                    List<Graph.Edge<MarvelNode>> set = paths.get(e.getToVertex()); // O(log n)
                    set.addAll(paths.get(e.getFromVertex())); // O(log n)
                    set.add(e);
                } else if (cost < toPair.getCost()) {
                    // Shorter path

                    // Remove and re-insert
                    unvisited.remove(toPair);
                    toPair.setCost(cost);
                    unvisited.add(toPair);

                    // Update paths
                    List<Graph.Edge<MarvelNode>> set = paths.get(e.getToVertex()); // O(log n)
                    set.clear();
                    set.addAll(paths.get(e.getFromVertex())); // O(log n)
                    set.add(e);
                }
            }

            if (end != null && vertex.equals(end)) {
                // Found shortest path
                break;
            }
        }

        if (end != null) {
        	// Output final path
            final Graph.CostVertexPair<MarvelNode> pair = costs.get(end);
            final List<Graph.Edge<MarvelNode>> set = paths.get(end);
            return (new Graph.CostPathPair<MarvelNode>(pair.getCost(), set));
        }
        return null;
    }
}
