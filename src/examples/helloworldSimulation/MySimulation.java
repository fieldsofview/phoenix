package examples.helloworldSimulation;

import system.Log;
import communication.messages.Message;

import agents.AgentController;

public class MySimulation extends AgentController {

	private static int NUMBER_OF_AGENTS = 10;

	public MySimulation() {
		super();
		// Set the agentControllerName to this Class Name.
		/*
		 * TODO: 1. A optional hash function can be implemented to be even more
		 * specific for the Controller name.
		 * 
		 * TODO: 2. Check if more of this functionality can be moved into the
		 * superclass.
		 */
		this.setAgentControllerName(this.getClass().getCanonicalName());
		// Read the configurations to set up the RabbitMQ communication.
		readConfigurations();
		addQueueListener();
		system.Log.ConfigureLogger();
		buildACStatus();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		MySimulation mySimulation;
		try {
			mySimulation = new MySimulation();
			mySimulation.runAC();
			Log.logger.info("Started MySimulation");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkIfAllAgentsReadyForNextTick() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void activateAgentBehaviour() {
		// TODO Auto-generated method stub
	}

	@Override
	public void processMessage(Message receivedMessage) {
		// TODO Auto-generated method stub

	}

	/**
	 * Create the different agents here. One can create a mix of agents if
	 * required.
	 */
	@Override
	protected void setUp() {
		createTestAgents();
		sendReadyForTick();
	}

	private void createTestAgents() {
		TestAgent temp;
		for (int i = 0; i < NUMBER_OF_AGENTS; i++) {
			temp = new TestAgent(this.getAgentIDGenerator());
			agents.add(temp);
			Log.logger.info("Created a Test Agent " + temp.getId() + ".");
		}
		Log.logger.info("Completed creating agents");
	}
}
