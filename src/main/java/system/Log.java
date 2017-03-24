/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package system;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This is the Logging system Class that generates a logger instance for
 * anything that requires to be logged in the system. The Logger used if
 * Apache's log4j 1.2.17
 * TODO: Set up separate files for logging different parts of Phoenix in the logger settings.
 */
public class Log {

    /**
     * Default logger to dump everything.
     */
    public static Logger logger;

    /**
     * Configure the logger based on the configuration file provided in the
     * "config" directory.
     */
    public static void ConfigureLogger() {
        PropertyConfigurator.configure("src/main/resources/config/logger.properties");
        logger = Logger.getLogger("phoenixLogger"); //Note Root Logger only set to console
    }
}
