package model.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class dedicated to the parsing of data
 * @author JARED SWANZEN (220134523)
 *
 */
public class DataCleaner {
	// File paths for raw data
	private static final String EDGES_PATH = "/raw/chronology_project_final.csv";
	private static final String COMICS_ONE_PATH = "/raw/comics/comics1.txt";
	private static final String COMICS_TWO_PATH = "/raw/comics/comics2.txt";
	private static final String ICONINC_HEROES_DIR = "/raw/iconic_heroes/";
	private static final String[] ICONIC_HEROES_PATHS = {
			"cap.txt",
			"hulk.txt",
			"iron_man.txt",
			"mr_fantastic.txt",
			"scarlet.txt",
			"spider.txt",
			"storm.txt",
			"sub-mariner.txt"
			};
	
	// File paths for output data
	private static final String FINAL_DIR = "data/final/";
	private static final String HEROES_FINAL_NAME = "heroes.txt";
	private static final String COMICS_FINAL_NAME = "comics.txt";
	private static final String EDGES_FINAL_NAME = "edges.txt";
	
	// Regular expressions to determine weights
	private static final String[] WEIGHT_REG = {
			".+\\(.+\\).*", // Partial Credit in brackets
			".+-OP.*", // Off Panel
			".+-VO.*", // Voice Over
			".+-FB.*", // Flash back
			".+-BTS.*" // Behind The Scenes
			};
	
	// Used to clean the names of iconic heroes
	private static final String[] REPLACE_ICONIC_HEROES_REG = { 
			"<\\/?b>", // html bold
			"<br>?", // html break
			"<\\/?a([\\s\\w=\":\\/.])*>" // html link
			};
	
	// Used to clean comic names
	private static final String[] REPLACE_W_EMPTY_REG = {
			"\\s?\\(.+\\)\\s?", // Partial Credit in brackets
			"[{}\\[\\]]", // [, ], { and }
			"amp;", // amp;
			"<?br>?", // <br>
			"<?vr>?", // <vr>
			"\"", // "
			"-OP", // Off Panel
			"-VO", // Voice Over
			"-FB", // Flash back
			"-BTS", // Behind The Scenes
			"\'", // '
			"@", // @
			"[<>]", // < and >
			"\\s?\\|\\s?cf.*" // Anything after cf
			};
	
	// Used to replace 102/3 with 102 STORY 3
	private static final String STORY_IN_ISSUE_REG = ".*([\\d]+|\\s)\\/\\d+.*"; // 102/3
											
	// Lists to store heroes, comics and vertices
	private static List<String> heroes = new ArrayList<>();
	private static List<String> comics = new ArrayList<>();
	private static List<ComicCode> comicCodes = new ArrayList<>();
	private static List<String> edges = new ArrayList<>();
	
	/**
	 * Method to clean comics, heroes and edges and output them to files
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void cleanData() throws FileNotFoundException, IOException {
		try { 
		     
			/* =========================================================================================================================
			 * PARSING COMICS
			 * Example Input (comics1.txt):
			 * <tr><td>CM:FYM</td><td>CAPTAIN MARVEL: FREE YOUR MIND</td><td>1994</td></tr>
			 * 
			 * Example Input (comics2.txt):
			 * <tr><td>A SHADOWLINE SAGA: CRITICAL MASS</td><td>CMASS</td></tr>
			 * 
			 * Output:
			 * Code: CM:FYM
			 * Name: CAPTAIN MARVEL: FREE YOUR MIND
			 * 
			 * Code: CMASS
			 * Name: A SHADOWLINE SAGA: CRITICAL MASS
			 */
			
		    System.out.println("Processing Comics...");
			
			Scanner sc = new Scanner(DataCleaner.class.getResourceAsStream(COMICS_ONE_PATH));
			while (sc.hasNext()) {
				String line = sc.nextLine();
				processComic1(line);
			}
			sc.close();

			sc = new Scanner(DataCleaner.class.getResourceAsStream(COMICS_TWO_PATH));
			while (sc.hasNext()) {
				String line = sc.nextLine();
				processComic2(line);
			}
			sc.close();
			
			comicCodes.sort(null);
			
			/* =========================================================================================================================
			 * PARSING ICONIC HEROES
			 * Example Input:
			 * <b><a href="http://www.medinnus.com/winghead/" name="AMERICA">CAPTAIN AMERICA</a>/STEVE ROGERS</b><br>
			 * CA7 1-FB<br>
			 * CA7 2-FB<br>
			 * 
			 * Output:
			 * Hero: CAPTAIN AMERICA/STEVE ROGERS
			 * Comic: CA7 1-FB
			 * Comic: CA7 2-FB
			 */
			System.out.println("Processing Iconic Heroes...");
			
