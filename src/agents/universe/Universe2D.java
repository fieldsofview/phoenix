/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package agents.universe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * This class implements a two dimensional space in which the agent exists.
 * 
 * @version 0.1
 */
public class Universe2D extends Universe {

	/**
	 * @param maxX
	 *            is the maximum value that the X ordinate can take.
	 * @param maxY
	 *            is the maximum value that the Y ordinate can take.
	 * @param minX
	 *            is the minimum value that the X ordinate can take.
	 * @param minY
	 *            is the minimum value that the Y ordinate can take.
	 */
	public int maxX, maxY, minX, minY;
        public Object[][] world;
        
	public Universe2D() {
            // TODO : Put logger code here

            this.maxX = 0;
            this.maxY = 0;
            this.minX = 0;
            this.minY = 0;
	}

	public Universe2D(int maxx, int maxy, int minx, int miny) {
            this.maxX = maxx;
            this.maxY = maxy;
            this.minX = minx;
            this.minY = miny;
            //TODO: Currently minx and miny are 0,0. Need to change 
            //to allow lesser values as well
            world = new Object[this.maxX][this.maxY];
            for(int i=0;i<maxX;i++){
                for(int j=0;j<maxY;j++){
                    world[i][j]=new ArrayList<UUID>();
                }
            }
	}
        
        /**
         * The method worldView outputs onto the console
         * the current locations of all the agents within the world
         */
        public void worldView(){
            for(int i=0;i<maxX;i++){
                for(int j=0;j<maxY;j++){
                    System.out.print(world[i][j]);
                }
                System.out.println("\n");
            }
        }
        
        /**
         * This method places the agent with UUID uuid
         * at the coordinates x and y
         * @param x
         * The x-coordinate for placing
         * @param y
         * The y-coordinate for placing
         * @param uuid 
         * The uuid of the agent to be placed
         */
        synchronized public void place(int x, int y, UUID uuid){
            ArrayList temp=(ArrayList) world[x][y];
            temp.add(uuid);
            world[x][y]=temp;
        }
        
        /**
         * This method removed the agent with UUID uuid
         * from the coordinates x and y
         * @param x
         * The x-coordinate
         * @param y
         * The y-coordinate
         * @param uuid 
         * The uuid of the agent to be removed
         */
        synchronized public void remove(int x, int y, UUID uuid){
            ArrayList temp=(ArrayList) world[x][y];
            temp.remove(uuid);
            world[x][y]=temp;
        }
}
