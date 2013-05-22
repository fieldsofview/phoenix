package examples.helloworldSimulation;

import system.Log;
import agents.AIDGenerator;
import agents.Agent;

public class TestAgent extends Agent {

	public TestAgent(AIDGenerator agentIDGenerator) {
		super(agentIDGenerator);
		Log.ConfigureLogger();
		Log.logger.info("Test Agent Created");
	}

}
