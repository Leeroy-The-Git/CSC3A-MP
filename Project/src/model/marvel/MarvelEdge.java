package model.marvel;

import com.brunomnsilva.smartgraph.graphview.SmartLabelSource;

public class MarvelEdge {
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
	private int cost;
	private boolean toComic;
	
	public MarvelEdge(int cost, boolean toComic) {
		this.cost = cost;
		this.toComic = toComic;
	}

	/**
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * @param cost the cost to set
	 */
	public void setCost(int cost) {
		this.cost = cost;
	}

	/**
	 * @return the toComic
	 */
	public boolean isToComic() {
		return toComic;
	}

	/**
	 * @param toComic the toComic to set
	 */
	public void setToComic(boolean toComic) {
		this.toComic = toComic;
	}
	
	@SmartLabelSource
    public String getDisplayDistance() {
		if (toComic) {
			return WEIGHT_IN_COMIC[cost - 1];
		} else {
			return WEIGHT_OUT_COMIC[cost - 1];
		}
    }
}
