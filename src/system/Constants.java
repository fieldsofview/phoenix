/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package system;

/**
 * This class contains constant values that will be used throughout the
 * simulation and will not change.
 */
public interface Constants {

	/**
	 * Throughout the local system IP address
	 */
	/**
	 * TODO: Replace this with a function to find the local system IP using a
	 * function.
	 */
	public final String localHost = "127.0.0.1";
	/**
	 * The file name for communication settings
	 */
	public final String machineFile = "config/machineConfig";
}