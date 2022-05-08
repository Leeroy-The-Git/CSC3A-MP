package model.ui;

import java.io.InputStream;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * About Pane to show information about the system
 * @author JARED SWANZEN (220134523)
 *
 */
public class AboutPane extends ScrollPane {
	// banner image location
	private static final String BANNER_LOC = "/images/banner.jpg";
	
	// Font styling
	private static final String BLUE = "-fx-stroke: cornflowerblue";
	private static final String BOLD = "-fx-font-weight: bold";
	private static final String LIGHTER = "-fx-font-weight: lighter";
	private static final String OBLIQUE = "-fx-font-style: oblique";
	private static final String LINK = OBLIQUE + ";" + BLUE;
	
	// About text
	private static final String ABOUT_HEAD = "ABOUT THE SYSTEM:\r\n";
	private static final String ABOUT_TEXT = "The Six degrees of Marvel Comics is an adaptation to the cultural phenomenon - The six Degrees of Kevin Bacon. It attempts to connect any two marvel heroes (or villians) with their comic book appearances. \r\n"
			+ "\r\n"
			+ "With a dataset of over 340 000 hero-to-comic connections you are sure to find a path between any two heroes.\r\n"
			+ "\r\n"
			+ "I hope you have as much fun as I had trying to find a hero that doesn't connect to Spider-Man in less than 3 degrees. \r\n"
			+ "\r\n"
			+ "For some further help and instructions, read below.\r\n"
			+ "\r\n"
			+ "Enjoy!";
	
	// Clean Data Text
	private static final String CLEAN_DATA_HEAD = "CLEANING DATA:\r\n";
	private static final String CLEAN_DATA_PREFIX = "The first step to getting our dataset is to clean the data. This uses a few raw files that were by using a web scraping tool on the chronology project: ";
	private static final String CLEAN_DATA_LINK =  "https://www.chronologyproject.com";
	private static final String CLEAN_DATA_MID =  ".\r\n\r\n"
			+ "We clean up this data and output all the edges to a file (edges.txt) using the following format:\r\n";
	private static final String CLEAN_DATA_SYNTAX =  "HERO\\tCOMIC\\tWEIGHT\r\n";
	private static final String CLEAN_DATA_SUFFIX =  "\r\n"
			+ "This data will be used to construct the graph.";
	
	// Construct Graph Text
	private static final String CONSTRUCT_GRAPH_HEAD = "CONSTRUCT GRAPH:\r\n";
	private static final String CONSTRUCT_GRAPH_TEXT = "Next we construct the graph. Here we create a vertex for every unique hero or comic and create an edge for every edge in the file.\r\n"
			+ "\r\n"
			+ "After adding all of these vertices and edges to the graph we can start searching and visualizing the graph.";
	
	// Select Heroes Text
	private static final String SELECT_HERO_HEAD = "SELECT HEROES:\r\n";
	private static final String SELECT_HERO_TEXT = "Using the provided comboboxes we can select a from hero (the \"start\") and a to hero (the \"end\"). We find the vertex corresponding to each of them.\r\n"
			+ "\r\n"
			+ "Each combo box features an auto complete function that allows you to find your heroes quicker by simply typing their name. Please remember to select a hero from the list, otherwise the system won't be able to find run the search algoritm.\r\n";

	// Shortest Path Text
	private static final String SHORTEST_PATH_HEAD = "FIND SHORTEST PATH:\r\n";
	private static final String SHORTEST_PATH_TEXT = "The shortest path function makes use of Dijkstra's Algorithm. Since it runs in O(log(n)) time Dijkstra works very well on big datasets.\r\n"
			+ "\r\n"
			+ "A quick explanation of the algorithm:\r\n"
			+ "1. Create a set of unvisited nodes and add all the nodes to it.\r\n"
			+ "2. Assign a tentative distance to every node (0 for source node and Integer.MAX for every other node). This distance will be updated with the actual distance between the source and respective node.\r\n"
			+ "3. For the current node (starting at the intitial node) consider every connected node and calculate the distance to that node. Compare the calculated distance to the currente tentative distance and take the smaller one.\r\n"
			+ "4. After considering all the unvisited nodes, remove the current node from the unvisited set.\r\n"
			+ "5. If the destination node has been visited, or the smallest distance is Integer.MAX, the algorithm is finished\r\n"
			+ "6. Otherwise, select the unvisited node with the smallest distance and set it as the current node and repeat from step 3.\r\n";

	// Resources Text
	private static final String RESOURCES_HEAD = "RESOURCES:\r\n";
	private static final String RESOURCES_PREFIX = "Banner image was taken from the following link: ";
	private static final String RESOURCES_BANNER_LINK = "http://blackclaws12.deviantart.com/art/Marvel-banner-606754106";
	private static final String RESOURCES_MID = "\r\n"
			+ "\r\n"
			+ "Visualization library provided by Bruno Silva: ";
	private static final String RESOURCES_SMARTGRAPH_LINK = "https://github.com/brunomnsilva/JavaFXSmartGraph";
	private static final String RESOURCES_SUFFIX= "\r\n";
	
