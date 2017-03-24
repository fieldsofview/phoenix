/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package system;

import communication.ACNetwork;
import communication.QueueParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The Boot Class sets up the start of a simulation process. This class contains
 * methods to perform checks for modules.
 */

public class Boot {
    private final static Logger logger = LoggerFactory.getLogger(Boot.class);

    // TODO: Modify this function to include code for reading the queue parameters for the agent to agent communication as well.

    /**
     * Read the configurations for the machine. Each machine runs a
     * AgentController. AgentController handles communication and agent
     * behaviour. The file is a standard java properties file. The file
     *
     * @throws IOException           File not found or readable.
     * @throws NumberFormatException Unable to parse number data.
     */
    public static void readMachineConfigurations() throws IOException,
            NumberFormatException {

        Properties queueProperties = new Properties();
        queueProperties.load(new FileInputStream(system.Constants.machineFile));

        logger.debug("Agent Controller Name:" + queueProperties.getProperty("AgentControllerName"));
        logger.debug("List of AC Queues:" + queueProperties.getProperty("ACHostQueues"));
        logger.debug("HostIP:" + queueProperties.getProperty("hostIP"));
        logger.debug("Queue Host IP:" + queueProperties.getProperty("queueHostIP"));
        logger.debug("Queue Name:" + queueProperties.getProperty("queueName"));
        logger.debug("Username: " + queueProperties.getProperty("username"));
        logger.debug("Password: " + queueProperties.getProperty("password"));
        logger.debug("Virtual Host: " + queueProperties.getProperty("virtualHost"));
        logger.debug("Port: " + queueProperties.getProperty("port"));
        logger.debug("Exchange Name: " + queueProperties.getProperty("exchangeName"));
        logger.debug("Routing Key: " + queueProperties.getProperty("routingKey"));

        //ACNetwork.agentControllerHostList.add(queueProperties.getProperty("hostIP"));
        QueueParameters queueParameters = new QueueParameters(
                queueProperties.getProperty("queueHostIP"),
                queueProperties.getProperty("queueName"),
                queueProperties.getProperty("username"),
                queueProperties.getProperty("password"),
                queueProperties.getProperty("virtualHost"),
                queueProperties.getProperty("port"),
                queueProperties.getProperty("exchangeName"),
                queueProperties.getProperty("routingKey"));

        /* Note that as there is a dedicated RabbitMQ server present, the lcoalhost will
        point to the queue represented by the current instance of the AgentController. The old assumption
        of one AC per machine is no longer valid and routing is happening based on queue names and not
        IP Addresses or hostnames.
         */
        ACNetwork.localhost = queueProperties.getProperty("queueName");

        //ACNetwork.ACName = queueProperties.getProperty("AgentControllerName");

        String listOfACs[] = (queueProperties.getProperty("ACHostQueues")).split(",");
        logger.debug("Number of ACs:" + listOfACs.length);

        /*Add all the ACs to the list*/
        logger.debug("Adding the AC List");
        for (String givenAC : listOfACs) {
            logger.debug(givenAC + "\n");
            ACNetwork.agentControllerHostList.add(givenAC);
        }
        //Add myself to this list.
        //ACNetwork.agentControllerHostList.add(ACNetwork.ACName);
        //ACNetwork.agentControllerHostList.add(ACNetwork.localhost);
        logger.debug("Size of agentControllerHostList:" + ACNetwork.agentControllerHostList.size());

        ACNetwork.hostMessageQueueLookup.put( queueProperties.getProperty("queueName"), queueParameters);
        ACNetwork.ACMessageQueueParameters = queueParameters;
    }
}