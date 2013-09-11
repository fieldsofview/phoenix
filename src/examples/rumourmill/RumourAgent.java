/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.rumourmill;

import agents.AIDGenerator;
import agents.Agent;
import agents.attributes.AgentAttributes;
import agents.behaviour.Behaviour;
import agents.behaviour.CompositeBehaviour;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import system.Log;

/**
 *
 * @author onkar
 */
public class RumourAgent extends Agent {

    RumourMillSimulation.RumourMillUniverse universe;
    CompositeBehaviour beh;

    public void tell() {
        int timesHeard=(int) this.agentAttributes.getAttribute("TimesHeard");
        int firstHeard=(int) this.agentAttributes.getAttribute("FirstHeard");
        timesHeard+=1;
        this.agentAttributes.addAttribute("TimesHeard", timesHeard);
        if(firstHeard==-1){
            this.agentAttributes.addAttribute("FirstHeard", universe.getTickNumber());
        }
    }

    public class RumourAgentAttributes extends AgentAttributes {

        public RumourAgentAttributes() {
            super();
        }
    }

    public class SpreadRumourBehaviour implements Behaviour {

        @Override
        public void run(AgentAttributes agentAttributes) {
            int timesHeard = (int) agentAttributes.getAttribute("TimesHeard");
            if (timesHeard != 0) {
                //Inform one random neighbour
                int x = 0, y = 0;
                int xcor = (int) agentAttributes.getAttribute("xcor");
                int ycor = (int) agentAttributes.getAttribute("ycor");
                int vertical = new Random().nextInt(3) - 1;
                int horizontal = new Random().nextInt(3) - 1;
                x = xcor + horizontal;
                y = ycor + vertical;
                if (x < 0) {
                    x = universe.maxX + x;
                } else if (x >= universe.maxX) {
                    x = x - universe.maxX;
                }
                if (y < 0) {
                    y = universe.maxY + y;
                } else if (y >= universe.maxY) {
                    y = y - universe.maxY;
                }
                if(!(x==xcor && y==ycor)){ 
                    ArrayList<UUID> agentSet=(ArrayList<UUID>) universe.world[x][y];
                    for(UUID u:agentSet){
                        RumourAgent rms=(RumourAgent) universe.getAgent(u);
                        rms.tell();
                    }
                }
                //universe.worldView();
            }
        }
    }

    public RumourAgent(AIDGenerator agentIDGenerator, RumourMillSimulation.RumourMillUniverse universe) {
        super(agentIDGenerator);
        this.universe = universe;
        agentAttributes = new RumourAgentAttributes();
        beh = new CompositeBehaviour();
        SpreadRumourBehaviour spread=new SpreadRumourBehaviour();
        beh.add(spread);
        Log.ConfigureLogger();
    }

    @Override
    public void run() {
        beh.run(agentAttributes);
        this.setStatusFlag(true);
        if((int)this.agentAttributes.getAttribute("TimesHeard")>=100){
            this.setObjectiveFlag(true);
        }
    }
}
