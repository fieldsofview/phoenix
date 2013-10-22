/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.UrbanSprawl;

import agents.AIDGenerator;
import agents.Agent;
import agents.AgentController;
import agents.universe.Universe2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import module.database.DatabaseModule;
import system.Log;

/**
 *
 * @author murali
 */
public class UrbanSprawlSimulation extends AgentController {

    public UrbanSprawlSimulation() {
        super();
        this.setAgentControllerName(this.getClass().getCanonicalName());
        readConfigurations();
        addQueueListener();
        system.Log.ConfigureLogger();
        buildACStatus();
    }
    UrbanSprawlUniverse universe;
    Properties simulationProperties;
    DatabaseModule databaseModule;
    @Override
    protected void cleanupBeforeNextTick() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        universe.worldView();
    }

    public class UrbanSprawlUniverse extends Universe2D {

        private UrbanSprawlUniverse(Integer maxX, Integer maxY) {
            super(maxX, maxY, 0, 0);
            Log.logger.info("Universe is setup.");
        }

        @Override
        public void worldView() {
            String opData="";
            for (int i = 0; i < maxX; i++) {
                for (int j = 0; j < maxY; j++) {
                    ArrayList<UUID> setOfAgents = (ArrayList<UUID>) world[i][j];
                    if (!setOfAgents.isEmpty()&& setOfAgents.size()!=0) {
                        System.out.print("[");
                        for (UUID uuid : setOfAgents) {
                            //System.out.print(getAgentType(uuid) + ",");
                            if (agents.get(uuid).agentAttributes.getAttribute("oldx") != null) {
                                System.out.print("[" + agents.get(uuid).agentAttributes.getAttribute("oldx") + "," + agents.get(uuid).agentAttributes.getAttribute("oldy") + "]:[" + agents.get(uuid).agentAttributes.getAttribute("xcor") + "," + agents.get(uuid).agentAttributes.getAttribute("ycor") + "]");
                            }
                        }
                        System.out.print("]");
                    }
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
            //System.out.println("canonical Name :"+canonicalName);
            String shortName;
            shortName = canonicalName.split("examples.UrbanSprawl.")[1].charAt(0) + "" + agents.get(uuid);
            //System.out.println("short name of agent :"+uuid+" is "+shortName);
            return shortName;
        }

        public AIDGenerator accessAidGenerator() {
            return getAgentIDGenerator();
        }

        public Map<UUID, Agent> accessAgentList() {
            return agents;
        }
    }

    @Override
    protected void cleanUp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void setUp() {
        try {
            simulationProperties = new Properties();
            simulationProperties.load(new FileInputStream("config/examples_properties/urbansprawl.properties"));
     //       databaseModule=new DatabaseModule();
            setupUniverse();
            setUpLocation();
            setUpUrbanAgents();
            sendReadyForTick();
            universe.worldView();
            Log.logger.info("Simulation is setup.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UrbanSprawlSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UrbanSprawlSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void setUpUrbanAgents() {
        int xcor;
        int ycor;
        int noOfUrbanAgents = new Integer(simulationProperties.getProperty("population"));
        for (int i = 0; i < noOfUrbanAgents; i++) {
            UrbanAgent urbanAgent = new UrbanAgent(this.getAgentIDGenerator(), universe);
            xcor = universe.maxX / 2;
            ycor = universe.maxY / 2;
            //xcor=0;ycor=0;
            urbanAgent.setCoordinates(xcor, ycor);
            urbanAgent.agentAttributes.addAttribute("searchangle", new Integer(simulationProperties.getProperty("searchangle")));
            urbanAgent.agentAttributes.addAttribute("patience", new Integer(simulationProperties.getProperty("patience")));
            urbanAgent.agentAttributes.addAttribute("waittime", new Integer(simulationProperties.getProperty("waittime")));
            urbanAgent.agentAttributes.addAttribute("agentState", new Integer(1));
            urbanAgent.agentAttributes.addAttribute("xcor", new Integer(xcor));
            urbanAgent.agentAttributes.addAttribute("ycor", new Integer(ycor));
            urbanAgent.agentAttributes.addAttribute("oldx", new Integer(xcor));
            urbanAgent.agentAttributes.addAttribute("oldy", new Integer(ycor));
            urbanAgent.agentAttributes.addAttribute("patienceCounter", new Integer(simulationProperties.getProperty("patience")));
            agents.put(urbanAgent.getAID(), urbanAgent);
            universe.place(xcor, ycor, urbanAgent.getAID());
        }
        Log.logger.info("urban agents are setup.");
    }

    private void setUpLocation() {
        int xcor;
        int ycor;
        int smoothNess = new Integer(simulationProperties.getProperty("smoothness"));
        int maxAttraction = new Integer(simulationProperties.getProperty("maxattraction"));
        for (int i = 0; i < universe.maxX; i++) {
            for (int j = 0; j < universe.maxY; j++) {
                double attraction = new Random().nextInt(maxAttraction);
                LocationAgent locationAgent = new LocationAgent(this.getAgentIDGenerator(), universe);
                xcor = i;
                ycor = j;
                locationAgent.setCoordinates(xcor, ycor);
                System.out.println("attraction:\t" + attraction);
                locationAgent.agentAttributes.addAttribute("attraction", attraction);
                locationAgent.agentAttributes.addAttribute("maxattraction", maxAttraction);
                //decide if this parameter should be set here or add a new method for spreading smoothness
                locationAgent.agentAttributes.addAttribute("smoothness", attraction * 0.4);
                agents.put(locationAgent.getAID(), locationAgent);
                agents.put(locationAgent.getAID(), locationAgent);
                universe.place(xcor, ycor, locationAgent.getAID());
            }
        }
        Log.logger.info("location agents is setup.");
    }

    private void setupUniverse() {
        universe = new UrbanSprawlSimulation.UrbanSprawlUniverse(new Integer(simulationProperties.getProperty("maxx")),
                new Integer(simulationProperties.getProperty("maxy")));
    }

    public static void main(String[] args) {
        UrbanSprawlSimulation simulation;
        try {
            simulation = new UrbanSprawlSimulation();
            simulation.runAC();
        } catch (Exception ex) {
            Logger.getLogger(UrbanSprawlSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
