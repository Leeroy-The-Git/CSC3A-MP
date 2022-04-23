import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartRandomPlacementStrategy;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import model.data.DataParser;
import model.data.GraphParser;
import model.graph.DijkstrasAlgorithm;
import model.marvel.MarvelNode;
import model.marvel.NODE_TYPE;

public class Main extends Application {
	private static com.jwetherell.algorithms.data_structures.Graph<MarvelNode> graph;
	private static Graph<MarvelNode, String> g = new DigraphEdgeList<>();;
	private static String[] WEIGHT_IN_COMIC = {
			"Appeared In", // Full Appearance
			"Briefly Appeared In", // Partial Credit in brackets
			"Was Off Panel In", // Off Panel
			"Had A Voice Over In", // Voice Over
			"Was A Flashback In", // Flash back
			"Was BTS In" // Behind The Scenes
	};
	
	private static String[] WEIGHT_OUT_COMIC = {
			"With Appearance Of", // Full Appearance
			"With A Brief Appearance Of", // Partial Credit in brackets
			"With Off Panel Reference To", // Off Panel
			"With Voice Over Of", // Voice Over
			"With A Flashback Of", // Flash back
			"With BTS Reference To" // Behind The Scenes
	};
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		SmartPlacementStrategy strategy = new SmartRandomPlacementStrategy();
		SmartGraphPanel<MarvelNode, String> graphView = new SmartGraphPanel<>(g, strategy);
		graphView.setAutomaticLayout(true);
		Scene scene = new Scene(graphView, 1024, 768);
		
		EventHandler<KeyEvent> keyDown = new EventHandler<KeyEvent>() { 
			@Override 
			public void handle(KeyEvent event) { 
				if (event.getCode() == KeyCode.ENTER) {
					findPaths();
					graphView.update();
				}
			}           
		};
		

		Stage stage = new Stage(StageStyle.DECORATED);
		stage.addEventHandler(KeyEvent.KEY_PRESSED, keyDown);
		stage.setTitle("JavaFXGraph Visualization");
		stage.setScene(scene);
		stage.show();

		//IMPORTANT - Called after scene is displayed so we can have width and height values
		graphView.init();
	}
	
	public static void main (String[] args) {
		System.out.println("==============================================");
		System.out.println("Parsing Data...");
//		parseData();
		System.out.println("Data Parsed");
		System.out.println("");
		
		System.out.println("==============================================");
		System.out.println("Processing Graph...");
		parseGraph();
		findPaths();
		launch(args);
	}
	
	private static void parseData() {
		try {
			DataParser.parseData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void parseGraph() {
		GraphParser gp = new GraphParser("data/final/edges.txt");
		try {
			graph = gp.processGraph();
			System.out.println("Graph Processed");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void findPaths() {
		Collection<Edge<String, MarvelNode>> edges = g.edges();
		for (Edge<String, MarvelNode> edge : edges) {
			g.removeEdge(edge);
		}
		Collection<Vertex<MarvelNode>> vertices = g.vertices();
		for (Vertex<MarvelNode> vertex : vertices) {
			g.removeVertex(vertex);
		}
		List<com.jwetherell.algorithms.data_structures.Graph.Vertex<MarvelNode>> list = graph.getVertices();
		Random random = new Random(System.currentTimeMillis());
		com.jwetherell.algorithms.data_structures.Graph.Vertex<MarvelNode> from = list.get(Math.abs(random.nextInt()) % list.size());
		while (from.getValue().getType() != NODE_TYPE.HERO)
			from = list.get(Math.abs(random.nextInt()) % list.size());
		com.jwetherell.algorithms.data_structures.Graph.Vertex<MarvelNode> to = list.get(Math.abs(random.nextInt()) % list.size());
		while (to.getValue().getType() != NODE_TYPE.HERO)
			to = list.get(Math.abs(random.nextInt()) % list.size());
		System.out.println("From: " + from.getValue() + "\tTo: " + to.getValue());
		com.jwetherell.algorithms.data_structures.Graph.CostPathPair<MarvelNode> path = DijkstrasAlgorithm.getShortestPath(graph, from, to);
		System.out.println(path);
		List<com.jwetherell.algorithms.data_structures.Graph.Edge<MarvelNode>> pathEdges = path.getPath();
		if (pathEdges.size() > 0) {
			Vertex<MarvelNode> fromVertex = g.insertVertex(pathEdges.get(0).getFromVertex().getValue());
			int count = 0;
			for (com.jwetherell.algorithms.data_structures.Graph.Edge<MarvelNode> edge : pathEdges) {
				count++;
				Vertex<MarvelNode> toVertex = g.insertVertex(edge.getToVertex().getValue());
				String edgeValue;
				if (toVertex.element().getType() == NODE_TYPE.COMIC) {
					edgeValue = "[" + count + "] " + WEIGHT_IN_COMIC[edge.getCost() - 1];
				} else {
					edgeValue = "[" + count + "] " + WEIGHT_OUT_COMIC[edge.getCost() - 1];
					
				}
				g.insertEdge(fromVertex.element(), toVertex.element(), edgeValue);
				fromVertex = toVertex;
			}
		} else {
			System.out.println("No path found!");
			g.insertVertex(from.getValue());
			g.insertVertex(to.getValue());
		}
	}
}
