/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.wolfsheep;

import agents.AIDGenerator;
import agents.Agent;
import agents.attributes.AgentAttributes;
import agents.behaviour.Behaviour;
import system.Log;

/**
 *
 * @author onkar
 */
public class SheepAgent extends Agent{
    int xcor;
    int ycor;
    MoveBehaviour move;
    WolfSheepPredationSimulation.WolfSheepUniverse universe;

    
    public class MoveBehaviour implements Behaviour{
        @Override
        public void run(AgentAttributes agentAttributes) {
            int oldX=xcor;
            int oldY=ycor;
            universe.remove(xcor,ycor,getAID());
            xcor=(xcor+1)%universe.maxX;
            ycor=(ycor+1)%universe.maxY;
            universe.place(xcor,ycor,getAID());
            int health=new Integer((int) agentAttributes.getAttribute("Health"));
            agentAttributes.addAttribute("Health", health-10);
            Log.logger.info("Moved Sheep "+getAID()+" from "+oldX+","+oldY+" to "+xcor+", "+ycor);
            //universe.worldView();
        }  
    }
    
    public class SheepAgentAttributes extends AgentAttributes{
        public SheepAgentAttributes(){
            super();
        }
    }
    
    public SheepAgent(AIDGenerator aidGenerator,WolfSheepPredationSimulation.WolfSheepUniverse universe){
        super(aidGenerator);
        this.universe=universe;
        agentAttributes=new SheepAgentAttributes();
        move=new MoveBehaviour();
        Log.ConfigureLogger();
    }
    @Override
    public void run() {
        move.run(agentAttributes);
        this.setStatusFlag(true);
        if((int)agentAttributes.getAttribute("Health")<=0){
            universe.agentDie(xcor, ycor, getAID());
            this.setObjectiveFlag(true);
        }
    }
    public void setCoordinates(int xcor,int ycor){
        this.xcor=xcor;
        this.ycor=ycor;
    }
    
}
