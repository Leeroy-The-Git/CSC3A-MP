import java.io.FileNotFoundException;

import com.jwetherell.algorithms.data_structures.Graph;

import model.data.DataParser;
import model.data.GraphParser;
import model.marvel.MarvelNode;

public class Main {

	public static void main(String[] args) {
//		try {
//			DataParser.parseData();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		

		System.out.println("Processing Graph...");
		GraphParser gp = new GraphParser();
		try {
			Graph<MarvelNode> graph = gp.processGraphThreaded();
			System.out.println("Graph Processed");
			//System.out.println(graph.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
