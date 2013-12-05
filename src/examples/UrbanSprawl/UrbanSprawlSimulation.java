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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
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
    HashMap<String, Double> resultSet = new HashMap<String, Double>();
    Connection connection = null;
    Statement statement = null;

    @Override
    protected void cleanupBeforeNextTick() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        universe.worldView();
        if (this.currentTickNumber == 500) {
            Set<UUID> agentsKeySet = agents.keySet();
            for (UUID uuid : agentsKeySet) {
                Log.logger.info("Killing agent " + uuid);
                agents.get(uuid).setObjectiveFlag(true);
            }

        }
    }

    private void getLatestSimulationData() throws SQLException {
        databaseModule.query = "select * from simulation_properties where status=0 order by simulation_properties_id desc limit 1";
        ResultSet execute = databaseModule.execute();
        while (execute.next()) {
            //resultSet.put("simulation_name", execute.getString("simulation_name"));
            resultSet.put("simulation_agents_count", new Double(execute.getString("simulation_agents_count")));
            resultSet.put("max_x", new Double(execute.getString("max_x")));
            resultSet.put("max_y", new Double(execute.getString("max_y")));
            resultSet.put("smoothness", new Double(execute.getString("smoothness")));
            resultSet.put("attraction", new Double(execute.getString("attraction")));
            resultSet.put("search_angle", new Double(execute.getString("search_angle")));
            resultSet.put("patience", new Double(execute.getString("patience")));
            resultSet.put("max_attraction", new Double(execute.getString("max_attraction")));
            resultSet.put("waittime", new Double(execute.getString("waittime")));
            resultSet.put("simulation_id", new Double(execute.getString("simulation_properties_id")));
        }

    }

    public class UrbanSprawlUniverse extends Universe2D {

        private UrbanSprawlUniverse(Integer maxX, Integer maxY) {
            super(maxX, maxY, 0, 0);
            Log.logger.info("Universe is setup.");
        }

        @Override
        public void worldView() {
            String insertQuery = "";
            String opData = "";
            String output = "";
            StringBuilder opString = new StringBuilder();
            for (int i = 0; i < maxX; i++) {
                for (int j = 0; j < maxY; j++) {
                    ArrayList<UUID> setOfAgents = (ArrayList<UUID>) world[i][j];
                    if (!setOfAgents.isEmpty() && setOfAgents.size() != 0) {
                        for (UUID uuid : setOfAgents) {
                            if (agents.get(uuid).agentAttributes.getAttribute("oldx") != null) {
//                                if ((agents.get(uuid).agentAttributes.getAttribute("oldx") != agents.get(uuid).agentAttributes.getAttribute("xcor") )&&( agents.get(uuid).agentAttributes.getAttribute("oldy") != agents.get(uuid).agentAttributes.getAttribute("ycor"))) {                                    
                                insertQuery = "INSERT INTO simulation_results(simulation_id,simulation_results_tick,simulation_results_change) VALUES";
                                //System.out.print("[" + agents.get(uuid).agentAttributes.getAttribute("oldx") + "," + agents.get(uuid).agentAttributes.getAttribute("oldy") + "]:[" + agents.get(uuid).agentAttributes.getAttribute("xcor") + "," + agents.get(uuid).agentAttributes.getAttribute("ycor") + "]");
                                //opData += "[" + agents.get(uuid).agentAttributes.getAttribute("oldx") + "," + agents.get(uuid).agentAttributes.getAttribute("oldy") + "]:[" + agents.get(uuid).agentAttributes.getAttribute("xcor") + "," + agents.get(uuid).agentAttributes.getAttribute("ycor") + "]";
                                opString.append("[" + agents.get(uuid).agentAttributes.getAttribute("xcor") + "," + agents.get(uuid).agentAttributes.getAttribute("ycor") + "],");
                                opData += "[" + agents.get(uuid).agentAttributes.getAttribute("xcor") + "," + agents.get(uuid).agentAttributes.getAttribute("ycor") + "],";
                            }

                        }
                    }
                }
                //System.out.println("\n");
            }
            if (opString.length() > 2) {
                //opString.insert(0, "[");
                //opString.append("]");
                //System.out.println(opString.charAt(opString.length() - 1));
                opString.deleteCharAt(opString.length() - 1);
                insertQuery += "(" + resultSet.get("simulation_id") + ",NOW()," + "'" + opString + "');";
                databaseModule.updateQuery = insertQuery;
                //System.out.println(insertQuery);
                databaseModule.executeUpdate();
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
    protected void shutdown() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        sendDoneWithWork();
        queueManager.exitMessaging();
        String query = "select simulation_properties_id from simulation_properties where status= 0 order by simulation_properties_id desc limit 1;";
        try {
            connection = UrbanSprawlSimulation.getLocalConnection();
            statement = connection.createStatement();
            ResultSet resultSet = null;
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                Integer simulation_id = new Integer(resultSet.getString("simulation_properties_id"));
                query = "UPDATE simulation_properties SET status=2 WHERE simulation_properties_id=" + simulation_id;
                int updated = statement.executeUpdate(query);
                Log.logger.info("Clean up done");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UrbanSprawlSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(UrbanSprawlSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
        Log.logger.info("Simulation has ended");
    }

    @Override
    protected void setUp() {
        try {
            simulationProperties = new Properties();
            simulationProperties.load(new FileInputStream("config/examples_properties/urbansprawl.properties"));
            databaseModule = new DatabaseModule();
            getLatestSimulationData();
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
        } catch (SQLException ex) {
            Logger.getLogger(UrbanSprawlSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void setUpUrbanAgents() {
        int xcor;
        int ycor;
        //int noOfUrbanAgents = new Integer(simulationProperties.getProperty("population"));
        int noOfUrbanAgents = new Integer(resultSet.get("simulation_agents_count").intValue());
        for (int i = 0; i < noOfUrbanAgents; i++) {
            UrbanAgent urbanAgent = new UrbanAgent(this.getAgentIDGenerator(), universe);
            xcor = universe.maxX / 2;
            ycor = universe.maxY / 2;
            //xcor=0;ycor=0;
            urbanAgent.setCoordinates(xcor, ycor);
            urbanAgent.agentAttributes.addAttribute("searchangle", new Integer(resultSet.get("search_angle").intValue()));
            urbanAgent.agentAttributes.addAttribute("patience", new Integer(resultSet.get("patience").intValue()));
            urbanAgent.agentAttributes.addAttribute("waittime", new Integer(resultSet.get("waittime").intValue()));
            urbanAgent.agentAttributes.addAttribute("agentState", new Integer(1));
            urbanAgent.agentAttributes.addAttribute("xcor", new Integer(xcor));
            urbanAgent.agentAttributes.addAttribute("ycor", new Integer(ycor));
            urbanAgent.agentAttributes.addAttribute("oldx", new Integer(xcor));
            urbanAgent.agentAttributes.addAttribute("oldy", new Integer(ycor));
            urbanAgent.agentAttributes.addAttribute("patienceCounter", new Integer(resultSet.get("patience").intValue()));
            urbanAgent.agentAttributes.addAttribute("patienceFlag", Boolean.TRUE);
            agents.put(urbanAgent.getAID(), urbanAgent);
            universe.place(xcor, ycor, urbanAgent.getAID());
        }
        Log.logger.info("urban agents are setup.");
    }

    private void setUpLocation() {
        int xcor;
        int ycor;
        //int smoothNess = new Integer(simulationProperties.getProperty("smoothness"));
        //int maxAttraction = new Integer(simulationProperties.getProperty("maxattraction"));
        int smoothNess = new Integer(resultSet.get("smoothness").intValue());
        int maxAttraction = new Integer(resultSet.get("max_attraction").intValue());
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
        universe = new UrbanSprawlSimulation.UrbanSprawlUniverse(new Integer(resultSet.get("max_x").intValue()),
                new Integer(resultSet.get("max_y").intValue()));
    }

    private static Connection getLocalConnection() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        Statement statement = null;
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/phoenix_simulations", "root", "vamshi511");
        return connection;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Connection connection = null;
        UrbanSprawlSimulation test = null;
        Statement statement = null;
        String query = "";
        ResultSet resultSet = null;
        UrbanSprawlSimulation simulation;
        Boolean newSimulation = Boolean.FALSE;
        Boolean currentSimulation = Boolean.FALSE;
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            //connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/phoenix_simulations", "root", "vamshi511");
            connection = UrbanSprawlSimulation.getLocalConnection();
            statement = connection.createStatement();
            while (true) {
                query = "select status from simulation_properties where status= 0 order by simulation_properties_id  desc ;";
                resultSet = statement.executeQuery(query);
                int count = 0;
                while (resultSet.next()) {
                    count++;
                }

                if (count!=0) {
                    System.out.println("New simulation started");
                    test = new UrbanSprawlSimulation();
                    test.runAC();
                } else {
                    System.out.println("Waiting for simulation input...");
                    continue;
                }

            }

        } catch (SQLException exception) {
            //Log.logger.info(exception.getMessage());
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }

    }
}
