/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */
package agents;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import system.Boot;
import system.Constants;
import system.Log;

import communication.ACNetwork;
import communication.QueueParameters;
import communication.messages.ACStatusMessage;
import communication.queueManager.ACQueueManagement;
import java.util.UUID;

/**
 * The Default Agent Controller. All types of Agent Controllers extend this
 * class. The properties and methods of this class is shared among all Agent
 * controllers within the simulation. This class is responsible for
 * synchronisation communication and progress of the simulation. This is first
 * class that establishes contact and proceeds with the simulation.
 */
public abstract class AgentController {

    /*
     * The current name of the agent controller which has to be set. This name
     * will be used for routing messages to this agent controller.
     */
    private String agentControllerName;
    /**
     * The current TICK number. TICK number indicates the steps of simulation
     * and is used for synchronisation of various CTAs.
     */
    public static int currentTickNumber;
    /**
     * A Map of all agents handled by the Agent Controller.
     * This has been changed to a Map from a List.
     */
    public Map<UUID,Agent> agents;
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
     * instance will be used by the AgentContoller to generate all the agents
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
        system.Log.ConfigureLogger();
        createListObjects();
        currentTickNumber = 0;
    }

    /**
     * This function is called to create instances for the list of agents
     * variable and the current status list for all the AgentControllers.
     */
    private void createListObjects() {
        ACStatus = Collections.synchronizedMap(new HashMap<String, Integer>());
        agents = Collections.synchronizedMap(new HashMap<UUID,Agent>());
    }

    /**
     * This is the first method called by a new AgentController. By default it
     * does a agent setup-up which creates various agents as defined in the
     * class. But it can be overridden to perform other tasks.
     */
    protected void runAC() {
        setUp();

        // Runs until objectives for all agents is fulfilled
        while (!objectiveSatisfiedForAllAgents()) {

            // Time out for waiting for other agents
            long timeBeforewaiting = System.currentTimeMillis();

            while (!checkIfAllACsReadyForNextTick()) {
                try {
                    long timeNow = System.currentTimeMillis();
                    if (timeNow - timeBeforewaiting >= ACNetwork.MAXIMUM_TIME_OUT_FOR_AC) {
                        updateTimeOutList();
                        Log.logger.info("Continuing with run after timeout");
                        break;
                    } else {
                        // wait until all ACs are ready
                        Thread.sleep(500);
                    }

                } catch (InterruptedException ex) {
                    Log.logger.info(ex.getMessage());
                }
            }
            /*
             * Call the deprecated method for tick-wise outputs
             */
            cleanupBeforeNextTick();
            /* INCREMENT TICK NUMBER */
            currentTickNumber++;
            Log.logger.info("TICK NUMBER: " + currentTickNumber);
            activateAgentBehaviour();
        }

        cleanUp();
    }

    /**
     * Checks and returns if all agents have satisfied their objectives and
     * hence the simulation can cease to run.
     *
     * @return
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
     * Perform any user defined clean-up operations
     */
    protected abstract void cleanUp();

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
     * Abstract function to be overridden by each AC. This function should
     * contain the logic for checking if every agent has completed its task for
     * the current tick.
     *
     * @return
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
         * run every agent's behaviour in a chosen order. A scheduling polict
         * can be implemented by the user here.
         */
        for (Agent p : agents.values()) {
            if (!p.getObjectiveFlag()) {
                p.run();
            }
        }
    }

    /**
     * Check if all the AgentControllers are ready for the next step of the
     * simulation.
     *
     * @return
     */
    public boolean checkIfAllACsReadyForNextTick() {
        if (ACStatus.size() == 0) {
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
        // TODO: There is only one queue for ACs and those parameters are being
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
            //if (!host.equalsIgnoreCase(Constants.localHost)) {
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

    /**
     * This is the part of the boot process where each agent controller reads
     * how the simulation is set up across the machines.
     */
    protected void readConfigurations() {
        try {
            Boot.readMachineConfigurations();
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
     * @param agentControllerName the agentControllerName to set
     */
    public void setAgentControllerName(String agentControllerName) {
        this.agentControllerName = agentControllerName;
    }

    /**
     * @deprecated This method is DEPRECATED. This is to be used only for test
     * purposes to output results for every tick. This role will be later taken
     * on by the output system. Since this method is called before the beginning
     * of every tick, it reduces the performance of Phoenix. This function is
     * currently empty. The simulation author can choose to override it to
     * perform necessary actions before the beginning of a new tick.
     *
     * However, usage of this method is strongly discouraged. This method MAY BE
     * removed after the output system is defined.
     */
    protected void cleanupBeforeNextTick() {
    }
}
