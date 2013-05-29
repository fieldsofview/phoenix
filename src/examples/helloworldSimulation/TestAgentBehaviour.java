package examples.helloworldSimulation;

import agents.attributes.AgentAttributes;
import agents.behaviour.Behaviour;

public class TestAgentBehaviour implements Behaviour {

	@Override
	public void run(AgentAttributes agentAttributes) {
		System.out.println("TestAgentBehaviour");
	}

}
