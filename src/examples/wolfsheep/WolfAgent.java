/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.wolfsheep;

import agents.AIDGenerator;
import system.Log;

public class WolfAgent extends agents.Agent {

    public WolfAgent(AIDGenerator aidGenerator) {
        super(aidGenerator);
        Log.ConfigureLogger();
    }

    @Override
    public void run() {
        Log.logger.info("Running agent " + this.getAID());
        this.setStatusFlag(true);
        this.setObjectiveFlag(true);
    }
}