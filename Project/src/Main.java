import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.jwetherell.algorithms.data_structures.Graph;
import com.jwetherell.algorithms.data_structures.Graph.Vertex;

import model.data.DataParser;
import model.data.GraphParser;
import model.graph.BreadthFirstSearch;
import model.marvel.MarvelNode;

public class Main {

	public static void main(String[] args) {
		try {
			DataParser.parseSmallData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Data Parsed");
				
		System.out.println("");
		System.out.println("Processing Graph...");
		GraphParser gp = new GraphParser("data/final/small-edges.txt");
		try {
			Graph<MarvelNode> graph = gp.processGraph();
			System.out.println("Graph Processed");
			BreadthFirstSearch<MarvelNode> bfs = new BreadthFirstSearch<>();
			List<Vertex<MarvelNode>> list = graph.getVertices();
			Random random = new Random(System.currentTimeMillis());
			Vertex<MarvelNode> from = list.get(Math.abs(random.nextInt()) % list.size());
			Vertex<MarvelNode> to = list.get(Math.abs(random.nextInt()) % list.size());
			System.out.println("From: " + from.getValue() + "\tTo: " + to.getValue());
			List<Vertex<MarvelNode>> result = bfs.search(from, to.getValue());
			for (Vertex<MarvelNode> vertex : result) {
				System.out.println(vertex.getValue());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
