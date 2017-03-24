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
import java.util.UUID;
import system.Log;

/**
 *
 * @author murali
 */
public class LocationAgent extends Agent {

    private int xcor;
    private int ycor;
    UrbanSprawlSimulation.UrbanSprawlUniverse universe;
    CompositeBehaviour compositeBehaviour;

    public class LocationAgentAttributes extends AgentAttributes {

        public LocationAgentAttributes() {
            super();
        }
    }

    public class GrowOldBehavior implements Behaviour {

        @Override
        public void run(AgentAttributes agentAttributes) {
            double attraction = (double) agentAttributes.getAttribute("attraction");
            int maxAttraction = (int) agentAttributes.getAttribute("maxattraction");
            if (attraction <= (2 * maxAttraction)) {
                attraction = attraction + 0.5;
            } else {
                attraction = 0;
            }
            agentAttributes.addAttribute("attraction", attraction);
        }
    }

    public LocationAgent(int xcor, int ycor, UrbanSprawlSimulation.UrbanSprawlUniverse universe, CompositeBehaviour compositeBehaviour, AIDGenerator agentIDGenerator) {
        super(agentIDGenerator);
        this.xcor = xcor;
        this.ycor = ycor;
        this.universe = universe;
        this.compositeBehaviour = compositeBehaviour;
        GrowOldBehavior gob = new GrowOldBehavior();
        this.compositeBehaviour.add(gob);
        //TODO: Remove the next line as the default Constructor in AgentController already calls this.
        //Log.ConfigureLogger();
    }

    public LocationAgent(AIDGenerator aIDGenerator, UrbanSprawlSimulation.UrbanSprawlUniverse universe) {
        super(aIDGenerator);
        this.universe = universe;
        this.compositeBehaviour = new CompositeBehaviour();
        agentAttributes = new LocationAgentAttributes();
        GrowOldBehavior gob = new GrowOldBehavior();
        this.compositeBehaviour.add(gob);
        //TODO: Remove the next line as the default Constructor in AgentController already calls this.
        //Log.ConfigureLogger();
    }

    public void setCoordinates(int xcor, int ycor) {
        this.xcor = xcor;
        this.ycor = ycor;
    }
    
    public double getAttraction(UUID uuid){
        double attraction=(double) universe.getAgent(uuid).agentAttributes.getAttribute("attraction");
        return attraction;
    }
    @Override
    public void run() {
        this.setStatusFlag(Boolean.TRUE);
        compositeBehaviour.run(agentAttributes);
        //this.setObjectiveFlag(Boolean.TRUE);

    }
}
