/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import Agents.Agent;
import Communication.QueueManager;
import Database.DBAccess;
import System.AIDGenerator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;



/**
 *
 * @author Murali
 */
public class Log {


    public static Logger logger;


/**
 * 
 * @param object of a class
 * @return logger 
 * @description method to return logger based on the class object type
 */
    public static Logger returnLogger(Object object){
      
        if(object instanceof Agents.Agent){
            return Logger.getLogger(Agent.class);
        }else if(object instanceof  DBAccess) {
            return Logger.getLogger(DBAccess.class);
        }else if(object instanceof   AIDGenerator)
            return Logger.getLogger(AIDGenerator.class);
        else if(object instanceof QueueManager )
            return  Logger.getLogger(QueueManager.class);
        return null;
        
    }
    
        /**
     * Configure the logger. 
     */
    public static void ConfigureLogger() {
        
        PropertyConfigurator.configure("src/Utilities/logger.properties");     
        //TimeZone tz = TimeZone.getTimeZone("EST"); // or PST, MID, etc ...
        Date now = new Date();
        //DateFormat df = new SimpleDateFormat("yyyy.MM.dd hh:mm ");
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        
        String currentTime = df.format(now);

        FileAppender appender = null;
        PatternLayout p = new PatternLayout("%d %-5p [%t] %-17c{2} (%13F:%L) %3x - %m%n");
      //  logger.setLevel(Level.INFO);
        //logger.info("logging initialized");
    }

}