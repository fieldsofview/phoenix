/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.wolfsheep;

import agents.AIDGenerator;
import agents.Agent;
import system.Log;

/**
 *
 * @author onkar
 */
public class GrassAgent extends Agent{
    private int xcor;
    private int ycor;
    public GrassAgent(AIDGenerator aidGenerator){
        super(aidGenerator);
        Log.ConfigureLogger();
    }
    @Override
    public void run() {
        this.setStatusFlag(true);
        this.setObjectiveFlag(true);
    }
    public void setCoordinates(int xcor,int ycor){
        this.xcor=xcor;
        this.ycor=ycor;
    }
}
