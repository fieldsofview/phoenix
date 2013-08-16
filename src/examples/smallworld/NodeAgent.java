/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.smallworld;

import agents.AIDGenerator;
import agents.Agent;
import agents.attributes.AgentAttributes;
import agents.behaviour.Behaviour;
import agents.behaviour.CompositeBehaviour;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import system.Log;

/**
 *
 * @author murali
 */
public class NodeAgent extends Agent{
    
    CompositeBehaviour behaviour;    
    Map<UUID,NodeAgent> agentMap;
    //simulation parameters, contained in every agent
    Double rewiringProbability;
    Integer numberRewired;
   
    public void doAgentCalculations(Object[] agents){
        ArrayList<NodeAgent> nodeAgentList = new ArrayList<NodeAgent>();
        for(Object tempAgent: agents){
            nodeAgentList.add((NodeAgent)tempAgent);
        }
      boolean connected = true;
      int infinity=999;
      double averagePathLength=0;
        findNodePathLength(nodeAgentList);
        int numberOfConnectedNodes = 0;
        numberOfConnectedNodes = findNumberOfConnectedNodes(nodeAgentList);
        //if (numberOfConnectedNodes != agents.length * (agents.length - 1)) {
        if (numberOfConnectedNodes != agents.length) {
            averagePathLength = infinity;
            connected = false;
        } else {
            for (Object a : agents) { 
                HashMap<UUID,Integer> distancesToOtherNodes=(HashMap<UUID,Integer>) ((NodeAgent)a).agentAttributes.getAttribute("distanceToOtherNodes");
                for(Integer distance:distancesToOtherNodes.values()){
                    averagePathLength=averagePathLength+distance;
                }
                //averagePathLength = averagePathLength + (int)((NodeAgent)a).agentAttributes.getAttribute("distanceToOtherNodes");
                System.out.println("Node average path length for node:\t"+((NodeAgent)a).getAID()+"\t"+averagePathLength);
            }
            averagePathLength = averagePathLength / numberOfConnectedNodes;
            System.out.println("Average path length:\t"+averagePathLength);
        }
        findNodeClusteringCoefficient(nodeAgentList);  
    }
    
    public int findNodePathLength(List<NodeAgent> agents){
         int i = 0;
        int j = 0;
        int k = 0;
        int infinity=999;
        //int numberOfNodes=new Integer(simulationProperties.getProperty("nodes"));
        //while(i<numberOfNodes)
        while (i < agents.size()) {
            j = 0;
            while (j < agents.size()) {
                NodeAgent iAgent = agents.get(i);
                NodeAgent jAgent = agents.get(j);
                if (i == j) {
                    //if both iAgent and jAgnet are same then distance =0
                   
                    Map<UUID, Integer> distanceToOtherNodes;
                    distanceToOtherNodes = (HashMap<UUID, Integer>) iAgent.agentAttributes.getAttribute("distanceToOtherNodes");
                    distanceToOtherNodes.put(jAgent.getAID(), 0);
                    iAgent.agentAttributes.addAttribute("distanceToOtherNodes", distanceToOtherNodes);
                } else {
                    Map<NodeAgent, NodeAgent> connectedNodes = (Map<NodeAgent, NodeAgent>) iAgent.agentAttributes.getAttribute("neighborNode");
                    Map<UUID,Boolean>iAgentNeighborHood=(Map<UUID,Boolean>) iAgent.agentAttributes.getAttribute("neighborHood");
                    //check if iAgent is neighbor to jAgnet
                    //set distance to 1
                    if(!iAgentNeighborHood.isEmpty()){
                    if (iAgentNeighborHood.containsKey(jAgent.getAID())) {
                        //iAgent.agentAttributes.addAttribute("distanceToOtherNode"+jAgent, 1);
                        Map<UUID, Integer> distanceToOtherNodes;
                        distanceToOtherNodes = (HashMap<UUID, Integer>) iAgent.agentAttributes.getAttribute("distanceToOtherNodes");
                        distanceToOtherNodes.put(jAgent.getAID(), 1);
                        iAgent.agentAttributes.addAttribute("distanceToOtherNodes", distanceToOtherNodes);
                    } else {
                        //the distance will be some random constant or infinity                        
                        
                        Map<UUID, Integer> distanceToOtherNodes;
                        distanceToOtherNodes = (HashMap<UUID, Integer>) iAgent.agentAttributes.getAttribute("distanceToOtherNodes");
                        distanceToOtherNodes.put(jAgent.getAID(),infinity );
                        iAgent.agentAttributes.addAttribute("distanceToOtherNodes", distanceToOtherNodes);
                    }
                   }
                }// end of else
                j = j + 1;
            }
            i = i + 1;
        }
        i = 0;
        j = 0;
        int temp = 0;
        while (k < agents.size()) {
            i = 0;
            while (i < agents.size()) {
                j = 0;
                while (j < agents.size()) {
                    NodeAgent iAgent = agents.get(i);
                    NodeAgent jAgent = agents.get(j);
                    NodeAgent kAgent = agents.get(k);
                    //find alternate path through kth node
                    Map<UUID,Integer> iAgentMap=(HashMap<UUID,Integer>) iAgent.agentAttributes.getAttribute("distanceToOtherNodes");
                    Map<UUID,Integer> jAgentMap=(HashMap<UUID,Integer>) jAgent.agentAttributes.getAttribute("distanceToOtherNodes");
                    Map<UUID,Integer> kAgentMap=(HashMap<UUID,Integer>) kAgent.agentAttributes.getAttribute("distanceToOtherNodes");
                    temp=kAgentMap.get(jAgent.getAID())+iAgentMap.get(kAgent.getAID());
                    //if the alternate path is shorter
                    if (temp < iAgentMap.get(jAgent.getAID())) {
                        //replace the old distance with shorter one
                        iAgentMap.put(jAgent.getAID(), temp);
                        iAgent.agentAttributes.addAttribute("distanceToOtherNodes",iAgentMap);
                    }
                    j = j + 1;
                }
                i = i + 1;
            }
            k = k + 1;
        }
        return 0;        
        
    }
    
