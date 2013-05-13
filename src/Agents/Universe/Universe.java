package Agents.Universe;

/**
 * The different types of spaces that the agents can reside within. This is
 * not essential to run an agent but interactions can be defined through 
 * the universe.
 */
public class Universe {
    private String universeName;

	/**
	 * @return the universeName
	 */
	public String getUniverseName() {
		return universeName;
	}

	/**
	 * @param universeName the universeName to set
	 */
	public void setUniverseName(String universeName) {
		this.universeName = universeName;
	}
    
    
}
