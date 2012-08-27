/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package System;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 *
 * @author jayanth
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
