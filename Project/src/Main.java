import java.io.FileNotFoundException;
import java.io.IOException;

import com.jwetherell.algorithms.data_structures.Graph;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.data.DataCleaner;
import model.data.GraphConstructor;
import model.marvel.MarvelNode;
import model.ui.AboutPane;
import model.ui.MarvelPane;

/**
 * Main class
 * @author JARED SWANZEN (220134523)
 */
public class Main extends Application {
	private static final int WIDTH = 1024;
	private static final int HEIGHT = 768;
	
	private MarvelPane marvelPane;
	private Scene sixDegreesScene;
	private Scene aboutScene;
	private Scene loadingScene;
	private Stage stage;
	private Label lblLoading;
	
	@Override
	/**
	 * { @inheritDoc }
	 */
	public void start(Stage primaryStage) throws Exception {
		stage = new Stage(StageStyle.DECORATED);
		
		marvelPane = new MarvelPane();
		BorderPane sixDegreesRoot = new BorderPane();
		MenuBar sixDegreesMenu = initMenus();		
		
		sixDegreesRoot.setTop(sixDegreesMenu);
		sixDegreesRoot.setCenter(marvelPane);
		
		sixDegreesScene = new Scene(sixDegreesRoot, WIDTH, HEIGHT);
		
		AboutPane aboutPane = new AboutPane();
		BorderPane aboutRoot = new BorderPane();
		MenuBar aboutMenu = initMenus();		

		aboutRoot.setTop(aboutMenu);
		aboutRoot.setCenter(aboutPane);
		
		aboutScene= new Scene(aboutRoot, WIDTH, HEIGHT);
		
		
		ProgressBar pb = new ProgressBar();
		lblLoading = new Label();
		VBox loadingRoot = new VBox(15, lblLoading, pb);
		loadingRoot.setAlignment(Pos.CENTER);
		
		loadingScene = new Scene(loadingRoot, WIDTH, HEIGHT);
		
		stage.setTitle("The Six Degrees Of Marvel Comics");
		stage.setScene(sixDegreesScene);
		stage.show();

		marvelPane.getGraphView().init();
	}
	

	/**
	 * Application entrypoint
	 */
	public static void main (String[] args) {
		launch(args);
	}
	
	/**
	 * Method to parse data using {@link DataCleaner}
	 */
	private void parseData() {
		System.out.println("==============================================");
		System.out.println("Cleaning Data...");
		try {
			DataCleaner.cleanData();
			System.out.println("Data Parsed");
			System.out.println("");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to parse graph using {@link GraphConstructor} 
	 * @return processed graph
	 */
	private Graph<MarvelNode> parseGraph() {
		System.out.println("==============================================");
		System.out.println("Processing Graph...");
		String path = DataCleaner.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		path = path.substring(0, path.lastIndexOf('/') + 1);
		GraphConstructor gp = new GraphConstructor(path + "edges.txt");
		Graph<MarvelNode> graph = null;
		try {
			graph = gp.constructGraph();
			System.out.println("Graph Processed");
			System.out.println("");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return graph;
	}
	
	/**
	 * Method to create and return a {@link MenuBar} 
	 * @return created MenuBar
	 */
	private MenuBar initMenus() {
		// Setup file menu items
		MenuItem cleanDataItem = new MenuItem("Clean Data");
		cleanDataItem.setOnAction(e -> {
			Task<Void> task = new Task<>() {

				@Override
				protected Void call() throws Exception {
					parseData();
					return null;
				}
				
			};
			task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				
				@Override
				public void handle(WorkerStateEvent event) {
					stage.setScene(sixDegreesScene);
				}
				
			});
			Thread dataThread = new Thread(task);
			setLoading("Please wait while the data is being cleaned");
			stage.setScene(loadingScene);
			dataThread.start();
		});
		MenuItem constructGraphItem = new MenuItem("Construct Graph");
		constructGraphItem.setOnAction(e -> {
			Task<Void> task = new Task<>() {

				@Override
				protected Void call() throws Exception {
					Graph<MarvelNode> graph = parseGraph();
					marvelPane.setGraph(graph);
					return null;
				}
			
			};
			task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent event) {
					stage.setScene(sixDegreesScene);
				}
				
			});
			
			Thread graphThread = new Thread(task);
			setLoading("Please wait while the graph is being constructed (This can take up to 2 minutes)");
			stage.setScene(loadingScene);
			graphThread.start();
		});
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(e -> {
			Platform.exit();
		});

		// Setup file menu
		Menu fileMenu = new Menu("File");
		fileMenu.getItems().setAll(cleanDataItem, constructGraphItem, exitItem);

		// Setup window menu items
		MenuItem sixDegreesItem = new MenuItem("Six Degrees Of Marvel Comics");
		sixDegreesItem.setOnAction(e -> {
			stage.setScene(sixDegreesScene);
		});		
		
		MenuItem aboutItem = new MenuItem("About System");
		aboutItem.setOnAction(e -> {
			stage.setScene(aboutScene);
		});
		
		// Setup window menu
		Menu windowMenu = new Menu("Windows");
		windowMenu.getItems().setAll(sixDegreesItem, aboutItem);
		
		// Create and return menu bar
		return new MenuBar(fileMenu, windowMenu);
	}

	
	/**
	 * Set message for loading screen
	 * @param message message to set
	 */
	private void setLoading(String message) {
		lblLoading.setText(message);
	}
}
