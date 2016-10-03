/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.wolfsheep;

import agents.AIDGenerator;
import agents.Agent;
import agents.attributes.AgentAttributes;
import agents.behaviour.Behaviour;
import agents.behaviour.CompositeBehaviour;
import system.Log;

/**
 *
 * @author onkar
 */
public class GrassAgent extends Agent {

    private int xcor;
    private int ycor;
    WolfSheepPredationSimulation.WolfSheepUniverse universe;
    CompositeBehaviour beh;

    public class GrassAgentAttributes extends AgentAttributes {

        public GrassAgentAttributes() {
            super();
        }
    }

    public class GrowBehaviour implements Behaviour {
        @Override
        public void run(AgentAttributes agentAttributes) {
            int health = new Integer((int) agentAttributes.getAttribute("Health"));
            int growthRate = new Integer((int) agentAttributes.getAttribute("GrowthRate"));
            if(health<90){
                health+=growthRate;
                agentAttributes.addAttribute("Health", health);
            }
        }
    }

    public GrassAgent(AIDGenerator aidGenerator, WolfSheepPredationSimulation.WolfSheepUniverse universe) {
        super(aidGenerator);
        this.universe = universe;
        agentAttributes = new GrassAgentAttributes();
        beh=new CompositeBehaviour();
        GrowBehaviour grow=new GrowBehaviour();
        beh.add(grow);
        //TODO: Remove the next line as the default Constructor in AgentController already calls this.
        //Log.ConfigureLogger();
    }

    @Override
    public void run() {
        this.setStatusFlag(true);
        beh.run(agentAttributes);
        //this.setObjectiveFlag(true);
        if ((int) agentAttributes.getAttribute("Health") <= 0) {
            universe.remove(xcor, ycor, getAID());
            this.setObjectiveFlag(true);
        }
    }

    public void setCoordinates(int xcor, int ycor) {
        this.xcor = xcor;
        this.ycor = ycor;
    }

    /**
     * Method to be called when grass is to be eaten
     */
    public void getEaten() {
        int growth = (int) agentAttributes.getAttribute("Health");
        growth -= (int) agentAttributes.getAttribute("EatRate");
        agentAttributes.addAttribute("Health", growth);
    }
}