			for (String icon : ICONIC_HEROES_PATHS) {
	    		sc = new Scanner(DataCleaner.class.getResourceAsStream(ICONINC_HEROES_DIR + icon));
				String hero = sc.nextLine();
				hero = cleanIconicHero(hero);
				while (sc.hasNext()) {
					String line = sc.nextLine();
					processEdge(hero, line);
				}
				sc.close();
			 }
			
			/* =========================================================================================================================
			 * PARSING EDGES
			 * 
			 * Example Input:
			 * "1646764886-44945","https://www.chronologyproject.com/","B","https://www.chronologyproject.com/b.php","BULL","TOS 59/2<br>
			 * PM&amp;IF 55<br>"
			 * 
			 * Output for given Input:
			 * Hero: BULL
			 * Edge: BULL\tTOS 59
			 * Edge: BULL\tPM&IF 55
			 */
			
			System.out.println("Processing Edges...");

			sc = new Scanner(DataCleaner.class.getResourceAsStream(EDGES_PATH));
			sc.nextLine(); // Remove headers of .csv file
			String currentHero = "";
			while (sc.hasNext()) {
				String line = sc.nextLine();
				if (!line.isEmpty()) {
					line = noHrefs(line);
					if (line.matches("\"\\d+\\-\\d+\".+")) {
						// Extract hero name and first comic from line
						String[] res = line.split("\",\"");						
						String temp = res[4];
						currentHero = processHero(temp);
						String comic = res[5];						
						processEdge(currentHero, comic);
					} else {
						processEdge(currentHero, line);
					}
				}
			}
			sc.close();
			
			sortData();
			
			/* =========================================================================================================================
			 * OUTPUT TO FILES
			 */
			System.out.println("Outputting to files...");
			
			System.out.println("Outputting to edge file...");
			PrintWriter pw = new PrintWriter(new File(FINAL_DIR + EDGES_FINAL_NAME));
			for (String e : edges) {
				pw.println(e);
				pw.flush();
			}
			pw.close();
			
			System.out.println("Outputting to comic file...");
			pw = new PrintWriter(new File(FINAL_DIR + COMICS_FINAL_NAME));
			for (String c : comics) {
				pw.println(c.toString());
				pw.flush();
			}
			pw.close();
			
			System.out.println("Outputting to hero file...");
			pw = new PrintWriter(new File(FINAL_DIR + HEROES_FINAL_NAME));
			for (String h : heroes) {
				pw.println(h.toString());
				pw.flush();
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FileNotFoundException("File Not found in Data Parser");
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("IO Exception in Data Parser");
		}
	}
	
