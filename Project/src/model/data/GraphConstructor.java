package model.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import com.jwetherell.algorithms.data_structures.Graph;
import com.jwetherell.algorithms.data_structures.Graph.Edge;
import com.jwetherell.algorithms.data_structures.Graph.TYPE;
import com.jwetherell.algorithms.data_structures.Graph.Vertex;

import model.marvel.MarvelNode;
import model.marvel.NODE_TYPE;

/**
 * Class dedicated to the parsing of a {@link Graph} from 
 * @author JARED SWANZEN (220134523)
 *
 */
public class GraphConstructor {	
	private String PRE_EDGES_PATH = "/final/edges.txt";
	private String EDGES_PATH = "edges.txt";
	private Graph<MarvelNode> graph = null;
	
	private List<Vertex<MarvelNode>> heroVertices = new ArrayList<>();
	private List<Vertex<MarvelNode>> comicVertices = new ArrayList<>();

	private List<String> heroes = new ArrayList<>();
	private List<String> comics = new ArrayList<>();

	/**
	 * Default constructor
	 */
	public GraphConstructor() {
		
	}
	
	
	/**
	 * Constructor with edge path 
	 * @param edgesPath path to edge file
	 */
	public GraphConstructor(String edgesPath) {
		EDGES_PATH = edgesPath;
	}
	
	/**
	 * Constructs a graph from a file containing graph edges
	 * @return graph
	 * @throws FileNotFoundException
	 */
	public Graph<MarvelNode> constructGraph() throws FileNotFoundException {
		System.out.println("Processing Edges and Vertices");
		long startTime = System.currentTimeMillis();
		File edgeFile = new File(EDGES_PATH);
		Scanner sc;
		if (edgeFile.exists()) {
			System.out.println("Using user generated edges");
			sc = new Scanner(edgeFile);
		} else {
			System.out.println("Using pre-generated edges");
			sc = new Scanner(GraphConstructor.class.getResourceAsStream(PRE_EDGES_PATH));
		}
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
		sc.close();
		long endTime = System.currentTimeMillis();
		double totalTime = (endTime - startTime)/1000.0;
		System.out.println("Processed edges and vertices in " + totalTime + " seconds");
		return graph;
	}

	/**
	 * Get a hero vertex from the list of vertices
	 * @param name name of hero
	 * @return hero vertex
	 */
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
	
	/**
	 * Get a comic vertex from the list of vertices
	 * @param name name of comic
	 * @return comic vertex
	 */
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
	
	/**
	 * Set file location
	 * @param loc location to set
	 */
	public void setEdgeFileLoc(String loc) {
		EDGES_PATH = loc;
	}
	
	/**
	 * Get file location
	 * @return file location
	 */
	public String getEdgeFileLoc() {
		return EDGES_PATH;
	}
}
