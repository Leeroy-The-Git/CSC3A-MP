package model.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import com.jwetherell.algorithms.data_structures.Graph;

import model.marvel.MarvelNode;

public class DijkstrasAlgorithm {	
	 public static Map<Graph.Vertex<MarvelNode>, Graph.CostPathPair<MarvelNode>> getShortestPaths(Graph<MarvelNode> graph, Graph.Vertex<MarvelNode> source) {
        final Map<Graph.Vertex<MarvelNode>, List<Graph.Edge<MarvelNode>>> paths = new HashMap<Graph.Vertex<MarvelNode>, List<Graph.Edge<MarvelNode>>>();
        final Map<Graph.Vertex<MarvelNode>, Graph.CostVertexPair<MarvelNode>> costs = new HashMap<Graph.Vertex<MarvelNode>, Graph.CostVertexPair<MarvelNode>>();

        getShortestPath(graph, source, null, paths, costs);

        final Map<Graph.Vertex<MarvelNode>, Graph.CostPathPair<MarvelNode>> map = new HashMap<Graph.Vertex<MarvelNode>, Graph.CostPathPair<MarvelNode>>();
        for (Graph.CostVertexPair<MarvelNode> pair : costs.values()) {
            int cost = pair.getCost();
            Graph.Vertex<MarvelNode> vertex = pair.getVertex();
            List<Graph.Edge<MarvelNode>> path = paths.get(vertex);
            map.put(vertex, new Graph.CostPathPair<MarvelNode>(cost, path));
        }
        return map;
    }

    public static Graph.CostPathPair<MarvelNode> getShortestPath(Graph<MarvelNode> graph, Graph.Vertex<MarvelNode> start, Graph.Vertex<MarvelNode> end) {
        final Map<Graph.Vertex<MarvelNode>, List<Graph.Edge<MarvelNode>>> paths = new HashMap<Graph.Vertex<MarvelNode>, List<Graph.Edge<MarvelNode>>>();
        final Map<Graph.Vertex<MarvelNode>, Graph.CostVertexPair<MarvelNode>> costs = new HashMap<Graph.Vertex<MarvelNode>, Graph.CostVertexPair<MarvelNode>>();
        return getShortestPath(graph, start, end, paths, costs);
    }

    private static Graph.CostPathPair<MarvelNode> getShortestPath(Graph<MarvelNode> graph, Graph.Vertex<MarvelNode> start, Graph.Vertex<MarvelNode> end,
    		Map<Graph.Vertex<MarvelNode>, List<Graph.Edge<MarvelNode>>> paths, Map<Graph.Vertex<MarvelNode>, Graph.CostVertexPair<MarvelNode>> costs) {
        
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

            // Compute costs from current vertex to all reachable vertices which haven't been visited
            for (Graph.Edge<MarvelNode> e : vertex.getEdges()) {
                final Graph.CostVertexPair<MarvelNode> toPair = costs.get(e.getToVertex()); // O(1)
                final Graph.CostVertexPair<MarvelNode> lowestCostToThisVertex = costs.get(vertex); // O(1)
                final int cost = lowestCostToThisVertex.getCost() + e.getCost();
                if (toPair.getCost() == Integer.MAX_VALUE) {
                    // Haven't seen this vertex yet

                    // Need to remove the pair and re-insert, so the priority queue keeps it's invariants
                    unvisited.remove(toPair); // O(n)
                    toPair.setCost(cost);
                    unvisited.add(toPair); // O(log n)

                    // Update the paths
                    List<Graph.Edge<MarvelNode>> set = paths.get(e.getToVertex()); // O(log n)
                    set.addAll(paths.get(e.getFromVertex())); // O(log n)
                    set.add(e);
                } else if (cost < toPair.getCost()) {
                    // Found a shorter path to a reachable vertex

                    // Need to remove the pair and re-insert, so the priority queue keeps it's invariants
                    unvisited.remove(toPair); // O(n)
                    toPair.setCost(cost);
                    unvisited.add(toPair); // O(log n)

                    // Update the paths
                    List<Graph.Edge<MarvelNode>> set = paths.get(e.getToVertex()); // O(log n)
                    set.clear();
                    set.addAll(paths.get(e.getFromVertex())); // O(log n)
                    set.add(e);
                }
            }

            // Termination conditions
            if (end != null && vertex.equals(end)) {
                // We are looking for shortest path to a specific vertex, we found it.
                break;
            }
        }

        if (end != null) {
            final Graph.CostVertexPair<MarvelNode> pair = costs.get(end);
            final List<Graph.Edge<MarvelNode>> set = paths.get(end);
            return (new Graph.CostPathPair<MarvelNode>(pair.getCost(), set));
        }
        return null;
    }
}
