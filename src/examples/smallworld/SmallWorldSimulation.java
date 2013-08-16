/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.smallworld;

import agents.Agent;
import agents.AgentController;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import system.Log;

/**
 *
 * @author murali
 */
public class SmallWorldSimulation extends AgentController {

    Properties simulationProperties;
    Map<UUID, NodeAgent> agentMap;
    int numberOfNodes;
    Double rewiringProbability;
    double clusteringCoefficiency;
    double averagePathLength;
    double initialClusteringCoefficiency;
    double initialAveragePathLength;
    int infinity;
    Integer numberRewired;
    

    public Agent getAgent(UUID uuid) {
        return agentMap.get(uuid);
    }

    public int getTickNumber() {
        return currentTickNumber;
    }

    public SmallWorldSimulation() {
        super();
        agentMap = Collections.synchronizedMap(new HashMap<UUID, NodeAgent>());
        this.setAgentControllerName(this.getClass().getCanonicalName());
        readConfigurations();
        addQueueListener();
        system.Log.ConfigureLogger();
        buildACStatus();
    }

    @Override
    protected void cleanUp() {
        sendDoneWithWork();
        System.exit(0);

    }

    @Override
    protected void setUp() {
        try {
            simulationProperties = new Properties();
            simulationProperties.load(new FileInputStream("config/examples_properties/smallworld.properties"));
            setUpSimulationParameters();
            setUpAgents();

            sendReadyForTick();
            Log.logger.info("simulation is setup");
        } catch (IOException iOException) {
            Logger.getLogger(SmallWorldSimulation.class.getName()).log(Level.SEVERE, null, iOException);
        }
    }

    public void setUpSimulationParameters() {
        numberOfNodes = new Integer(simulationProperties.getProperty("nodes"));
        rewiringProbability = new Double(simulationProperties.getProperty("rewiringprobability"));
        infinity = new Integer(simulationProperties.getProperty("infinity"));
        numberRewired=new Integer(0);
    }

    public void setupInitialParameters() {
    }

    public void setUpAgents() {
        for (int i = 0; i < numberOfNodes; i++) {
            //create those many node agents
            NodeAgent tempNodeAgent = new NodeAgent(this.getAgentIDGenerator(),agentMap,rewiringProbability,numberRewired);
            tempNodeAgent.agentAttributes.addAttribute("distanceToOtherNodes", new HashMap<UUID, Integer>());
            tempNodeAgent.agentAttributes.addAttribute("neighborHood", new HashMap<UUID,Boolean>());
            tempNodeAgent.agentAttributes.addAttribute("nodeClusteringCoefficient", new Double(0));            
            tempNodeAgent.agentAttributes.addAttribute("numberOfNeighbours", new Integer(0));
            tempNodeAgent.agentAttributes.addAttribute("rewired", new Boolean(Boolean.FALSE));
            //tempNodeAgent.agentAttributes.addAttribute("numberOfNodes", numberOfNodes);
            agents.add(tempNodeAgent);
            agentMap.put(tempNodeAgent.getAID(), tempNodeAgent);
        }
        //create lattice
        createLattice(agents);
    }

    public void createLattice(List<Agent> agents) {
        for (int i = 0; i < numberOfNodes; i++) {
            //connect next two neighbour nodes
            makeEdge((NodeAgent) agents.get(i), (NodeAgent) agents.get((i + 1) % numberOfNodes));
            makeEdge((NodeAgent) agents.get(i), (NodeAgent) agents.get((i + 2) % numberOfNodes));
            
        }
    }

    public void makeEdge(NodeAgent nodeAgent1, NodeAgent nodeAgent2) {
        //connect node agent 1 to node agent2
        
        HashMap<UUID,Boolean> agentNeighborHood=(HashMap<UUID,Boolean>) nodeAgent1.agentAttributes.getAttribute("neighborHood");
        agentNeighborHood.put(nodeAgent2.getAID(), Boolean.TRUE);
        nodeAgent1.agentAttributes.addAttribute("neighborHood", agentNeighborHood);
        Boolean rewired =(Boolean) nodeAgent1.agentAttributes.getAttribute("rewired");
        rewired=Boolean.FALSE;
        nodeAgent1.agentAttributes.addAttribute("rewired", rewired);
    }
 
    public NodeAgent getNodeAgent(UUID uuid){
        return agentMap.get(uuid);
    }
    
    public double getRewiringProbability(){
        return rewiringProbability;
    }
    
    public static void main(String[] args) throws Exception {
        SmallWorldSimulation sws = new SmallWorldSimulation();
        if(sws.agentMap.size()!=sws.numberOfNodes){
            //setup again all the agents
            sws.setUp();
        }else{
            sws.runAC();
        }
        
    }
}// end of small world simulation
