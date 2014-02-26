/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.rumourmill;

import agents.Agent;
import agents.AgentController;
import agents.universe.Universe2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
public class RumourMillSimulation extends AgentController{
    Properties simulationProperties;
    RumourMillUniverse universe;

    @Override
    protected void cleanupBeforeNextTick() {
        universe.worldView();
    }
    
    public class RumourMillUniverse extends Universe2D{

        public RumourMillUniverse(int maxx,int maxy) {
            super(maxx, maxy, 0, 0);
            Log.logger.info("Universe is setup.");
        }
        
        @Override
        public void worldView(){
            for(int i=0;i<maxX;i++){
                for(int j=0;j<maxY;j++){
                    System.out.print("[");
                    ArrayList<UUID> agentSet=(ArrayList<UUID>) world[i][j];
                    for(UUID u:agentSet){
                        System.out.print(agents.get(u).agentAttributes.getAttribute("TimesHeard")+":"+
                                agents.get(u).agentAttributes.getAttribute("FirstHeard"));
                    }
                    System.out.print("]");
                }
                System.out.println();
            }
            System.out.println();
        }
        
        public Agent getAgent(UUID uuid) {
            return agents.get(uuid);
        }
        
        public int getTickNumber(){
            return currentTickNumber;
        }
        
    }
    
    public RumourMillSimulation(){
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
            simulationProperties=new Properties();
            simulationProperties.load(new FileInputStream("config/examples_properties/rumourmill.properties"));
            setupUniverse();
            setupAgents();
            sendReadyForTick();
            Log.logger.info("Simulation is setup.");
            universe.worldView();
        } catch (IOException ex) {
            Logger.getLogger(RumourMillSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setupUniverse(){
        universe=new RumourMillUniverse(new Integer(simulationProperties.getProperty("maxx")), 
                new Integer(simulationProperties.getProperty("maxy")));
    }
    
    public void setupAgents(){
        for(int i=0;i<universe.maxX;i++){
            for(int j=0;j<universe.maxY;j++){
                RumourAgent temp=new RumourAgent(this.getAgentIDGenerator(), universe);
                temp.agentAttributes.addAttribute("TimesHeard", new Integer(0));
                temp.agentAttributes.addAttribute("FirstHeard", new Integer(-1));
                temp.agentAttributes.addAttribute("xcor", new Integer(i));
                temp.agentAttributes.addAttribute("ycor", new Integer(j));
                agents.put(temp.getAID(),temp);
                universe.place(i, j, temp.getAID());
            }
        }
        Log.logger.info("Agents setup with TimesHeard 0.");
        //Assign random agent to be the first guy to have the rumour
        int xcor=new Random().nextInt(universe.maxX);
        int ycor=new Random().nextInt(universe.maxY);
        ArrayList<UUID> agentList=(ArrayList<UUID>) universe.world[xcor][ycor];
        for(UUID u:agentList){
            agents.get(u).agentAttributes.addAttribute("TimesHeard", new Integer(1));
            agents.get(u).agentAttributes.addAttribute("FirstHeard", new Integer(0));
        }
    }
    
    public static void main(String args[]){
        RumourMillSimulation rms=new RumourMillSimulation();
        rms.runAC();
    }
}
