/**
 * A unique identification generator for every agent in the system.
 * Currently implements a straightforward java UUID generator.
 */

package agents;

import java.util.UUID;

import system.Log;

/**
 * Assigns Unique Agent identifiers using the built-in java UUID generator.
 */

public class AIDGenerator {

	public AIDGenerator() {
		// TODO: Write the Log system initialization here.
		Log.ConfigureLogger();
	}

	/**
	 * This function uses the Java's UUID random generator to generate a unique
	 * identifier for each agent.
	 * 
	 * @param agentType
	 *            The type or name of agent. This is set per simulation.
	 * @return the UUID object containing the unique identifier
	 */
	public UUID newID() {
		Object test = new Object();
		synchronized (test) {
			UUID AID;
			AID = UUID.randomUUID();
			Log.logger.info("AID Generated : " + AID);
			return AID;
		}
	}

	// Testing TODO: Replace this with JUnit
	public static void main(String args[]) {
		int numberOfAgents = 100;
		AIDGenerator agentIDGenerator = new AIDGenerator();
		for (int i = 0; i < numberOfAgents; i++) {
			agentIDGenerator.newID();
		}
	}
}
