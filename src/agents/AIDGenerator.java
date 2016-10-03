/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package agents;

import java.util.UUID;

import system.Log;

/**
 * A unique identification generator for every agent in the system. Currently
 * implements a straightforward java UUID generator. Assigns Unique Agent
 * identifiers using the built-in java UUID generator.
 */
public class AIDGenerator {

	public AIDGenerator() {
		// TODO: Remove the next piece of code. The ConfigureLogger needs to be called once before in the AC
		//Log.ConfigureLogger();
	}

	/**
	 * This function uses the Java's UUID random generator to generate a unique
	 * identifier for each agent.
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
