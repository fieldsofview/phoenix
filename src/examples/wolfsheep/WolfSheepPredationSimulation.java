/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.wolfsheep;

import agents.AIDGenerator;
import agents.Agent;
import agents.AgentController;
import agents.universe.Universe2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import system.Log;

/**
 *
 * @author onkar
 */
public class WolfSheepPredationSimulation extends AgentController {

    Properties simulationProperties;
    WolfSheepUniverse universe;

    @Override
    protected void cleanupBeforeNextTick() {
        universe.worldView();
    }

    public class WolfSheepUniverse extends Universe2D {

        WolfSheepUniverse(Integer maxx, Integer maxy) {
            super(maxx, maxy, 0, 0);
            Log.logger.info("Universe is setup.");
        }

        @Override
        public void worldView() {
            for (int i = 0; i < maxX; i++) {
                for (int j = 0; j < maxY; j++) {
                    System.out.print("[");
                    ArrayList<UUID> setOfAgents = (ArrayList<UUID>) world[i][j];
                    for (UUID uuid : setOfAgents) {
                        System.out.print(getAgentType(uuid) + ",");
                    }
                    System.out.print("]");
                }
                System.out.println("\n");
            }
        }

        public void agentDie(int xcor, int ycor, UUID uuid) {
            //agentMap.put(uuid, null);
            this.remove(xcor, ycor, uuid);
        }

        public Agent getAgent(UUID uuid) {
            return agents.get(uuid);
        }

        public ArrayList<UUID> getAgentsOnLocation(int x, int y) {
            return (ArrayList<UUID>) world[x][y];
        }

        public String getAgentType(UUID uuid) {
            String canonicalName = agents.get(uuid).getClass().getCanonicalName().toString();
            String shortName;
            shortName = canonicalName.split("examples.wolfsheep.")[1].charAt(0) + "" + agents.get(uuid).agentAttributes.getAttribute("Health");
            return shortName;
        }
        public AIDGenerator accessAidGenerator(){
            return getAgentIDGenerator();
        }
        public Map<UUID,Agent> accessAgentList(){
            return agents;
        }
    }

    public WolfSheepPredationSimulation() {
        super();
        this.setAgentControllerName(this.getClass().getCanonicalName());
        readConfigurations();
        addQueueListener();
        //TODO: Remove the next line as the default Constructor in AgentController already calls this.
        //system.Log.ConfigureLogger();
        buildACStatus();
    }

    @Override
    protected void shutdown() {
        sendDoneWithWork();
        System.exit(0);
    }

    @Override
    protected void setUp() {
        try {
            simulationProperties = new Properties();
            simulationProperties.load(new FileInputStream("config/examples_properties/wolfsheep.properties"));
            setupUniverse();
            setupGrass();
            setupWolfAgents();
            setupSheepAgents();
            sendReadyForTick();
            universe.worldView();
            Log.logger.info("Simulation is setup.");
        } catch (IOException ex) {
            Logger.getLogger(WolfSheepPredationSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        WolfSheepPredationSimulation wsp;
        try {
            wsp = new WolfSheepPredationSimulation();
            wsp.runAC();
        } catch (NumberFormatException e) {
            e.getMessage();
        }
    }

    private void setupUniverse() {
        universe = new WolfSheepUniverse(new Integer(simulationProperties.getProperty("maxx")),
                new Integer(simulationProperties.getProperty("maxy")));
    }

    private void setupGrass() {
        int xcor;
        int ycor;
        int grassPercent = new Integer(simulationProperties.getProperty("grass"));
        for (int i = 0; i < universe.maxX; i++) {
            for (int j = 0; j < universe.maxY; j++) {
                if (new Random().nextInt(100) <= grassPercent) {
                    GrassAgent grass = new GrassAgent(this.getAgentIDGenerator(), universe);
                    xcor = i;
                    ycor = j;
                    grass.setCoordinates(xcor, ycor);
                    grass.agentAttributes.addAttribute("Health", new Integer(simulationProperties.getProperty("currentgrowth")));
                    grass.agentAttributes.addAttribute("EatRate", new Integer(simulationProperties.getProperty("eatrate")));
                    grass.agentAttributes.addAttribute("GrowthRate", new Integer(simulationProperties.getProperty("growthrate")));
                    agents.put(grass.getAID(),grass);
                    universe.place(i, j, grass.getAID());
                }
            }
        }
    }

    private void setupWolfAgents() {
        int xcor;
        int ycor;
        for (int i = 0; i < new Integer(simulationProperties.getProperty("wolf")); i++) {
            WolfAgent wolf = new WolfAgent(this.getAgentIDGenerator(), universe);
            xcor = new Random().nextInt(universe.maxX);
            ycor = new Random().nextInt(universe.maxY);
            wolf.setCoordinates(xcor, ycor);
            wolf.agentAttributes.addAttribute("Health", new Random().nextInt(100));
            wolf.agentAttributes.addAttribute("WolfGain", new Integer(simulationProperties.getProperty("wolfgain")));
            wolf.agentAttributes.addAttribute("WolfReproduce", new Integer(simulationProperties.getProperty("wolfreproduce")));
            agents.put(wolf.getAID(),wolf);
            universe.place(xcor, ycor, wolf.getAID());
        }
    }

    private void setupSheepAgents() {
        int xcor;
        int ycor;
        for (int i = 0; i < new Integer(simulationProperties.getProperty("sheep")); i++) {
            SheepAgent sheep = new SheepAgent(this.getAgentIDGenerator(), universe);
            xcor = new Random().nextInt(universe.maxX);
            ycor = new Random().nextInt(universe.maxY);
            sheep.setCoordinates(xcor, ycor);
            sheep.agentAttributes.addAttribute("Health", new Random().nextInt(100));
            sheep.agentAttributes.addAttribute("SheepGain", new Integer(simulationProperties.getProperty("sheepgain")));
            sheep.agentAttributes.addAttribute("SheepReproduce", new Integer(simulationProperties.getProperty("sheepreproduce")));
            agents.put(sheep.getAID(),sheep);
            universe.place(xcor, ycor, sheep.getAID());
        }
    }
}
