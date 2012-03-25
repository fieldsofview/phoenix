package Agents;

import java.io.Serializable;

import Agents.Attributes.AgentAttributes;
import Agents.Behaviour.CompositeBehaviour;

/**
 * Abstract class which must be extended by all agents. The class defines that
 * an agent is unique and has a behaviour associated with it. The agents use the
 * behaviours to achieve a set of objectives. When all the objectives are set
 * are achieved the objective flag is set by the agent. Agents also have a
 * single or a set of utility functions to measure if the agent has actually
 * achieved its objectives.
 * 
 * Every agent as a set of attributes and a set of behaviour The agent may live
 * in a universe or independent of the universe i.e. they their interaction is
 * direct and does not depend on each other's position.
 * 
 */

abstract public class Agent extends Thread implements Serializable {

    /*
     * Unique Identifier for each agent
     */
    private String AID;
    /*
     * The flag to indicate if the final objective for the agent is complete. This flag determines the life span of
     * an agent.
     */
    private boolean objectiveFlag;
    /*
     * Status of the agent. Agents may have have different status as prescribed in a simulation. The simulation itself
     * may be driven by this flag. This flag determines if an agent has completed one iteration of its behaviour.
     */
    private boolean statusFlag;
    /*
     * The behaviours for this agent
     */
    public CompositeBehaviour behaviour = null;

    public AgentAttributes agentAttributes;

    /**
     * returns the agent id
     * @return returns the agent id.
     */
    public String getAID() {
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
