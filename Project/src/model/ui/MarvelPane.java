package model.ui;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.jwetherell.algorithms.data_structures.Graph;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import model.graph.DijkstrasAlgorithm;
import model.marvel.MarvelEdge;
import model.marvel.MarvelNode;
import model.marvel.NODE_TYPE;

/**
 * MarvelPane for the graph UI
 * @author JARED SWANZEN (220134523)
 *
 */
public class MarvelPane extends BorderPane{
	private Graph<MarvelNode> graph;
	private com.brunomnsilva.smartgraph.graph.Graph<MarvelNode, MarvelEdge> g = new DigraphEdgeList<>();
	private List<String> comboList = new ArrayList<>();
	private SmartGraphPanel<MarvelNode, MarvelEdge> graphView;
	
	private ComboBox<String> cmbFrom;
	private ComboBox<String> cmbTo;
	
	/**
	 * Constructor with graph initialization
	 * @param graph graph to initialize
	 */
	public MarvelPane(Graph<MarvelNode> graph) {
		this();
		this.graph = graph;
	}
	
	/**
	 * Default Constructor 
	 */
	public MarvelPane() {
		try {
			// Initialize graph view
			SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
			SmartGraphProperties properties = new SmartGraphProperties(MarvelPane.class.getResourceAsStream("/smartgraph/smartgraph.properties"));
			graphView = new SmartGraphPanel<>(g, properties, strategy, MarvelPane.class.getResource("/smartgraph/smartgraph.css").toURI());
			graphView.setAutomaticLayout(true);
			graphView.setMinHeight(200);
			graphView.setMinWidth(500);
			
			this.setCenter(graphView);
			
			// Generate lists for use in combo boxes 
			generateComboList();
			
			// When the user presses enter, find the shortest path
			EventHandler<KeyEvent> keyDown = new EventHandler<KeyEvent>() { 
				@Override 
				public void handle(KeyEvent event) { 
					if (event.getCode() == KeyCode.ENTER) {
						findShortestPath();
						graphView.updateAndWait();
						styleVertices();
					}
				}           
			};
			
			this.addEventHandler(KeyEvent.KEY_PRESSED, keyDown);
			
			// Button to find shortest path
			Button btnFindShortestPath = new Button("Find shortest path");
			
			btnFindShortestPath.setOnAction(e -> {
				findShortestPath();
				graphView.updateAndWait();
				styleVertices();
			});
			
			// Setup view
			VBox vbox = new VBox(25, new VBox(5, new Label("From:"), cmbFrom), new VBox(5, new Label("To:"), cmbTo), btnFindShortestPath);
			vbox.setPadding(new Insets(10));
			
			this.setBottom(vbox);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Style vertices based on them being a comic or a vertex
	 */
	private void styleVertices() {
		Collection<Vertex<MarvelNode>> list = g.vertices();
		for (Vertex<MarvelNode> vertex : list) {
			String style = vertex.element().getType() == NODE_TYPE.HERO ? "heroVertex" : "comicVertex";
			graphView.getStylableVertex(vertex.element()).setStyleClass(style);
		}
	}

	/**
	 * Add combo box lists to combo boxes
	 */
	private void setupComboBoxes() {
		if (cmbFrom == null)
			cmbFrom = new ComboBox<>(FXCollections.observableArrayList(comboList));
		else
			cmbFrom.setItems(FXCollections.observableArrayList(comboList));
		new AutoCompleteComboBoxListener<>(cmbFrom);
		
		if (cmbTo == null)
			cmbTo = new ComboBox<>(FXCollections.observableArrayList(comboList));
		else
			cmbTo.setItems(FXCollections.observableArrayList(comboList));
		new AutoCompleteComboBoxListener<>(cmbTo);
	}

	/**
	 * Generate combo box lists from heroes present in graph
	 */
	private void generateComboList() {
		if (graph != null) {
			List<com.jwetherell.algorithms.data_structures.Graph.Vertex<MarvelNode>> list = graph.getVertices();
			for (int i = 0; i < list.size(); i++) {
		        if (list.get(i).getValue().getType() == NODE_TYPE.HERO) {
		            comboList.add(list.get(i).getValue().getName());
		        }
		    }
		}
		setupComboBoxes();
	}

	/**
	 * Find and display shortest path
	 */
	private void findShortestPath() {
		// Get from and to vertices
		Graph.Vertex<MarvelNode> from = getNode(cmbFrom.getValue().trim());
		Graph.Vertex<MarvelNode> to = getNode(cmbTo.getValue().trim());
		
		// One of the selected vertices were invalid
		if (from == null || to == null)
			return;
		
		// Clear display
		Collection<Edge<MarvelEdge, MarvelNode>> edges = g.edges();
		for (Edge<MarvelEdge, MarvelNode> edge : edges) {
			g.removeEdge(edge);
		}
		
		Collection<Vertex<MarvelNode>> vertices = g.vertices();
		for (Vertex<MarvelNode> vertex : vertices) {
			g.removeVertex(vertex);
		}
		
		// Get shortest path
		Graph.CostPathPair<MarvelNode> path = DijkstrasAlgorithm.getShortestPath(graph, from, to);
		System.out.println(path);
		
		// Output path to graph
		List<Graph.Edge<MarvelNode>> pathEdges = path.getPath();
		if (pathEdges.size() > 0) {
			Vertex<MarvelNode> fromVertex = g.insertVertex(pathEdges.get(0).getFromVertex().getValue());
			for (Graph.Edge<MarvelNode> edge : pathEdges) {
				Vertex<MarvelNode> toVertex = g.insertVertex(edge.getToVertex().getValue());
				g.insertEdge(fromVertex.element(), toVertex.element(), new MarvelEdge(edge.getCost(), toVertex.element().getType() == NODE_TYPE.COMIC));
				fromVertex = toVertex;
			}
		} else {
			System.out.println("No path found!");
			g.insertVertex(from.getValue());
			g.insertVertex(to.getValue());
		}
	}

	/**
	 * Get node based on selected item in combo box
	 * @param name name to find
	 * @return vertex that matches the selected item
	 */
	private Graph.Vertex<MarvelNode> getNode(String name) {		
		Iterator<Graph.Vertex<MarvelNode>> nodesIterator = graph.getVertices().iterator();
		while (nodesIterator.hasNext()) {
			Graph.Vertex<MarvelNode> vertex = nodesIterator.next();
			if (vertex.getValue().getName().equals(name)) {
				return vertex;
			}
		}
		return null;
	}
	
	/**
	 * @return graph
	 */
	public Graph<MarvelNode> getGraph() {
		return graph;
	}
	
	/**
	 * @param graph graph to set
	 */
	public void setGraph(Graph<MarvelNode> graph) {
		this.graph = graph;
		generateComboList();
	}
	
	/**
	 * @return graphView
	 */
	public SmartGraphPanel<MarvelNode, MarvelEdge> getGraphView() {
		return graphView;
	}
	
	
}
