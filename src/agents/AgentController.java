package agents;

import globalData.Constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import system.Boot;
import system.Log;

import communication.ACNetwork;
import communication.QueueParameters;
import communication.messageProcessor.IMessageProcessor;
import communication.messages.ACStatusMessage;
import communication.queueManager.ACQueueManagement;


/**
 * The Default Agent Controller. All types of Agent Controllers extend this
 * class. The properties and methods of this class is shared among all Agent
 * controllers within the simulation. This class is responsible for
 * synchronization communication and progress of the simulation. This is first
 * class that establishes contact and proceeds with the simulation.
 */
public abstract class AgentController implements IMessageProcessor {

	/*
	 * The current name of the agent controller which has to be set. This name
	 * will be used for routing messages to this agent controller.
	 */
	private String agentControllerName;

	/**
	 * The current TICK number. TICK number indicates the steps of simulation
	 * and is used for synchronization of various CTAs.
	 */
	public static int currentTickNumber;

	/**
	 * A list of all agents handled by the Agent Controller
	 */
	public List<Agent> agents;

	/**
	 * The AgentController's communication queue.
	 */
	public ACQueueManagement queueManager;

	/**
	 * The current status of the other AgentControllers that are involved in the
	 * simulation.
	 */
	public static Map<String, Integer> ACStatus;

	/*
	 * Current simulation step the AgentController is in.
	 */
	public int getCurrentTickNumber() {
		return currentTickNumber;
	}

	/**
	 * Abstract function to be overridden by each AC. This function should
	 * contain the logic for checking if every agent has completed its task for
	 * the current tick.
	 * 
	 * @return
	 */
	public abstract boolean checkIfAllAgentsReadyForNextTick();

	/**
	 * Call the individual agents behaviour from its list of behaviour
	 */
	public abstract void activateAgentBehaviour();

	/**
	 * Check if all the AgentControllers are ready for the next step of the
	 * simulation.
	 * 
	 * @return
	 */
	public boolean checkIfAllACsReadyForNextTick(boolean flag) {
		if (ACStatus.size() == 0) {
			Log.logger.info("I am the only host");
			return true;
		}
		Log.logger.info("Holdmessages status:" + flag);
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
		// TODO: There is only one queue for ACs and those parameters are being
		// used.
		QueueParameters queueParameters = ACNetwork.ACMessageQueueParameters;
		queueManager = ACQueueManagement.getInstance(queueParameters, this);
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
		for (String host : ACStatus.keySet()) {
			Integer status = ACStatus.get(host);
			if (status == ACNetwork.AC_COMPUTING
					|| status == ACNetwork.AC_READY_FOR_NEXT_TICK) {

				ACStatusMessage statusMessage = new ACStatusMessage();
				statusMessage.AC_STATUS = ACNetwork.AC_READY_FOR_NEXT_TICK;
				statusMessage.messageObject = Constants.localHost;
				statusMessage.hostName = Constants.localHost;

				queueManager.send(host, statusMessage);
			}
		}
	}

	/**
	 * Broadcast a message that the current Agent COntroller has completed
	 * running the behaviour of all its agents once and is ready to proceede
	 * with the simulation.
	 */
	public void sendDoneWithWork() {
		for (String host : ACStatus.keySet()) {
			if (ACStatus.get(host) == ACNetwork.AC_COMPUTING
					|| ACStatus.get(host) == ACNetwork.AC_READY_FOR_NEXT_TICK) {

				ACStatusMessage statusMessage = new ACStatusMessage();
				statusMessage.AC_STATUS = ACNetwork.AC_DONE_WITH_WORK;
				statusMessage.hostName = Constants.localHost;
				statusMessage.messageObject = Constants.localHost;

				queueManager.send(host, statusMessage);
			}
		}
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
		Log.logger.info("Building list");
		Iterator<String> hosts = ACNetwork.agentControllerhostList.iterator();
		while (hosts.hasNext()) {
			String host = hosts.next();
			if (!host.equalsIgnoreCase(Constants.localHost)) {
				ACStatus.put(host, ACNetwork.AC_COMPUTING);
			}
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
	 * Send a message to all the ACs to indicate you are computing for next step
	 * of simulation.
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

	// TODO: Remove the KML outputs Write the output to java LiveGraph here.
	// protected void writeKMLFile(String ctaName) {
	// String stamp = new
	// SimpleDateFormat("hh-mm-ss-aaa_dd-MMMMM-yyyy").format(new
	// Date()).toString();
	// //kmlUtility.writeFile();
	// //kmlUtility.writeFile("kml/" + ctaName + "_" + stamp + "_" +
	// currentTickNumber + ".kml");
	//
	// }

	/**
	 * This is the part of the boot process where each agent controller reads
	 * how the simulation is set up across the machines.
	 */
	protected void readConfigurations() {
		try {
			Boot.loadMachineConfigurations("config/agentControllerConfig");
		} catch (FileNotFoundException ex) {
			Log.logger.info("Did not find the configuration file.");
			ex.printStackTrace();
		} catch (IOException ex) {
			Log.logger.info("IO error in configuration file.");
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
	 * @param agentControllerName
	 *            the agentControllerName to set
	 */
	public void setAgentControllerName(String agentControllerName) {
		this.agentControllerName = agentControllerName;
	}
}
