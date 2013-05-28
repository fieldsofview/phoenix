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
