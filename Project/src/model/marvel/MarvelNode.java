package model.marvel;

public class MarvelNode implements Comparable<MarvelNode>{
	private String name;
	private NODE_TYPE type;
	
	public MarvelNode(NODE_TYPE type, String name) {
		this.name = name;
		this.type = type;
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MarvelNode))
			return false;
		MarvelNode m = (MarvelNode) o;
		return type.equals(m.getType()) && name.equals(m.getName());
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public int compareTo(MarvelNode o) {
		return name.compareTo(o.getName());
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public int hashCode() {
		final int code = name.length() + type.hashCode(); 
		return 29 * code;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public NODE_TYPE getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(NODE_TYPE type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type.name() + ": " + name;
	}
	
}
