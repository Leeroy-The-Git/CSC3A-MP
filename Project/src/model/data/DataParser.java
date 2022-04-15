package model.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataParser {
	private static final String EDGES_PATH = "data/raw/chronology_project_final.csv";
	private static final String COMICS_ONE_PATH = "data/raw/comics/comics1.txt";
	private static final String COMICS_TWO_PATH = "data/raw/comics/comics2.txt";
	private static final String ICONINC_HEROES_DIR = "data/raw/iconic_heroes/";
	
	private static final String FINAL_DIR = "data/final/";
	private static final String NODES_FINAL_NAME = "nodes.txt";
	private static final String EDGES_FINAL_NAME = "edges.txt";
	
	// Regular expressions to determine weights and replacements for clean names
	private static final String[] WEIGHT_REG = { // Used to assign weights to edges
			".+\\(.+\\).*", // Partial Credit in brackets
			".+-OP.*", // Off Panel
			".+-VO.*", // Voice Over
			".+-FB.*", // Flash back
			".+-BTS.*" // Behind The Scenes
			};
	private static final String[] REPLACE_ICONIC_HEROES_REG = { // Used to clean the names of iconic heroes
			"<\\/?b>", // html bold
			"<br>", // html break
			"<\\/?a([\\s\\w=\":\\/.])*>" // html link
			};
	private static final String[] REPLACE_W_EMPTY_REG = {
			"\\s?\\(.+\\)\\s?", // Partial Credit in brackets
			"[{}\\[\\]]", // [, ], { and }
			"amp;", // amp;
			"<br>", // <br>
			"\"", // "
			"-OP", // Off Panel
			"-VO", // Voice Over
			"-FB", // Flash back
			"-BTS", // Behind The Scenes
			"\'", // '
			"@", // @
			"\\s\\|\\scf.*" // Anything after cf
			};
	private static final String STORY_IN_ISSUE_REG = ".*([\\d]+|\\s)\\/\\d+.*"; // 102/3
	private static final String HREF_REG = "<\\/?a([\\s\\w=\":\\/.])*>"; // html link
											
	
	private static ArrayList<String> heroes = new ArrayList<>();
	private static ArrayList<String> comics = new ArrayList<>();
	private static ArrayList<ComicCode> comicCodes = new ArrayList<>();
	private static ArrayList<String> edges = new ArrayList<>();
	
	public static void parseData() throws FileNotFoundException, IOException {
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
			
			Scanner sc = new Scanner(new File(COMICS_ONE_PATH));
			while (sc.hasNext()) {
				String line = sc.nextLine();
				processComic1(line);
			}
			sc.close();

			sc = new Scanner(new File(COMICS_TWO_PATH));
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
			
			Path dir = Paths.get(ICONINC_HEROES_DIR);
		    Files.walk(dir).forEach(path -> {
		    	File file = path.toFile();
		    	if (file.isFile())
			    	try {
			    		Scanner sc1 = new Scanner(file);
						String hero = sc1.nextLine();
						hero = cleanIconicHero(hero);
						while (sc1.hasNext()) {
							String line = sc1.nextLine();
							processEdge(hero, line);
						}
						sc1.close();
			    	} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
		    });
			
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

			sc = new Scanner(new File(EDGES_PATH));
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
			
			PrintWriter pw = new PrintWriter(new File(FINAL_DIR + EDGES_FINAL_NAME));
			for (String e : edges) {
				pw.println(e);
				pw.flush();
			}
			pw.close();
			
			pw = new PrintWriter(new File(FINAL_DIR + "comics.txt"));
			for (ComicCode c : comicCodes) {
				pw.println(c.toString());
				pw.flush();
			}
			pw.close();
			
			System.out.println("DONE");
			
//			System.out.println("=====================================================");
//			System.out.println("HEROES");
//			for (String hero : heroes) {
//				System.out.println(hero);
//			}
//
//			System.out.println("=====================================================");
//			System.out.println("COMICS");
//			for (String comic : comics) {
//				System.out.println(comic);
//			}
			
//			System.out.println("=====================================================");
//			System.out.println("EDGES");
//			for (String edge : edges) {
//				System.out.println(edge);
//			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("File Not found in Data Parser");
		} catch (IOException e) {
			throw new IOException("IO Exception in Data Parser");
		}
	}

	private static String cleanIconicHero(String hero) {
		String clean = hero;
		for (String reg : REPLACE_ICONIC_HEROES_REG) {
			clean = clean.replaceAll(reg, "");
		}
		return clean;
	}

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

	public static void sortData() {
		heroes.sort(null);
		comics.sort(null);
		edges.sort(null);
	}
	
	private static String noHrefs(String line) {
		// Removes link tags from a line
		String res = line;
		res = res.replaceAll("<a\\s?href=\"[\\w.\\-\"#]+\">", "");
		res = res.replaceAll("<\\/a>", "");
		return res;
	}
	
	private static void addEdge(String hero, String comic, int weight) {
		edges.add(hero + "\t" + comic + "\t" + weight);
	}
	
	private static void processEdge(String hero, String comic) {
		comic = comic.replaceAll(HREF_REG, "");
		if (comic.startsWith("From") || comic.startsWith("See") || comic.startsWith("null") || comic.length() == 0)
			return;
		// Processes an edge for a single comic, or calls processEdge for each comic in the relevant string (in the case of data that contains = or ~)
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
			if (!comics.contains(cleanComic))
				comics.add(cleanComic);
			addEdge(hero, cleanComic, detWeight(comic));
		}
	}
	
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
	
	private static int detWeight(String comic) {
		int weight = 0;
		for (int i = WEIGHT_REG.length - 1; i >= 0; i--) {
			if (comic.matches(WEIGHT_REG[i])) {
				weight = i + 1;
			}
		}
		return weight;
	}
}

final class ComicCode implements Comparable<ComicCode> {
	public String code;
	public String name;
	
	public ComicCode(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ComicCode))
			return false;
		ComicCode temp = (ComicCode) obj;
		return code.equals(temp.code);
	}

	@Override
	public int compareTo(ComicCode o) {
		return code.compareTo(o.code);
	}
	
	@Override
	public String toString() {
		return code + "\t" + name;
	}
}
