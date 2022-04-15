package model.data;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.jwetherell.algorithms.data_structures.Graph;
import com.jwetherell.algorithms.data_structures.Graph.Edge;
import com.jwetherell.algorithms.data_structures.Graph.TYPE;
import com.jwetherell.algorithms.data_structures.Graph.Vertex;

import model.marvel.MarvelNode;
import model.marvel.NODE_TYPE;

public class ParserThread extends Thread {
	private static List<Vertex<MarvelNode>> heroVertices = new ArrayList<>();
	private static List<Vertex<MarvelNode>> comicVertices = new ArrayList<>();
	private static List<Edge<MarvelNode>> edges = new ArrayList<>();	
	private static Graph<MarvelNode> graph = new Graph<MarvelNode>(TYPE.UNDIRECTED);	

	private static List<String> heroes = new ArrayList<>();
	private static List<String> comics = new ArrayList<>();
	
	private ArrayList<String> lines;
	
	public ParserThread(ArrayList<String> lines) {
		this.lines = lines;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		for (String line : lines) {
			StringTokenizer tokens = new StringTokenizer(line, "\t");
			String hero = tokens.nextToken();
			String comic = tokens.nextToken();
			int cost = Integer.parseInt(tokens.nextToken());
			
			Vertex<MarvelNode> heroNode = null;
			Vertex<MarvelNode> comicNode = null;

			if (!heroes.contains(hero)) {
				heroNode = new Vertex<MarvelNode>(new MarvelNode(NODE_TYPE.HERO, hero));
				while (!heroVertices.contains(heroNode))
					heroVertices.add(heroNode);
				heroes.add(hero);
				graph.addVertex(heroNode);
			} else {
				heroNode = getHeroNode(hero);
				int count = 0;
				while (heroNode == null && count <= 10) {
					System.err.println("Sleeping...");
					try {
						sleep(100);
						heroNode = getHeroNode(hero);
						count++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (heroNode == null) {
					heroNode = new Vertex<MarvelNode>(new MarvelNode(NODE_TYPE.HERO, hero));
					while (!heroVertices.contains(heroNode))
						heroVertices.add(heroNode);
					graph.addVertex(heroNode);
				}
			}
			if (!comics.contains(comic)) {
				comicNode = new Vertex<MarvelNode>(new MarvelNode(NODE_TYPE.COMIC, comic));
				while (!comicVertices.contains(comicNode))
					comicVertices.add(comicNode);
				comics.add(comic);
				graph.addVertex(comicNode);
			} else {
				comicNode = getComicNode(comic);
				int count = 0;
				while (comicNode == null && count <= 10) {
					System.err.println("Sleeping...");
					try {
						sleep(100);
						comicNode = getComicNode(comic);
						count++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (comicNode == null) {
					comicNode = new Vertex<MarvelNode>(new MarvelNode(NODE_TYPE.COMIC, comic));
					while (!comicVertices.contains(comicNode))
						comicVertices.add(comicNode);
					graph.addVertex(comicNode);
				}
			}
			
			Edge<MarvelNode> edge = new Edge<MarvelNode>(cost, heroNode, comicNode);
			graph.addEdge(edge);
			
			if (graph.getEdges().size() % 10000 == 0) {
				System.out.println("Processed Edges: " + graph.getEdges().size());
			}
		}
	}
	
	private Vertex<MarvelNode> getHeroNode(String name) {
		try {
			Iterator<Vertex<MarvelNode>> nodesIterator = heroVertices.iterator();
			while (nodesIterator.hasNext()) {
				Vertex<MarvelNode> vertex = nodesIterator.next();
				if (vertex.getValue().getName().equals(name)) {
					return vertex;
				}
			}
			return null;
		} catch (ConcurrentModificationException e) {
			return getHeroNode(name);
		}
	}
	
	private Vertex<MarvelNode> getComicNode(String name) {
		try {
			Iterator<Vertex<MarvelNode>> nodesIterator = comicVertices.iterator();
			while (nodesIterator.hasNext()) {
				Vertex<MarvelNode> vertex = nodesIterator.next();
				if (vertex.getValue().getName().equals(name)) {
					return vertex;
				}
			}
			return null;
		} catch (ConcurrentModificationException e) {
			return getComicNode(name);
		}
	}
	
	/**
	 * @param graph the graph to set
	 */
	public static Graph<MarvelNode> getGraph() {
		return graph;
	}
	/**
	 * @return the vertices
	 */
	public static List<Vertex<MarvelNode>> getVertices() {
		List<Vertex<MarvelNode>> list = new ArrayList<>();
		list.addAll(heroVertices);
		list.addAll(comicVertices);
		return list;
	}
	/**
	 * @return the edges
	 */
	public static List<Edge<MarvelNode>> getEdges() {
		return edges;
	}	
}