    public double findNodeClusteringCoefficient(List<NodeAgent> agents){
         int countOfNoNeighbors = 0;
        int countOfNodesWithNeighbors=0;
        double clusteringCoefficiency=0;
        for(NodeAgent a: agents){
            Map<UUID,Boolean>neighborHood=(HashMap<UUID,Boolean>) a.agentAttributes.getAttribute("neighborHood");
            if(neighborHood.containsValue(Boolean.TRUE)){
                countOfNodesWithNeighbors++;
                continue;
            }else{
                countOfNoNeighbors++;
            }
            if(countOfNoNeighbors<=1){
                clusteringCoefficiency=0;
            }
        }
         double total=0;
         
         //calculate node cluster coefficient
         for(NodeAgent a:agents){
             int numberOfNeighbours=0;
             Map<UUID,Boolean>nodeNeighborHood=(HashMap<UUID,Boolean>) a.agentAttributes.getAttribute("neighborHood");
             Double nodeClusteringCoefficient=(Double) a.agentAttributes.getAttribute("nodeClusteringCoefficient");
             Integer numberOfNeighbouts=(Integer) a.agentAttributes.getAttribute("numberOfNeighbours");
             if(!nodeNeighborHood.isEmpty()){
                for(Boolean value:nodeNeighborHood.values()){
                 if(value.equals(Boolean.TRUE)){
                     numberOfNeighbours++;
                     continue;
                 }
                 a.agentAttributes.addAttribute("numberOfNeighbours", numberOfNeighbours);
                }
                if(numberOfNeighbours<=1){
                    nodeClusteringCoefficient=0.0;
                    a.agentAttributes.addAttribute("nodeClusteringCoefficient", nodeClusteringCoefficient);
                }             
             }
         }
         
         for(NodeAgent a:agents){
             Double nodeClusteringCoefficient = 0.0;
             //a node in graph exists atleast with 1 link
             int linksInNeighBorHood=1;
             Map<UUID,Boolean>nodeNeighborHood=(HashMap<UUID,Boolean>) a.agentAttributes.getAttribute("neighborHood");
             if(nodeNeighborHood.size()>1){
                 for(UUID uuid:nodeNeighborHood.keySet()){
                     //find out if the agents are in neighbor hood both ways
                     //this code needs to be checked again
                     Agent agent=agentMap.get(uuid);
                     Map<UUID,Boolean> tempNodeNeighborHood=(Map<UUID,Boolean>) agent.agentAttributes.getAttribute("neighborHood");
                     if(tempNodeNeighborHood.containsKey(a.getAID())){
                      linksInNeighBorHood++;
                     }
                 }
                 nodeClusteringCoefficient=(2.0*linksInNeighBorHood)/((nodeNeighborHood.size()+1)*(nodeNeighborHood.size()));
                 System.out.println("Node clustering coefficiency for agent:\t"+a.getAID()+"\t"+nodeClusteringCoefficient);
             }
             total=total+nodeClusteringCoefficient;
         }
         
       
         if(countOfNodesWithNeighbors>1){
             clusteringCoefficiency=total/countOfNodesWithNeighbors;
         }
         System.out.println("Total clustering coefficieny:\t"+clusteringCoefficiency);
        return clusteringCoefficiency;
        
    }
    public int findNumberOfConnectedNodes(List<NodeAgent> agents) {
        int numberOfConnectedNodes = 0;
        for (NodeAgent a : agents) {
            //if ((int) a.agentAttributes.getAttribute("distanceToOtherNodes") != infinity && (int) a.agentAttributes.getAttribute("distanceToOtherNodes") != 0) {
              //  numberOfConnectedNodes = (int) a.agentAttributes.getAttribute("distanceToOtherNodes");
             Map<UUID, Integer> distanceToOtherNodes;
             distanceToOtherNodes = (HashMap<UUID, Integer>) a.agentAttributes.getAttribute("distanceToOtherNodes");
             if(distanceToOtherNodes.get(a.getAID())!=999 || distanceToOtherNodes.get(a.getAID())!=0) {
                 //numberOfConnectedNodes=numberOfConnectedNodes+distanceToOtherNodes.get(a.getAID());
                 numberOfConnectedNodes++;
             }
            //}
        }
        return numberOfConnectedNodes;

    }
    
