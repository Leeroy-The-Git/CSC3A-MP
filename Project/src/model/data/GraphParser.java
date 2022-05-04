package model.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.jwetherell.algorithms.data_structures.Graph;
import com.jwetherell.algorithms.data_structures.Graph.Edge;
import com.jwetherell.algorithms.data_structures.Graph.TYPE;
import com.jwetherell.algorithms.data_structures.Graph.Vertex;

import model.marvel.MarvelNode;
import model.marvel.NODE_TYPE;

public class GraphParser {	
	private String EDGES_PATH = "data/final/edges.txt";
	private Graph<MarvelNode> graph = null;
	
	private List<Vertex<MarvelNode>> heroVertices = new ArrayList<>();
	private List<Vertex<MarvelNode>> comicVertices = new ArrayList<>();

	private List<String> heroes = new ArrayList<>();
	private List<String> comics = new ArrayList<>();

	public GraphParser() {
	
	}
	
	public GraphParser(String edgesPath) {
		EDGES_PATH = edgesPath;
	}
	
	public Graph<MarvelNode> processGraph() throws FileNotFoundException {
		System.out.println("Processing Edges and Vertices");
		long startTime = System.currentTimeMillis();
		Scanner sc = new Scanner(new File(EDGES_PATH));
		graph = new Graph<MarvelNode>(TYPE.UNDIRECTED);
		Vertex<MarvelNode> heroNode = null;
		while (sc.hasNext()) {
			String line = sc.nextLine();
			StringTokenizer tokens = new StringTokenizer(line, "\t");
			String hero = tokens.nextToken();
			String comic = tokens.nextToken();
			int cost = Integer.parseInt(tokens.nextToken());
			
			Vertex<MarvelNode> comicNode;
			if (heroNode == null || !heroNode.getValue().getName().equals(hero)) {
				if (!heroes.contains(hero)) {
					heroes.add(hero);
					heroNode = new Vertex<MarvelNode>(new MarvelNode(NODE_TYPE.HERO, hero));
					heroVertices.add(heroNode);
					graph.addVertex(heroNode);
				} else {
					heroNode = getHeroNode(hero); 
				}
			}
			if (!comics.contains(comic)) {
				comics.add(comic);
				comicNode = new Vertex<MarvelNode>(new MarvelNode(NODE_TYPE.COMIC, comic));
				comicVertices.add(comicNode);
				graph.addVertex(comicNode);
			} else {
				comicNode = getComicNode(comic);
			}
			
			Edge<MarvelNode> edge = new Edge<MarvelNode>(cost, heroNode, comicNode);
			graph.addEdge(edge);
			
			if (graph.getEdges().size() % 10000 == 0) {
				System.out.println("Processed Edges: " + graph.getEdges().size());
			}
		}
		long endTime = System.currentTimeMillis();
		double totalTime = (endTime - startTime)/1000.0;
		System.out.println("Processed edges and vertices in " + totalTime + " seconds");
		return graph;
	}

	private Vertex<MarvelNode> getHeroNode(String name) {
		Iterator<Vertex<MarvelNode>> nodesIterator = heroVertices.iterator();
		while (nodesIterator.hasNext()) {
			Vertex<MarvelNode> vertex = nodesIterator.next();
			if (vertex.getValue().getName().equals(name)) {
				return vertex;
			}
		}
		return null;
	}
	
	private Vertex<MarvelNode> getComicNode(String name) {		
		Iterator<Vertex<MarvelNode>> nodesIterator = comicVertices.iterator();
		while (nodesIterator.hasNext()) {
			Vertex<MarvelNode> vertex = nodesIterator.next();
			if (vertex.getValue().getName().equals(name)) {
				return vertex;
			}
		}
		return null;
	}
	
	public void setEdgeFileLoc(String loc) {
		EDGES_PATH = loc;
	}
	
	public String getEdgeFileLoc() {
		return EDGES_PATH;
	}
}
