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
import com.sun.javafx.geom.CubicApproximator;

import model.marvel.MarvelNode;
import model.marvel.NODE_TYPE;

public class GraphParser {
	private static final int MAX_T = 5;
	private static final int MAX_L = 25000;
	
	private String EDGES_PATH = "data/final/edges.txt";
	private String STRUCTURED_EDGES_PATH = "data/final/structuredEdges.txt";
	private String STRUCTURED_EDGES_PATH_THREAD = "data/final/structuredEdgesThreaded.txt";
	private Graph<MarvelNode> graph = null;
	
	private List<Vertex<MarvelNode>> heroVertices = new ArrayList<>();
	private List<Vertex<MarvelNode>> comicVertices = new ArrayList<>();

	private List<String> heroes = new ArrayList<>();
	private List<String> comics = new ArrayList<>();

	public GraphParser() {
	
	}
	
	GraphParser(String edgesPath) {
		EDGES_PATH = edgesPath;
	}
	
	public Graph<MarvelNode> processGraph() throws FileNotFoundException {
		System.out.println("Processing Edges and Vertices");
		long startTime = System.currentTimeMillis();
		Scanner sc = new Scanner(new File(EDGES_PATH));
		graph = new Graph<MarvelNode>(TYPE.UNDIRECTED);
		while (sc.hasNext()) {
			String line = sc.nextLine();
			StringTokenizer tokens = new StringTokenizer(line, "\t");
			String hero = tokens.nextToken();
			String comic = tokens.nextToken();
			int cost = Integer.parseInt(tokens.nextToken());
			
			Vertex<MarvelNode> heroNode;
			Vertex<MarvelNode> comicNode;

			if (!heroes.contains(hero)) {
				heroes.add(hero);
				heroNode = new Vertex<MarvelNode>(new MarvelNode(NODE_TYPE.HERO, hero));
				heroVertices.add(heroNode);
				graph.addVertex(heroNode);
			} else {
				heroNode = getHeroNode(hero); 
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
		System.out.println("");
		
		System.out.println("Outputting Graph");
		PrintWriter pw = new PrintWriter(new File(STRUCTURED_EDGES_PATH));
		pw.write(graph.toString());
		pw.flush();
		pw.close();
		return graph;
	}
	

	public Graph<MarvelNode> processGraphThreaded() throws FileNotFoundException {
		System.out.println("Processing Edges and Vertices");
		long startTime = System.currentTimeMillis();
		Scanner sc = new Scanner(new File(EDGES_PATH));
		ArrayList<String> lines = new ArrayList<>();
		ExecutorService es = Executors.newFixedThreadPool(MAX_T);
		while (sc.hasNext()) {
			String line = sc.nextLine();
			lines.add(line);
			if (lines.size() >= MAX_L) {
				es.execute(new ParserThread(new ArrayList<>(lines)));
				lines.clear();
			}
		}
		es.execute(new ParserThread(lines));		
		es.shutdown();
		graph = ParserThread.getGraph();
		
		int count = 0;
		try {
			while (!es.awaitTermination(1, TimeUnit.MINUTES) && count < 10) {
				count++;
				System.out.println("Timeout reached: " + count + "/10");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		long endTime = System.currentTimeMillis();
		double totalTime = (endTime - startTime)/1000.0;
		System.out.println("Processed edges and vertices in " + totalTime + " seconds");
		System.out.println("");
		
		System.out.println("Outputting Graph with " + graph.getEdges().size() + " edges");
		PrintWriter pw = new PrintWriter(new File(STRUCTURED_EDGES_PATH_THREAD));
		pw.write(graph.toString());
		pw.flush();
		pw.close();
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
