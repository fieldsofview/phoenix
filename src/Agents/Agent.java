package Agents;

import java.io.Serializable;

import Agents.Attributes.AgentAttributes;
import Agents.Behaviour.CompositeBehaviour;
import java.util.UUID;

/**
 * Abstract class which is extended to create a simulation. The class defines that
 * an agent is unique and has a set of behaviours associated with it. 
 * The agents use behaviours to achieve a set of objectives. 
 * When all the objectives are achieved the objective flag is set by the agent 
 * and the agent terminates. Agents also have a single or a set of utility 
 * functions to measure if the it has achieved its objectives.
 * 
 * Every agent as a set of attributes and a set of behaviours The agent may live
 * in a universe or independent, i.e their interaction is direct with each other
 * or their surroundings and does not depend on each other's position.
 * 
 */

abstract public class Agent extends Thread implements Serializable {

    /*
	 * Current serialization version ID. Please change this while modifying 
	 * agents.
	 */
	private static final long serialVersionUID = 1L;
	/*
     * Unique Identifier for each agent
     */
    private UUID AID;
    /*
     * The flag to indicate if the final objective for the agent is complete.
     * This flag determines the life span of an agent.
     */
    private boolean objectiveFlag;
    /*
     * Status of the agent. Agents may have have different status as prescribed
     * in a simulation. The simulation itself may be driven by this flag. 
     * This flag determines if an agent has completed one iteration of its 
     * behaviour.
     */
    private boolean statusFlag;
    /*
     * The behaviours for this agent
     */
    public CompositeBehaviour behaviour = null;
    
    /*
     * A list of agent properties as a set of key value pairs
     */
    public AgentAttributes agentAttributes;
    
    /**
     * returns the agent id
     * @return agent identifier.
     */
    public UUID getAID() {
        return AID;
    }

    /* Return the agent's current objective status */
    public boolean getObjectiveFlag() {
        return objectiveFlag;
    }

    /* Return the status of the agent */
    public boolean getStatusFlag() {
        return statusFlag;
    }

    /* Set the current status flag for an agent*/
    public void setStatusFlag(boolean status){
        statusFlag = status;
    }
}