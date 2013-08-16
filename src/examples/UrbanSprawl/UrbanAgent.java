/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.UrbanSprawl;

import agents.AIDGenerator;
import agents.Agent;
import agents.attributes.AgentAttributes;
import agents.behaviour.Behaviour;
import agents.behaviour.CompositeBehaviour;
import agents.universe.Universe;
import java.util.Random;
import java.util.UUID;
import system.Log;

/**
 *
 * @author murali
 */
public class UrbanAgent extends Agent {

    CompositeBehaviour compositeBehaviour;
    int xcor;
    int ycor;
    UrbanSprawlSimulation.UrbanSprawlUniverse universe;

    public class UrbanAgentAttributes extends AgentAttributes {

        public UrbanAgentAttributes() {
            super();
        }
    }

    public class SprawlBehaior implements Behaviour {

        @Override
        public void run(AgentAttributes agentAttributes) {
            System.out.println("in sprawl behavior");
            int searchangle = new Integer((int) agentAttributes.getAttribute("searchangle"));
            //int random = (new Random().nextInt(searchangle));
            //this needs to be checked with Onkar
            int random = (new Random().nextInt(10));
            int oldx = xcor;
            int oldy = ycor;
            int ahead = ycor + 1;
            int right = (random + xcor) + 1;
            int left = (random + xcor )- 1;
            double rightAttraction = 0;
            double leftAttraction = 0;
            double attractionAhead = 0;
            System.out.println("xcor "+xcor+" ycor "+ycor+" right "+right+" left"+left);
            if(left>0 && right < universe.maxX && left < universe.maxY){
            for (UUID uuid : universe.getAgentsOnLocation(xcor, ahead)) {
                if (universe.getAgentType(uuid).startsWith("L")) {
                    attractionAhead = (double) ((LocationAgent) universe.getAgent(uuid)).agentAttributes.getAttribute("attraction");
                }
            }
            for (UUID uuid : universe.getAgentsOnLocation(right, ahead)) {
                if (universe.getAgentType(uuid).startsWith("L")) {
                    rightAttraction = (double) ((LocationAgent) universe.getAgent(uuid)).agentAttributes.getAttribute("attraction");
                }
            }
            for (UUID uuid : universe.getAgentsOnLocation(left, ahead)) {
                if (universe.getAgentType(uuid).startsWith("L")) {
                    leftAttraction = (double) ((LocationAgent) universe.getAgent(uuid)).agentAttributes.getAttribute("attraction");
                }
            }
            
            Log.logger.info("attraction ahead "+attractionAhead +" right attraction "+rightAttraction+ " left attraction "+leftAttraction);
            if (rightAttraction > attractionAhead && rightAttraction > leftAttraction) {
                //move the urban agent 
               // universe.remove(xcor, ycor, getAID());
                xcor = new Random().nextInt(right);
                universe.place(xcor, ahead, getAID());
                Log.logger.info("Agent moved towards right " + getAID() + "from [" + oldx + "," + oldy + "]" + "to [" + xcor + "," + ycor + "]");
            } else {
                if (leftAttraction > attractionAhead) {
                    // move the urban agent
                    //universe.remove(xcor, ycor, getAID());
                    xcor = new Random().nextInt(left);
                    universe.place(xcor, ahead, getAID());
                    Log.logger.info("Agent moved towards left" + getAID() + "from [" + oldx + "," + oldy + "]" + "to [" + xcor + "," + ycor + "]");
                }
            }

        }
        
        }      
    }

    public class SeekingBehavior implements Behaviour {

