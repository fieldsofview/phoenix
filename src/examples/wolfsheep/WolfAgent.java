/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.wolfsheep;

import agents.AIDGenerator;
import agents.attributes.AgentAttributes;
import agents.behaviour.Behaviour;
import system.Log;

public class WolfAgent extends agents.Agent {

    private int xcor;
    private int ycor;
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
            int health=(int) agentAttributes.getAttribute("Health");
            agentAttributes.addAttribute("Health", health-10);
            universe.place(xcor,ycor,getAID());
            Log.logger.info("Moved Wolf "+getAID()+" from "+oldX+","+oldY+" to "+xcor+", "+ycor);
            //universe.worldView();
        }
    }
    
    public class WolfAgentAttributes extends AgentAttributes{
        public WolfAgentAttributes(){
            super();
        }
    }
    
    public WolfAgent(AIDGenerator aidGenerator, WolfSheepPredationSimulation.WolfSheepUniverse universe) {
        super(aidGenerator);
        agentAttributes=new WolfAgentAttributes();
        move=new MoveBehaviour();
        this.universe=universe;
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