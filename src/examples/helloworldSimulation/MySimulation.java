/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package examples.helloworldSimulation;

import system.Log;
import agents.AgentController;
import agents.Agent;

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
		//readConfigurations();
		addQueueListener();
		//system.Log.ConfigureLogger();
        //sendReadyForTick();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		MySimulation mySimulation;
		try {
			mySimulation = new MySimulation();
            Log.logger.info("Starting MySimulation");
			mySimulation.runAC();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create the different agents here. One can create a mix of agents if
	 * required.
	 */
	@Override
	protected void setUp() {
		createTestAgents();
    //    sendReadyForTick();
	}

    @Override
    protected void postAgentBehaviour() {
        for(Agent agent : agents.values()){
            agent.setStatusFlag(false);
        }
        return;
    }

    private void createTestAgents() {
		TestAgent temp;
		for (int i = 0; i < NUMBER_OF_AGENTS; i++) {
			temp = new TestAgent(this.getAgentIDGenerator());
			agents.put(temp.getAID(),temp);
			Log.logger.info("Created a Test Agent " + temp.getId() + ".");
		}
		Log.logger.info("Completed creating agents");
	}

    @Override
    protected void cleanupBeforeNextTick() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
