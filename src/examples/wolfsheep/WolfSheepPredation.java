/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.wolfsheep;

import agents.AgentController;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import system.Log;

/**
 *
 * @author onkar
 */
public class WolfSheepPredation extends AgentController {

    Properties simulationProperties;

    public WolfSheepPredation() {
        super();
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
            simulationProperties.load(new FileInputStream("config/examples/wolfsheep.properties"));
            setupWolfAgents();
            Log.logger.info("Simulation setup");
        } catch (IOException ex) {
            Logger.getLogger(WolfSheepPredation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        WolfSheepPredation wsp;
        try {
            wsp = new WolfSheepPredation();
            wsp.runAC();
        } catch (NumberFormatException e) {
            e.getMessage();
        }
    }

    private void setupWolfAgents() {
        for (int i = 0; i<new Integer(simulationProperties.getProperty("wolf")); i++) {
            WolfAgent wolf = new WolfAgent(this.getAgentIDGenerator());
            agents.add(wolf);
        }
    }
}