        @Override
        public void run(AgentAttributes agentAttributes) {
            double attraction = 0.0;
            int maxAttraction = 0;
            int stayCounter = 0;
            int searchangle = new Integer((int) agentAttributes.getAttribute("searchangle"));
            int agentState = (int) agentAttributes.getAttribute("agentState");
            System.out.println("xcor "+xcor+" ycor "+ycor);
            universe.remove(xcor, ycor, getAID());
            if (agentState == 1) {
                for (UUID uuid : universe.getAgentsOnLocation(xcor, ycor)) {

                    if (universe.getAgentType(uuid).startsWith("L")) {
                        attraction = (double) ((LocationAgent) universe.getAgent(uuid)).agentAttributes.getAttribute("attraction");
                        maxAttraction = (int) ((LocationAgent) universe.getAgent(uuid)).agentAttributes.getAttribute("maxattraction");
                    }

                    //SEEKER           
                    double randomAttraction = new Random().nextDouble() * attraction;
                    if (wantToBuild(randomAttraction, maxAttraction)) {
                        stayCounter = (int) agentAttributes.getAttribute("waittime");
                        changeState();
                        Log.logger.info("Agent :" + getAID() + " changed state to house");
                    } else {
                        int patienceCounter = (int) agentAttributes.getAttribute("patienceCounter");
                        if (patienceCounter > 0) {
                            new SprawlBehaior().run(agentAttributes);
                        }
                        //decrement patience counter
                        patienceCounter--;
                        agentAttributes.addAttribute("patienceCounter", patienceCounter);
                        attraction = attraction + 0.1;
                        universe.getAgent(uuid).agentAttributes.addAttribute("attraction", attraction);
                    }
                    }
                }//end of agent state==1
             else if (agentState == 2) {
                for (UUID uuid : universe.getAgentsOnLocation(xcor, ycor)) {

                    if (universe.getAgentType(uuid).startsWith("L")) {
                        attraction = (double) ((LocationAgent) universe.getAgent(uuid)).agentAttributes.getAttribute("attraction");
                        maxAttraction = (int) ((LocationAgent) universe.getAgent(uuid)).agentAttributes.getAttribute("maxattraction");
                    }
                    //HOUSE
                    if (attraction <= (2 * maxAttraction)) {
                        attraction = attraction + 0.5;
                    } else {
                        attraction = 0;
                    }
                    universe.getAgent(uuid).agentAttributes.addAttribute("attraction", attraction);
                    stayCounter = stayCounter - 1;
                    universe.getAgent(uuid).agentAttributes.addAttribute("waittime", stayCounter);
                    if (stayCounter <= 0) {
                        Log.logger.info("Agent :" + getAID() + " changed state to seeker");
                        int patienceCounter = (int) agentAttributes.getAttribute("patienceCounter");
                        int patience = (int) agentAttributes.getAttribute("patience");
                        agentAttributes.addAttribute("patience", patienceCounter);
                        changeState();

                    }
                }//end of agent state 2

            }
        
        }
    }
    @Override
    public void run() {
        this.setStatusFlag(Boolean.TRUE);
        compositeBehaviour.run(agentAttributes);
        //this.setObjectiveFlag(Boolean.TRUE);

    }

    public UrbanAgent(AIDGenerator aIDGenerator, CompositeBehaviour compositeBehaviour, UrbanSprawlSimulation.UrbanSprawlUniverse universe) {
        super(aIDGenerator);
        this.compositeBehaviour = compositeBehaviour;
        this.universe = universe;
        SeekingBehavior seekingBehavior = new SeekingBehavior();
        //SprawlBehaior sb=new SprawlBehaior();
        this.compositeBehaviour.add(seekingBehavior);
        //this.compositeBehaviour.add(sb);
        Log.ConfigureLogger();
    }

    public UrbanAgent(AIDGenerator agentIDGenerator, UrbanSprawlSimulation.UrbanSprawlUniverse universe) {
        super(agentIDGenerator);
        this.universe = universe;
        this.compositeBehaviour = new CompositeBehaviour();
        agentAttributes = new UrbanAgentAttributes();
        SeekingBehavior seekingBehavior = new SeekingBehavior();
        // SprawlBehaior sb=new SprawlBehaior();        
        this.compositeBehaviour.add(seekingBehavior);
        //this.compositeBehaviour.add(sb);        
        Log.ConfigureLogger();
    }

    public void setCoordinates(int xcor, int ycor) {
        this.xcor = xcor;
        this.ycor = ycor;
    }

    public void changeState() {
        Integer state = (Integer) agentAttributes.getAttribute("agentState");
        if (state == 1) {
            //if state of agent is seeker
            //state 1 is for seeker
            state = 2;
            agentAttributes.addAttribute("agentState", 2);
        } else if (state == 2) {
            agentAttributes.addAttribute("agentState", 1);
        }
    }

    public boolean wantToBuild(double attraction, double maxattraction) {
        int patienceCounter = (int) agentAttributes.getAttribute("patienceCounter");
        System.out.println("random attraction " + attraction + " threshold: " + maxattraction / 2 + " patience counter:" + patienceCounter);
        if (attraction > (maxattraction / 2) || patienceCounter == 0) {
            return true;
        } else {
            return false;
        }
    }
}
