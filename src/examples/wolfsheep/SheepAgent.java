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
import java.util.Random;
import java.util.UUID;
import system.Log;

/**
 *
 * @author onkar
 */
public class SheepAgent extends Agent {

    int xcor;
    int ycor;
    CompositeBehaviour beh;
    WolfSheepPredationSimulation.WolfSheepUniverse universe;

    public class MoveBehaviour implements Behaviour {

        @Override
        public void run(AgentAttributes agentAttributes) {
            int oldX = xcor;
            int oldY = ycor;
            universe.remove(xcor, ycor, getAID());
            int vertical = new Random().nextInt(2) - 1;
            int horizontal = new Random().nextInt(2) - 1;
            xcor = (xcor + horizontal);
            if (xcor > universe.maxX) {
                xcor = xcor - universe.maxX;
            }
            if (xcor < 0) {
                xcor = universe.maxX + xcor;
            }
            ycor = (ycor + vertical);
            if (ycor > universe.maxY) {
                ycor = ycor - universe.maxY;
            }
            if (ycor < 0) {
                ycor = universe.maxY + ycor;
            }
            universe.place(xcor, ycor, getAID());
            int health = new Integer((int) agentAttributes.getAttribute("Health"));
            agentAttributes.addAttribute("Health", health - 5);
            Log.logger.info("Moved Sheep " + getAID() + " from " + oldX + "," + oldY + " to " + xcor + ", " + ycor);
            //universe.worldView();
        }
    }

    public class EatBehaviour implements Behaviour {

        @Override
        public void run(AgentAttributes agentAttributes) {
            int health = new Integer((int) agentAttributes.getAttribute("Health"));
            if (health <= 90) {
                for (UUID u : universe.getAgentsOnLocation(xcor, ycor)) {
                    if (universe.getAgentType(u).startsWith("G")) {
                        GrassAgent temp = (GrassAgent) universe.getAgent(u);
                        temp.getEaten();
                        int gain = new Integer((int) agentAttributes.getAttribute("SheepGain"));
                        health += gain;
                        if (health > 100) {
                            health = 100;
                        }
                        agentAttributes.addAttribute("Health", health);
                        Log.logger.info("Sheep " + getAID() + " ate grass at " + xcor + "," + ycor);
                    }
                }
            }
        }
    }

    public class ReproduceBehaviour implements Behaviour {

        @Override
        public void run(AgentAttributes agentAttributes) {
            int repRate = new Integer((int) agentAttributes.getAttribute("SheepReproduce"));
            int health = new Integer((int) agentAttributes.getAttribute("Health"));
            if (new Random().nextInt(100) < repRate) {
                SheepAgent sheep = new SheepAgent(universe.accessAidGenerator(), universe);
                sheep.setCoordinates(xcor+1,ycor);
                sheep.agentAttributes.addAttribute("Health", health/2);
                sheep.agentAttributes.addAttribute("SheepGain", agentAttributes.getAttribute("sheepgain"));
                sheep.agentAttributes.addAttribute("SheepReproduce", agentAttributes.getAttribute("sheepreproduce"));
                universe.accessAgentList().put(sheep.getAID(),sheep);
                universe.place(xcor, ycor, sheep.getAID());
                //Set current sheep's health to half its original
                agentAttributes.addAttribute("Health", health/2);
                Log.logger.info("Sheep "+getAID()+" reproduced sheep "+sheep.getAID());
            }
        }
    }

    public class SheepAgentAttributes extends AgentAttributes {

        public SheepAgentAttributes() {
            super();
        }
    }

    public SheepAgent(AIDGenerator aidGenerator, WolfSheepPredationSimulation.WolfSheepUniverse universe) {
        super(aidGenerator);
        this.universe = universe;
        agentAttributes = new SheepAgentAttributes();
        MoveBehaviour move = new MoveBehaviour();
        EatBehaviour eat = new EatBehaviour();
        //TODO: Add mechanism to add new agents safely
        //ReproduceBehaviour rep=new ReproduceBehaviour();
        beh = new CompositeBehaviour();
        beh.add(move);
        beh.add(eat);
        //beh.add(rep);
        //TODO: Remove the next line as the default Constructor in AgentController already calls this.
        //Log.ConfigureLogger();
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
        Log.logger.info("Sheep " + getAID() + " has died.");
        //universe.worldView();
        this.setObjectiveFlag(true);
    }

    public void getEaten() {
        Log.logger.info("Sheep " + getAID() + " has been eaten.");
        agentAttributes.addAttribute("Health", 0);
    }
}