    public int getNodesWithNeighbours(List<NodeAgent> agents){
        int countNodes=0;
        for(NodeAgent agent:agents){
            int numberOfNeighbours=(int) agent.agentAttributes.getAttribute("numberOfNeighbours");
            if(numberOfNeighbours>1)
                countNodes++;
        }
        return countNodes;
    }
    
    public void makeEdge(NodeAgent nodeAgent1, NodeAgent nodeAgent2) {
        //connect node agent 1 to node agent2
        
        HashMap<UUID,Boolean> agentNeighborHood=(HashMap<UUID,Boolean>) nodeAgent1.agentAttributes.getAttribute("neighborHood");
        agentNeighborHood.put(nodeAgent2.getAID(), Boolean.TRUE);
        nodeAgent1.agentAttributes.addAttribute("neighborHood", agentNeighborHood);
        Boolean rewired =(Boolean) nodeAgent1.agentAttributes.getAttribute("rewired");
        rewired=Boolean.TRUE;
        nodeAgent1.agentAttributes.addAttribute("rewired", rewired);
    }
    
    public class NodeAgentAttributes extends AgentAttributes{
        public NodeAgentAttributes(){
            super();
        }
    }// end of attributes
    
    //Rewire doesn't look like a agent behavior
    //it looks like there is no behavior for agents except to update the aget attributes depending
    //on the progress in simulation
    public class RewireBehaviour implements Behaviour{

        @Override
        public void run(AgentAttributes agentAttributes) {
            Boolean success=Boolean.FALSE;   
            Map<UUID,NodeAgent> tempAgentMap=new HashMap<UUID, NodeAgent>();
            tempAgentMap=agentMap;            
            //while(!success){
                for(UUID uuid:agentMap.keySet()){
                    double random=Math.random();
                    NodeAgent agent=agentMap.get(uuid);
                    Boolean nodeRewired=(Boolean) agent.agentAttributes.getAttribute("rewired");
                    if(random<rewiringProbability && nodeRewired==Boolean.FALSE){                        
                        Map<UUID, Integer> distanceToOtherNodes;
                        distanceToOtherNodes = (HashMap<UUID, Integer>) agent.agentAttributes.getAttribute("distanceToOtherNodes");
                        HashMap<UUID,Boolean> agentNeighborHood=(HashMap<UUID,Boolean>) agent.agentAttributes.getAttribute("neighborHood");                        
                        //if agent is not connected to anyone
                        if(distanceToOtherNodes.size()<agentMap.size()-1){
                            //connect this agent to any random agent and which is not in neighbour
                            for(UUID uuid1:agentMap.keySet()){
                                if(!agentNeighborHood.containsKey(uuid1)){
                                    NodeAgent distantNodeAgent=agentMap.get(uuid1);
                                    //connect this to earlier agent
                                    makeEdge(agent, distantNodeAgent);
                                    //increment number rewired
                                    numberRewired++;                                   
                                    nodeRewired=Boolean.TRUE;
                                    agent.agentAttributes.addAttribute("rewired", nodeRewired);
                                }
                            }
                        }
                    }
                 doAgentCalculations(agentMap.values().toArray());
                }// end of for loop
                success=Boolean.TRUE;                
          //  }// end of while
        }//end of run
        
    }// end of person agent behavior
    
    
    public NodeAgent(AIDGenerator aIDGenerator,Map<UUID,NodeAgent>agentMap,Double rewiringProbability,Integer numberRewired){
        super(aIDGenerator);
        agentAttributes=new NodeAgentAttributes();
        behaviour=new CompositeBehaviour();
        this.agentMap=agentMap;
        this.rewiringProbability=rewiringProbability;
        this.numberRewired=numberRewired;
        RewireBehaviour rewireBehaviour=new RewireBehaviour();
        behaviour.add(rewireBehaviour);        
        Log.ConfigureLogger(); 
   }
    
    
   @Override
    public void run() {
        behaviour.run(agentAttributes);
        this.setStatusFlag(true);
        if((boolean)this.agentAttributes.getAttribute("rewired")==true){
            this.setObjectiveFlag(true);
        }
    }
}
