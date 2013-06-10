/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package agents.universe;

/**
 * An example of other types of universes for the agents.
 * 
 * 
 */
public class UniversePolar2D extends Universe {
	private float universeRadius;

	/**
	 * @return the universeRadius
	 */
	public float getUniverseRadius() {
		return universeRadius;
	}

	/**
	 * @param universeRadius
	 *            the universeRadius to set
	 */
	public void setUniverseRadius(float universeRadius) {
		this.universeRadius = universeRadius;
	}
}
