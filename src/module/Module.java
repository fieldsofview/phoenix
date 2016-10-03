/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package module;

/**
 * @author onkar
 * @version 0.1 Date of creation: 1 April 2013 This is the module interface. All
 *          Phoenix modules must implement this interface. Custom methods for
 *          each module must be provided by the module.
 */
public interface Module {
	/**
	 * This method will boot the module. It should check for other module
	 * dependencies.
	 */
	void boot();

	/**
	 * This method will run the initialisation script for the module.
	 */
	void initialise();
}
