/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package system;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This is the Logging system Class that generates a logger instance for
 * anything that requires to be logged in the system. The Logger used if
 * Apache's log4j 1.2.17
 */

public class Log {

	public static Logger logger = Logger.getRootLogger();

	/*
	 * TODO:// Write a on the fly log files generator.
	 * TODO:// Pass the AgentController Name to the log file generation.
	 */

	/**
	 * Configure the logger based on the configuration file provided in the
	 * "config" directory.
	 */
	public static void ConfigureLogger() {
		PropertyConfigurator.configure("config/logger.properties");
                logger.setLevel(Level.DEBUG);
		// TimeZone tz = TimeZone.getTimeZone("IST");
	}
}
