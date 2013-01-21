/*
 * Old logger file that will be removed to include the one developed by murali.
 */
package System;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 * System wide Logger
 */
public class Log {
    /**
     * Logger, level set at info
     */
    public static Logger logger = Logger.getRootLogger();

    /**
     * Configure the logger. To be called before anything else
     */
    public static void ConfigureLogger() {
        
        PropertyConfigurator.configure("src/Utilities/logger.properties");     
        logger.setLevel(Level.INFO);
        logger.info("logging initialized");
    }

}