//	public static void parseSmallData() throws FileNotFoundException, IOException {
//		try { 
//		     
//			/* =========================================================================================================================
//			 * PARSING COMICS
//			 * Example Input (comics1.txt):
//			 * <tr><td>CM:FYM</td><td>CAPTAIN MARVEL: FREE YOUR MIND</td><td>1994</td></tr>
//			 * 
//			 * Example Input (comics2.txt):
//			 * <tr><td>A SHADOWLINE SAGA: CRITICAL MASS</td><td>CMASS</td></tr>
//			 * 
//			 * Output:
//			 * Code: CM:FYM
//			 * Name: CAPTAIN MARVEL: FREE YOUR MIND
//			 * 
//			 * Code: CMASS
//			 * Name: A SHADOWLINE SAGA: CRITICAL MASS
//			 */
//			
//		    System.out.println("Processing Comics...");
//			
//			Scanner sc = new Scanner(new File(COMICS_ONE_PATH));
//			while (sc.hasNext()) {
//				String line = sc.nextLine();
//				processComic1(line);
//			}
//			sc.close();
//
//			sc = new Scanner(new File(COMICS_TWO_PATH));
//			while (sc.hasNext()) {
//				String line = sc.nextLine();
//				processComic2(line);
//			}
//			sc.close();
//			
//			comicCodes.sort(null);
//			
//			/* =========================================================================================================================
//			 * PARSING ICONIC HEROES
//			 * Example Input:
//			 * <b><a href="http://www.medinnus.com/winghead/" name="AMERICA">CAPTAIN AMERICA</a>/STEVE ROGERS</b><br>
//			 * CA7 1-FB<br>
//			 * CA7 2-FB<br>
//			 * 
//			 * Output:
//			 * Hero: CAPTAIN AMERICA/STEVE ROGERS
//			 * Comic: CA7 1-FB
//			 * Comic: CA7 2-FB
//			 */
//			System.out.println("Processing Single Iconic Hero...");
////			
////			Path dir = Paths.get(ICONINC_HEROES_DIR);
////		    Files.walk(dir).forEach(path -> {
////		    	File file = path.toFile();
////		    	if (file.isFile())
////		    });
//		    try {
//		    	Scanner sc1 = new Scanner(new File(ICONINC_HEROES_DIR + CAPTAIN_AMERICA_PATH));
//		    	String hero = sc1.nextLine();
//		    	hero = cleanIconicHero(hero);
//		    	while (sc1.hasNext()) {
//		    		String line = sc1.nextLine();
//		    		processEdge(hero, line);
//		    	}
//		    	sc1.close();
//		    } catch (FileNotFoundException e1) {
//		    	e1.printStackTrace();
//		    }
//			
//			/* =========================================================================================================================
//			 * PARSING EDGES
//			 * 
//			 * Example Input:
//			 * "1646764886-44945","https://www.chronologyproject.com/","B","https://www.chronologyproject.com/b.php","BULL","TOS 59/2<br>
//			 * PM&amp;IF 55<br>"
//			 * 
//			 * Output for given Input:
//			 * Hero: BULL
//			 * Edge: BULL\tTOS 59
//			 * Edge: BULL\tPM&IF 55
//			 */
//			
//			System.out.println("Processing Edges...");
//
//			sc = new Scanner(new File(EDGES_PATH));
//			sc.nextLine(); // Remove headers of .csv file
//			String currentHero = "";
//			Random rand = new Random(System.currentTimeMillis());
//			while (sc.hasNext()) {
//				String line = sc.nextLine();
//				if (!line.isEmpty()) {
//					// Randomly select 20% of the dataset
//					line = noHrefs(line);
//					if (line.matches("\"\\d+\\-\\d+\".+")) {
//						// Extract hero name and first comic from line
//						String[] res = line.split("\",\"");						
//						String temp = res[4];
//						currentHero = processHero(temp);
//						String comic = res[5];						
//						processEdge(currentHero, comic);
//					} else {
//						if (rand.nextInt() % 2 > 0) {
//							processEdge(currentHero, line);
//						}
//					}
//				}
//			}
//			sc.close();
//			
//			sortData();
//			
//			/* =========================================================================================================================
//			 * OUTPUT TO FILES
//			 */
//			System.out.println("Outputting to files...");
//			
//			System.out.println("Outputting to edge file...");
//			PrintWriter pw = new PrintWriter(new File(FINAL_DIR + SMALL_PREFIX + EDGES_FINAL_NAME));
//			for (String e : edges) {
//				pw.println(e);
//				pw.flush();
//			}
//			pw.close();
//			
//			System.out.println("Outputting to comic file...");
//			pw = new PrintWriter(new File(FINAL_DIR + SMALL_PREFIX + COMICS_FINAL_NAME));
//			for (String c : comics) {
//				pw.println(c.toString());
//				pw.flush();
//			}
//			pw.close();
//			
//			System.out.println("Outputting to hero file...");
//			pw = new PrintWriter(new File(FINAL_DIR + SMALL_PREFIX + HEROES_FINAL_NAME));
//			for (String h : heroes) {
//				pw.println(h.toString());
//				pw.flush();
//			}
//			pw.close();
//		} catch (FileNotFoundException e) {
//			throw new FileNotFoundException("File Not found in Data Parser");
//		}
//	}

	/**
	 * Cleans the name of an iconic hero
	 * @param hero name to clean
	 * @return cleaned name
	 */
	private static String cleanIconicHero(String hero) {
		String clean = hero;
		for (String reg : REPLACE_ICONIC_HEROES_REG) {
			clean = clean.replaceAll(reg, "");
		}
		if (!heroes.contains(clean)) {
			heroes.add(clean);
		}
		return clean;
	}

	/**
	 * Process the comic from the first comic file
	 * @param line line to process
	 */
	private static void processComic1(String line) {
		// <tr><td>CAD4</td><td>CLOAK AND DAGGER VOL. 4</td><td>2010</td></tr>
		if (!line.isEmpty()) {
			line = line.replaceAll("<tr>", "");
			line = line.replaceAll("<td>", "");
			line = line.replaceAll("amp;", "");
			String[] items = line.split("</td>");
			ComicCode newCode = new ComicCode(items[0], items[1]);
			if (!(comicCodes.contains(newCode)))
				comicCodes.add(newCode);
		}
	}
	
	/**
	 * Process the comic from the second comic file
	 * @param line line to process
	 */
	private static void processComic2(String line) {
		// <tr><td>A YEAR OF MARVELS: UNSTOPPABLE</td><td>AYOM/:UNS</td></tr>
		if (!line.isEmpty()) {
			line = line.replaceAll("<tr>", "");
			line = line.replaceAll("<td>", "");
			line = line.replaceAll("amp;", "");
			String[] items = line.split("</td>");
			ComicCode newCode = new ComicCode(items[1], items[0]);
			if (!(comicCodes.contains(newCode)))
				comicCodes.add(newCode);
		}
	}
	
	/**
	 * Process a hero found in the edges.csv file
	 * @param hero hero to process
	 * @return processed hero
	 */
	private static String processHero(String hero) {
		String cleanName = "";
		// Change MURPHY, JOHN into JOHN MURPHY
		String[] names = hero.split("/");
		for (int i = 0; i < names.length; i++) {
			String[] nameAndSurname = names[i].split(",");
			if (nameAndSurname.length > 1) {
				cleanName += nameAndSurname[1].trim() + " " + nameAndSurname[0].trim();
			} else {
				cleanName += names[i];
			}
			// Add / back into name
			if (i != names.length - 1)
				cleanName += "/";
		}
		cleanName = cleanName.replaceAll("\"\"", "\"");
		if (!heroes.contains(cleanName)) {
			heroes.add(cleanName);
		}
		return cleanName;
	}

	/**
	 * Sort data using the {@link ArrayList} sort method
	 */
	public static void sortData() {
		heroes.sort(null);
		comics.sort(null);
		edges.sort(null);
	}
	
	/**
	 * Remove html links from a line
	 * @param line line to remove links from
	 * @return line without links
	 */
	private static String noHrefs(String line) {
		// Removes link tags from a line
		String res = line;
		res = res.replaceAll("<a\\s?href=\"[\\w.\\-\"#]+\">", "");
		res = res.replaceAll("<\\/a>", "");
		return res;
	}
	
	/**
	 * Add a new edge to the edge list
	 * @param hero hero to connect from
	 * @param comic comic to connect to
	 * @param weight weight or cost of the edge
	 */
	private static void addEdge(String hero, String comic, int weight) {
		edges.add(hero + "\t" + comic + "\t" + weight);
	}
	
	/**
	 * Cleans hero and comic before adding them to an edge
	 * @param hero hero to connect from
	 * @param comic comic to connect to
	 */
	private static void processEdge(String hero, String comic) {
		comic = comic.trim();
		if (comic.startsWith("From") || comic.startsWith("See") || comic.startsWith("null") || comic.startsWith("Note") || comic.length() == 0)
			return;
		// Processes an edge for a single comic, or calls processEdge for each comic in the relevant string (in the case of data that contains = or ~ for two comic appearances)
		if (comic.contains("=")) {
			String[] comics = comic.split("\\s?=\\s?");
			for (String c : comics) {
				processEdge(hero, c);
			}
		} else if (comic.contains("~")) {
			String[] comics = comic.split("\\s?~\\s?");
			for (String c : comics) {
				processEdge(hero, c);
			}
		} else {
			String cleanComic = cleanComic(comic);
			if (cleanComic.matches(".*[a-z]+.*"))
				return;
			if (!comics.contains(cleanComic))
				comics.add(cleanComic);
			addEdge(hero, cleanComic, detWeight(comic));
		}
	}
	
	/**
	 * Cleans the name of a comic
	 * @param comic name to clean
	 * @return cleaned name
	 */
	private static String cleanComic(String comic) {
		String clean = comic;
		for (String reg : REPLACE_W_EMPTY_REG) {
			clean = clean.replaceAll(reg, "");
		}
		for (int i = (comicCodes.size() - 1); i > 0; i--) {
			if (clean.startsWith(comicCodes.get(i).code)) {
				clean = clean.replace(comicCodes.get(i).code, comicCodes.get(i).name);
				break;
			}
		}
		if (clean.matches(STORY_IN_ISSUE_REG)) {
			clean = clean.substring(0, clean.lastIndexOf("/")) + " STORY " + clean.substring(clean.lastIndexOf("/") + 1, clean.length());
		}
		
		return clean;
	}
	
	/**
	 * Determines the weight of an edge based on the type of appearance the hero had in a comic
	 * @param comic comic data
	 * @return weight of appearance in comic
	 * 
	 */
	private static int detWeight(String comic) {
		int weight = 0;
		for (int i = WEIGHT_REG.length - 1; i >= 0; i--) {
			if (comic.matches(WEIGHT_REG[i])) {
				weight = i + 1;
			}
		}
		return weight + 1;
	}
}

/**
 * ComicCode to link a comic's code to its a name
 * @author JARED SWANZEN (220134523)
 *
 */
final class ComicCode implements Comparable<ComicCode> {
	public String code;
	public String name;
	
	/**
	 * constructor
	 * @param code comic code
	 * @param name comic name
	 */
	public ComicCode(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ComicCode))
			return false;
		ComicCode temp = (ComicCode) obj;
		return code.equals(temp.code);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(ComicCode o) {
		return code.compareTo(o.code);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return code + "\t" + name;
	}
}
