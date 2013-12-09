/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.wolfsheep;

import agents.AIDGenerator;
import agents.attributes.AgentAttributes;
import agents.behaviour.Behaviour;
import agents.behaviour.CompositeBehaviour;
import java.util.Random;
import java.util.UUID;
import system.Log;

public class WolfAgent extends agents.Agent {

    private int xcor;
    private int ycor;
    CompositeBehaviour beh;
    WolfSheepPredationSimulation.WolfSheepUniverse universe;

    public class MoveBehaviour implements Behaviour {
        @Override
        public void run(AgentAttributes agentAttributes) {
            int oldX = xcor;
            int oldY = ycor;
            universe.remove(xcor, ycor, getAID());
            int vertical=new Random().nextInt(2)-1;
            int horizontal=new Random().nextInt(2)-1;
            xcor = (xcor + horizontal);
            if(xcor>universe.maxX){
                xcor=xcor-universe.maxX;
            }
            if(xcor<0){
                xcor=universe.maxX+xcor;
            }
            ycor = (ycor + vertical);
            if(ycor>universe.maxY){
                ycor=ycor-universe.maxY;
            }
            if(ycor<0){
                ycor=universe.maxY+ycor;
            }
            int health = (int) agentAttributes.getAttribute("Health");
            agentAttributes.addAttribute("Health", health - 10);
            universe.place(xcor, ycor, getAID());
            Log.logger.info("Moved Wolf " + getAID() + " from " + oldX + "," + oldY + " to " + xcor + ", " + ycor);
            //universe.worldView();
        }
    }
    
    public class EatBehaviour implements Behaviour{
        @Override
        public void run(AgentAttributes agentAttributes) {
            int health=new Integer((int) agentAttributes.getAttribute("Health"));
            if(health<=90){
                for(UUID u:universe.getAgentsOnLocation(xcor, ycor)){
                    if(universe.getAgentType(u).startsWith("S")){
                        SheepAgent temp=(SheepAgent) universe.getAgent(u);
                        temp.getEaten();
                        int gain = new Integer((int) agentAttributes.getAttribute("WolfGain"));
                        health += gain;
                        if(health>100){
                            health=100;
                        }
                        agentAttributes.addAttribute("Health", health);
                        Log.logger.info("Wolf " + getAID() + " ate sheep at " + xcor + "," + ycor);
                    }
                }
            }
        }
    }

    public class WolfAgentAttributes extends AgentAttributes {

        public WolfAgentAttributes() {
            super();
        }
    }

    public WolfAgent(AIDGenerator aidGenerator, WolfSheepPredationSimulation.WolfSheepUniverse universe) {
        super(aidGenerator);
        agentAttributes = new WolfAgentAttributes();
        beh=new CompositeBehaviour();
        MoveBehaviour move = new MoveBehaviour();
        EatBehaviour eat=new EatBehaviour();
        beh.add(move);
        beh.add(eat);
        this.universe = universe;
        Log.ConfigureLogger();
    }

    @Override
    public void run() {
        beh.run(agentAttributes);
        this.setStatusFlag(true);
        if ((int) agentAttributes.getAttribute("Health") <= 0) {
            die();
        }
    }

    public void setCoordinates(int xcor, int ycor) {
        this.xcor = xcor;
        this.ycor = ycor;
    }

    public void die() {
        universe.agentDie(xcor, ycor, getAID());
        Log.logger.info("Wolf " + getAID() + " has died.");
        //universe.worldView();
        this.setObjectiveFlag(true);
    }
}