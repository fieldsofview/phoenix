/**
 * A unique identification generator for every agent in the system.
 * Currently implements a straightforward java UUID generator.
 */

package Agents;

import java.util.UUID;
import System.Log;

//import java.util.concurrent.ConcurrentHashMap;

/**
 * Assigns Unique Agent identifiers
 */

public class AIDGenerator {

	// private static boolean notInitialized = true;
	// private static ConcurrentHashMap<String, Integer> currentAID = new
	// ConcurrentHashMap<String, Integer>();

	public AIDGenerator() {
		// TODO: Write the Log System initialization here.
		// REMOVE THIS METHOD TO BOOT CLASS
		Log.ConfigureLogger();
	}

	/*
	 * TODO: Remove this function as a general Phoenix will not know agent type
	 * till it is initilized. Moreover, the UUID code will ensure that the ID
	 * will be unique throughout the simulation.
	 */
	/**
	 * This function must be called in order to generate unique IDs for all
	 * agents
	 * 
	 * @return true if initialisation possible, false if already initialised
	 */
	/*
	 * public static boolean initializeAIDGen() { if (notInitialized) {
	 * currentAID.put("Agents.Person", Integer.MAX_VALUE);
	 * currentAID.put("Agents.TrafficLight", Integer.MAX_VALUE);
	 * currentAID.put("Agents.Vehicle", Integer.MAX_VALUE);
	 * currentAID.put("Agents.Group", Integer.MAX_VALUE);
	 * currentAID.put("Agents.EmergencyService", Integer.MAX_VALUE);
	 * notInitialized = false; return true; } else { return true; } }
	 */
	/**
	 * 
	 * @param type
	 * @return
	 */
	/*
	 * public static String newID(String type) { Object test = new Object(); if
	 * (!notInitialized) { synchronized (test) { String AID; int num =
	 * currentAID.get(type); AID = type + "." + num--; //
	 * currentAID.remove(type); currentAID.put(type, num);
	 * Log.logger.info("AID Generated : " + AID); return AID; }
	 * 
	 * } else { throw new IllegalAccessError(
	 * "AIDGenerator has not been initialized"); }
	 * 
	 * }
	 */
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

	// Testing
	public static void main(String args[]) {
		int numberOfAgents = 100;
		AIDGenerator agentIDGenerator = new AIDGenerator();
		for (int i = 0; i < numberOfAgents; i++) {
			agentIDGenerator.newID();
		}
	}
}
