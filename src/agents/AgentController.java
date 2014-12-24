/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */
package agents;

import communication.ACNetwork;
import communication.QueueParameters;
import communication.messages.ACStatusMessage;
import communication.queueManager.ACQueueManagement;
import system.Boot;
import system.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * The Default Agent Controller. All types of Agent Controllers extend this
 * class. The properties and methods of this class is shared among all Agent
 * controllers within the simulation. This class is responsible for
 * synchronisation communication and progress of the simulation. This is first
 * class that establishes contact and proceeds with the simulation.
 */
public abstract class AgentController {

	/**
	 * The current name of the agent controller which has to be set. This name
	 * will be used for routing messages to this agent controller. This name is originally
	 * set in ACNetwork class during the boot process when it is read from machine config.
	 */
	private String agentControllerName;
	/**
	 * The current TICK number. TICK number indicates the steps of simulation
	 * and is used for synchronisation of various CTAs.
	 */
	protected static int currentTickNumber;
	/**
	 * A Map of all agents handled by the Agent Controller.
	 * This has been changed to a Map from a List.
	 */
	public Map<UUID, Agent> agents;
	/**
	 * The AgentController's communication queue.
	 */
	public ACQueueManagement queueManager;
	/**
	 * The current status of the other AgentControllers that are involved in the
	 * simulation.
	 */
	public static Map<String, Integer> ACStatus;
	/**
	 * This generator is initialised by the implementing AgentController. This
	 * instance will be used by the AgentController to generate all the agents
	 * under it.
	 */
	private AIDGenerator agentIDGenerator;

	/*
	 * TODO: Modify this to include default values and initial settings for a
	 * general AgentController
	 */
	// Default Constructor
	public AgentController() {
		this.agentIDGenerator = new AIDGenerator();
		//system.Log.ConfigureLogger();
		createListObjects();
		currentTickNumber = 0;
		agentControllerName = ACNetwork.ACName;
		readConfigurations(); // NOTE: Call this function before buildACStatus
		buildACStatus();
	}

	/**
	 * This function is called to create instances for the list of agents
	 * variable and the current status list for all the AgentControllers.
	 */
	private void createListObjects() {
		ACStatus = Collections.synchronizedMap(new HashMap<String, Integer>());
		agents = Collections.synchronizedMap(new HashMap<UUID, Agent>());
	}

	/**
	 * This is the first method called by a new AgentController. By default it
	 * does a agent setup-up which creates various agents as defined in the
	 * class. But it can be overridden to perform other tasks.
	 */
	protected void runAC() {
		setUp();
		sendReadyForTick();

		// Runs until objectives for all agents is fulfilled
		while (!objectiveSatisfiedForAllAgents()) {

			// Time out for waiting for other agents
			long timeBeforeWaiting = System.currentTimeMillis();

			while (!checkIfAllACsReadyForNextTick()) {
				try {
					long timeNow = System.currentTimeMillis();
					if (timeNow - timeBeforeWaiting >= ACNetwork.MAXIMUM_TIME_OUT_FOR_AC) {
						updateTimeOutList();
						Log.logger.info("Continuing with run after timeout");
						break;
					} else {
						// wait until all ACs are ready
						Thread.sleep(500);
					}

				} catch (InterruptedException ex) {
					Log.logger.error(ex.getMessage());
				}
			}
			changeReadyACsToComputing();
			/* INCREMENT TICK NUMBER */
			currentTickNumber++;
			Log.logger.info("TICK NUMBER: " + getCurrentTickNumber());
			preAgentBehaviour();
			activateAgentBehaviour();
			postAgentBehaviour();
			cleanUpAC(); //Onkar was right
            /*
             * Call the deprecated method for tick-wise outputs
             */
			cleanupBeforeNextTick();
		}
		Log.logger.info("All agents have achieved their objectives");
		shutdown();
	}

