package model.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jwetherell.algorithms.data_structures.Graph;
import com.jwetherell.algorithms.data_structures.Graph.CostPathPair;
import com.jwetherell.algorithms.data_structures.Graph.CostVertexPair;
import com.jwetherell.algorithms.data_structures.Graph.Edge;
import com.jwetherell.algorithms.data_structures.Graph.Vertex;

import model.marvel.MarvelNode;

public class DijkstrasAlgorithm {
	public List<CostVertexPair<MarvelNode>> calculateShortestPath(Graph<MarvelNode> graph, Vertex<MarvelNode> source) {
		List<CostVertexPair<MarvelNode>> paths = new ArrayList<>();
		CostVertexPair<MarvelNode> sourcePath = new CostVertexPair<>(0, source);

	    Set<CostVertexPair<MarvelNode>> settledNodes = new HashSet<>();
	    Set<CostVertexPair<MarvelNode>> unsettledNodes = new HashSet<>();

	    unsettledNodes.add(sourcePath);

	    while (unsettledNodes.size() != 0) {
	    	CostVertexPair<MarvelNode> currentNode = getLowestDistanceNode(unsettledNodes);
	        unsettledNodes.remove(currentNode);
	        for (CostVertexPair<MarvelNode> adjacencyPair: getAdjacentNodes(currentNode)) {
	        	Vertex<MarvelNode> adjacentNode = adjacencyPair.getVertex();
	            Integer edgeWeight = adjacencyPair.getCost();
	            if (!settledNodes.contains(adjacentNode)) {
	                calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
	                unsettledNodes.add(adjacencyPair);
	            }
	        }
	        settledNodes.add(currentNode);
	    }
	    return paths;
	}
	
	private Set<CostVertexPair<MarvelNode>> getAdjacentNodes(CostVertexPair<MarvelNode> currentNode) {
		Set<CostVertexPair<MarvelNode>> adjacentVertices = new HashSet<>();
		for (Edge<MarvelNode> edge : currentNode.getVertex().getEdges()) {
			if (edge.getFromVertex().equals(currentNode.getVertex())) {
				adjacentVertices.add(new CostVertexPair<MarvelNode>(0, edge.getToVertex()));
			}
		}
		return adjacentVertices;
	}
	
	private static CostVertexPair<MarvelNode> getLowestDistanceNode(Set<CostVertexPair<MarvelNode>> unsettledNodes) {
		CostVertexPair<MarvelNode> lowestDistanceNode = null;
	    int lowestDistance = Integer.MAX_VALUE;
	    for (CostVertexPair<MarvelNode> node: unsettledNodes) {
	        int nodeDistance = node.getCost();
	        if (nodeDistance < lowestDistance) {
	            lowestDistance = nodeDistance;
	            lowestDistanceNode = node;
	        }
	    }
	    return lowestDistanceNode;
	}
	
	private static void calculateMinimumDistance(CostVertexPair<MarvelNode> evaluationNode, Integer edgeWeigh, CostVertexPair<MarvelNode> sourceNode) {
	    Integer sourceDistance = sourceNode.getCost();
	    if (sourceDistance + edgeWeigh < evaluationNode.getCost()) {
	        evaluationNode.setCost(sourceDistance + edgeWeigh);
	        LinkedList<CostVertexPair<MarvelNode>> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
	        shortestPath.add(sourceNode);
	        evaluationNode.setShortestPath(shortestPath);
	    }
	}
}
