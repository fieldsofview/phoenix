/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package examples.helloworldSimulation;

import system.Log;
import agents.AIDGenerator;
import agents.Agent;
import agents.behaviour.CompositeBehaviour;

public class TestAgent extends Agent {

	/*
	 * Declare different agent attributes and behaviour here.
	 */

	/**
	 * Default constructor called with an unique ID to identify it.
	 * 
	 * @param agentIDGenerator
	 */
	public TestAgent(AIDGenerator agentIDGenerator) {
		super(agentIDGenerator);
		Log.ConfigureLogger();
		Log.logger.info("Test Agent Created");

		/*
		 * Create a list of behaviours that this agent can perform. Each of this
		 * behaviour is executed as an independent thread.
		 */
		behaviour = new CompositeBehaviour();
		behaviour.add(new TestAgentBehaviour());
	}

	/*
	 * The entire TestAgent itself runs as a thread.
	 */
	public void run() {
		behaviour.run(null);
		this.setStatusFlag(true);
		this.setObjectiveFlag(true);
	}

}