	private static final int WIDTH = 715;
	private static final int PADDING = 20;
	
	public AboutPane() {
		VBox content = new VBox(25);
		content.setAlignment(Pos.TOP_CENTER);
		content.setPadding(new Insets(PADDING));
		InputStream in = AboutPane.class.getResourceAsStream(BANNER_LOC);
		Image image = new Image(in, WIDTH + 50, 0, true, true);
		ImageView iv = new ImageView(image);
		
		// Setup about information
		TextFlow aboutFlow = new TextFlow();
		aboutFlow.setMaxWidth(WIDTH);
		Text aboutHead = new Text(ABOUT_HEAD);
		aboutHead.setStyle(BOLD);
		Text aboutText = new Text(ABOUT_TEXT);
		aboutFlow.getChildren().addAll(aboutHead, aboutText);
		
		// Setup clean data information
		TextFlow cleanDataFlow = new TextFlow();
		cleanDataFlow.setMaxWidth(WIDTH);
		Text cleanDataHead = new Text(CLEAN_DATA_HEAD);
		cleanDataHead.setStyle(BOLD);
		Text cleanDataPrefix = new Text(CLEAN_DATA_PREFIX);
		Text cleanDataLink = new Text(CLEAN_DATA_LINK);
		cleanDataLink.setStyle(LINK);
		Text cleanDataMid = new Text(CLEAN_DATA_MID);
		Text cleanDataSyntax = new Text(CLEAN_DATA_SYNTAX);
		cleanDataSyntax.setStyle(OBLIQUE + ";" + LIGHTER);
		Text cleanDataSuffix = new Text(CLEAN_DATA_SUFFIX);
		cleanDataFlow.getChildren().addAll(cleanDataHead, cleanDataPrefix, cleanDataLink, cleanDataMid, cleanDataSyntax, cleanDataSuffix);
		
		//Setup construct graph information
		TextFlow constructGraphFlow = new TextFlow();
		constructGraphFlow.setMaxWidth(WIDTH);
		Text constructGraphHead = new Text(CONSTRUCT_GRAPH_HEAD);
		constructGraphHead.setStyle(BOLD);
		Text constructGraphText = new Text(CONSTRUCT_GRAPH_TEXT);
		constructGraphFlow.getChildren().addAll(constructGraphHead, constructGraphText);
		
		//Setup select heroes information
		TextFlow selectHeroesFlow = new TextFlow();
		selectHeroesFlow.setMaxWidth(WIDTH);
		Text selectHeroesHead = new Text(SELECT_HERO_HEAD);
		selectHeroesHead.setStyle(BOLD);
		Text selectHeroesText = new Text(SELECT_HERO_TEXT);
		selectHeroesFlow.getChildren().addAll(selectHeroesHead, selectHeroesText);
		
		// Setup find shortest path information
		TextFlow shortestPathFlow = new TextFlow();
		shortestPathFlow.setMaxWidth(WIDTH);
		Text shortestPathHead = new Text(SHORTEST_PATH_HEAD);
		shortestPathHead.setStyle(BOLD);
		Text shortestPathText = new Text(SHORTEST_PATH_TEXT);
		shortestPathFlow.getChildren().addAll(shortestPathHead, shortestPathText);
		
		// Setup resources information
		TextFlow resourcesFlow = new TextFlow();
		resourcesFlow.setMaxWidth(WIDTH);
		Text resourcesHead = new Text(RESOURCES_HEAD);
		resourcesHead.setStyle(BOLD);
		Text resourcesPrefix = new Text(RESOURCES_PREFIX);
		Text resourcesBannerLink = new Text(RESOURCES_BANNER_LINK);
		resourcesBannerLink.setStyle(LINK);
		Text resourcesMid = new Text(RESOURCES_MID);
		Text resourcesSmartgraphLink = new Text(RESOURCES_SMARTGRAPH_LINK);
		resourcesSmartgraphLink.setStyle(LINK);
		Text resourcesSuffix = new Text(RESOURCES_SUFFIX);
		resourcesFlow.getChildren().addAll(resourcesHead, resourcesPrefix, resourcesBannerLink, resourcesMid, resourcesSmartgraphLink, resourcesSuffix);
		
		content.getChildren().addAll(iv, aboutFlow, cleanDataFlow, constructGraphFlow, selectHeroesFlow, shortestPathFlow, resourcesFlow);
		this.setFitToWidth(true);
		this.setContent(content);
		this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
//		this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
	}
}
