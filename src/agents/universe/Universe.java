/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package agents.universe;

/**
 * The different types of spaces that the agents can reside within. This is not
 * essential to run an agent but interactions can be defined through the
 * universe.
 */
public class Universe {
	private String universeName;

	/**
	 * @return the universeName
	 */
	public String getUniverseName() {
		return universeName;
	}

	/**
	 * @param universeName
	 *            the universeName to set
	 */
	public void setUniverseName(String universeName) {
		this.universeName = universeName;
	}

}
