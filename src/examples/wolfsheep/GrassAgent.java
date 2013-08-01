/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.wolfsheep;

import agents.AIDGenerator;
import agents.Agent;
import agents.attributes.AgentAttributes;
import system.Log;

/**
 *
 * @author onkar
 */
public class GrassAgent extends Agent{
    private int xcor;
    private int ycor;
    WolfSheepPredationSimulation.WolfSheepUniverse universe;
    
    public class GrassAgentAttributes extends AgentAttributes{
        public GrassAgentAttributes(){
            super();
        }
    }
    
    public GrassAgent(AIDGenerator aidGenerator,WolfSheepPredationSimulation.WolfSheepUniverse universe){
        super(aidGenerator);
        this.universe=universe;
        agentAttributes=new GrassAgentAttributes();
        Log.ConfigureLogger();
    }
    @Override
    public void run() {
        this.setStatusFlag(true);
        //this.setObjectiveFlag(true);
        if((long)agentAttributes.getAttribute("Health")<=0){
            universe.remove(xcor, ycor, getAID());
            this.setObjectiveFlag(true);
        }
    }
    public void setCoordinates(int xcor,int ycor){
        this.xcor=xcor;
        this.ycor=ycor;
    }
    /**
     * Method to be called when grass is to be eaten
     */
    public void getEaten(){
        long growth=(long) agentAttributes.getAttribute("Health");
        growth-=(long)agentAttributes.getAttribute("EatRate");
        agentAttributes.addAttribute("Health", growth);
    }
}
