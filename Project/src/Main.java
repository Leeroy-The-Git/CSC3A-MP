import java.io.FileNotFoundException;

import model.data.DataParser;

public class Main {

	public static void main(String[] args) {
		try {
			DataParser.parseData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