	/**
	 * Checks and returns if all agents have satisfied their objectives and
	 * hence the simulation can cease to run.
	 *
	 * @return true if all agents' objectives are satisfied and the simulation is ready to end.
	 */
	protected boolean objectiveSatisfiedForAllAgents() {
		int count = 0;
		for (Agent p : agents.values()) {
			if (p.getObjectiveFlag()) {
				count++;
			}
		}

		if (count == agents.size()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Perform clean-up operations after every run of an AC before the next tick is called.
	 * This method is for a given AC and does not do simulation specific clean-up.
	 * CAUTION: This method can potentially slow down the application if not implemented to
	 * be run fast and parallel.
	 * Onkar was right!
	 * Refer cleanupBeforeNextTick() for user defined method.
	 */
	protected void cleanUpAC() {
		changeACStatus(ACNetwork.localhost, ACNetwork.AC_READY_FOR_NEXT_TICK);
		sendReadyForTick();
		Log.logger.info("Sent Ready for next TICK");

		for (String host : ACStatus.keySet()) {
			Log.logger.debug(host + " : " + ACStatus.get(host));
		}
	}

	/**
	 * This is a method that can be used to perform shutdown operation. By default it exits the
	 * simulation.
	 */
	protected void shutdown() {
		sendDoneWithWork();
		System.exit(0);
	}

	/**
	 * Create the different agents here. One can create a mix of agents if
	 * required.
	 */
	protected abstract void setUp();

	/**
	 * Returns the Agent Controllers current instance of the ID Generator.
	 *
	 * @return the agent ID generator for the AgentController.
	 */
	public AIDGenerator getAgentIDGenerator() {
		return this.agentIDGenerator;
	}

	/*
	 * Current simulation step the AgentController is in.
	 */
	public int getCurrentTickNumber() {
		return currentTickNumber;
	}

	/**
	 * Function to be overridden by each AC if required. This function should
	 * contain the logic for checking if every agent has completed its task for
	 * the current tick. By default this is a basic implementation where a status flag defined
	 * in every agent is set to indicate the run of every agent.
	 *
	 * @return true if all agents in this AC have completed their operations for this TICK.
	 */
	public boolean checkIfAllAgentsReadyForNextTick() {
		int count = 0;
		for (Agent p : agents.values()) {
			if (p.getStatusFlag()) {
				count++;
			}
		}
		if (count == agents.size()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Call the individual agents behaviour from its list of behaviour
	 */
	public void activateAgentBehaviour() {
        /*
         * run every agent's behaviour in a chosen order. A scheduling policy
         * can be implemented by the user here.
         */
		for (Agent p : agents.values()) {
			if (!p.getObjectiveFlag()) {
				p.start();
			}
		}
	}

	/**
	 * This is a method is called after one run of all the agent finishing a run and before
	 * the AC checks for agent objectives. It is set to a empty function and overridden if required.
	 */
	protected void postAgentBehaviour() {

	}

	/**
	 * This an abstract method is called before the run of all the agents, before
	 * the AC calls activate AgentBehaviour(). It is set to a empty function and overridden if required.
	 */
	protected void preAgentBehaviour() {

	}

	/**
	 * Check if all the AgentControllers are ready for the next step of the
	 * simulation.
	 *
	 * @return
	 */
	public boolean checkIfAllACsReadyForNextTick() {
		if (ACStatus.size() == 1) {
			Log.logger.info("I am the only host");
			return true;
		}

		if (ACStatus.containsValue(ACNetwork.AC_COMPUTING)) {
			Log.logger.info("Some hosts are busy");
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Create the communication channel for the communication
	 */
	public void addQueueListener() {
		// QueueParameters queueParameters = ACNetwork.hostMessageQueueLookup
		// .get(Constants.localHost);
		// TODO: There is a single queue for ACs and those parameters are being
		// used.
		QueueParameters queueParameters = ACNetwork.ACMessageQueueParameters;
		queueManager = ACQueueManagement.getInstance(queueParameters);
		queueManager.start();

		// processMessage = new ACProcessMessage(agents);
		// processMessage.start();
		// processMessage.agents = agents;
	}

	/**
	 * Broadcast a message to all the AgentControllers that the current
	 * AgentController is ready to proceed to the next step of the simulation.
	 */
	public void sendReadyForTick() {

		ACStatusMessage statusMessage = new ACStatusMessage();
		statusMessage.AC_STATUS = ACNetwork.AC_READY_FOR_NEXT_TICK;
		statusMessage.messageObject = ACNetwork.localhost;
		statusMessage.hostName = ACNetwork.localhost;

		queueManager.send(null, statusMessage);
       /* for (String host : ACStatus.keySet()) {
            Integer status = ACStatus.get(host);
            if (status == ACNetwork.AC_COMPUTING
                    || status == ACNetwork.AC_READY_FOR_NEXT_TICK) {

                ACStatusMessage statusMessage = new ACStatusMessage();
                statusMessage.AC_STATUS = ACNetwork.AC_READY_FOR_NEXT_TICK;
                statusMessage.messageObject = ACNetwork.localhost;
                statusMessage.hostName = ACNetwork.localhost;

                queueManager.send(host, statusMessage);
            }
        }
        */
	}

	/**
	 * Broadcast a message that the current Agent COntroller has completed
	 * running the behaviour of all its agents once and is ready to proceede
	 * with the simulation.
	 */
	public void sendDoneWithWork() {

		ACStatusMessage statusMessage = new ACStatusMessage();
		statusMessage.AC_STATUS = ACNetwork.AC_DONE_WITH_WORK;
		statusMessage.hostName = ACNetwork.localhost;
		statusMessage.messageObject = ACNetwork.localhost;
		queueManager.send(null, statusMessage);

        /*for (String host : ACStatus.keySet()) {
            if (ACStatus.get(host) == ACNetwork.AC_COMPUTING
                    || ACStatus.get(host) == ACNetwork.AC_READY_FOR_NEXT_TICK) {

                ACStatusMessage statusMessage = new ACStatusMessage();
                statusMessage.AC_STATUS = ACNetwork.AC_DONE_WITH_WORK;
                statusMessage.hostName = ACNetwork.localhost;
                statusMessage.messageObject = ACNetwork.localhost;

                queueManager.send(host, statusMessage);
            }
        }*/
	}

	/**
	 * Update the local list of AgentControllers and their current status.
	 *
	 * @param host
	 * @param status
	 */
	public static void changeACStatus(String host, Integer status) {
		ACStatus.put(host, status);
	}

	/**
	 * Build the local list of AgentControllers and their current status.
	 */
	public void buildACStatus() {
		Log.logger.debug("Building ACs status list");
		Iterator<String> hosts = ACNetwork.agentControllerHostList.iterator();
		Log.logger.debug("Size of ACStatus = " + ACStatus.size());
		while (hosts.hasNext()) {
			String host = hosts.next();
			//if (!host.equalsIgnoreCase(Constants.localHost)) {
			Log.logger.debug("ACStatus = " + host);
			ACStatus.put(host, ACNetwork.AC_COMPUTING);
			//}
		}
	}

	/**
	 * Update the local list of AgentControllers if they time-out.
	 */
	public void updateTimeOutList() {
		for (String host : ACStatus.keySet()) {
			if (ACStatus.get(host) == ACNetwork.AC_COMPUTING) {
				// TODO: Currently disabled removing Time-out ACs
				// CTAStatus.remove(host);
				Log.logger.info(host + " : AC has timed out");
				ACStatus.put(host, ACNetwork.AC_TIMED_OUT);
			}
		}

	}

	/**
	 * Send a message to all the ACs to indicate change state computing for next step
	 * of simulation. Note that a given AC assumes that all ACs in the simulation,
	 * on receiving a new tick will start computing. Hence all the ACStatus will
	 * indicate this.
	 */
	public void changeReadyACsToComputing() {
		for (String host : ACStatus.keySet()) {
			if (ACStatus.get(host) == ACNetwork.AC_READY_FOR_NEXT_TICK) {
				// ACStatus.remove(host);
				// Utilities.Log.logger.info(host + " : AC has timed out");
				ACStatus.put(host, ACNetwork.AC_COMPUTING);
			}
		}
	}

	/**
	 * This is the part of the boot process where each agent controller reads
	 * how the simulation is set up across the machines.
	 */
	protected void readConfigurations() {
		try {
			Boot.readMachineConfigurations();
		} catch (FileNotFoundException ex) {
			Log.logger.error("Did not find the configuration file.");
			ex.printStackTrace();
		} catch (IOException ex) {
			Log.logger.error("IO error in configuration file.");
			ex.printStackTrace();
		}
	}

	/**
	 * @return the agentControllerName
	 */
	public String getAgentControllerName() {
		return agentControllerName;
	}

	/**
	 * @param agentControllerName the agentControllerName to set
	 */
	public void setAgentControllerName(String agentControllerName) {
		this.agentControllerName = agentControllerName;
	}

	/**
	 * This method was DEPRECATED. This is to be used only for test
	 * purposes to output results for every tick. This role will be later taken
	 * on by the output system. Since this method is called before the beginning
	 * of every tick, it reduces the performance of Phoenix. This function is
	 * currently empty. The simulation author can choose to override it to
	 * perform necessary actions before the beginning of a new tick.
	 * <p/>
	 * However, usage of this method is strongly discouraged. This method MAY BE
	 * removed after the output system is defined.
	 *
	 * TODO: Make same change in Phoenix.
	 */
	protected void cleanupBeforeNextTick() {
	}
}
