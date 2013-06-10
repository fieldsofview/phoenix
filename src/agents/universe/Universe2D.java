/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package agents.universe;

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
	public long maxX, maxY, minX, minY;

	public Universe2D() {
		// TODO : Put logger code here

		this.maxX = 0;
		this.maxY = 0;
		this.minX = 0;
		this.minY = 0;
	}

	public Universe2D(long maxx, long maxy, long minx, long miny) {
		this.maxX = maxx;
		this.maxY = maxy;
		this.minX = minx;
		this.minY = miny;
	}
}
