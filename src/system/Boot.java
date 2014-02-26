/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package system;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import communication.ACNetwork;
import communication.QueueParameters;

/**
 * The Boot Class sets up the start of a simulation process. This class contains
 * methods to perform checks for modules.
 */

/**
 * This class loads the configurations for a given agent controller.
 */
public class Boot {

    public Boot() {
        //TODO: Calling in AC process already. Remove the next line.
        //Log.ConfigureLogger();
    }

    // TODO: Modify this function to include code for reading the queue
    // parameters for the agent to agent communication as well.

    /**
     * Read the configurations for the machine. Each machine runs a
     * AgentController. AgentController handles communication and agent
     * behaviour. The file is a standard java properties file. The file
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws NumberFormatException
     */
    public static void readMachineConfigurations() throws IOException,
            FileNotFoundException, NumberFormatException {

        Properties queueProperties = new Properties();

        queueProperties.load(new FileInputStream(
                system.Constants.machineFile));

        Log.logger.debug("Agent Controller Name:" + queueProperties.getProperty("AgentControllerName"));

        Log.logger.debug("List of AC Queues:" + queueProperties.getProperty("ACHostQueues"));

        Log.logger.debug("HostIP:" + queueProperties.getProperty("hostIP"));

        Log.logger.debug("Queue Host IP:" + queueProperties.getProperty("queueHostIP"));

        Log.logger.debug("Queue Name:"
                + queueProperties.getProperty("queueName"));
        Log.logger
                .debug("Username: " + queueProperties.getProperty("username"));
        Log.logger
                .debug("Password: " + queueProperties.getProperty("password"));
        Log.logger.debug("Virtual Host: "
                + queueProperties.getProperty("virtualHost"));
        Log.logger.debug("Port: " + queueProperties.getProperty("port"));
        Log.logger.debug("Exchange Name: "
                + queueProperties.getProperty("exchangeName"));
        Log.logger.debug("Routing Key: "
                + queueProperties.getProperty("routingKey"));

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
        Log.logger.debug("Number of ACs:"+listOfACs.length);

        /*Add all the ACs to the list*/
        Log.logger.debug("Adding the AC List");
        for (String givenAC : listOfACs) {
            Log.logger.debug(givenAC + "\n");
            ACNetwork.agentControllerHostList.add(givenAC);
        }
        //Add myself to this list.
        //ACNetwork.agentControllerHostList.add(ACNetwork.ACName);
        //ACNetwork.agentControllerHostList.add(ACNetwork.localhost);
        Log.logger.debug("Size of agentControllerHostList:" + ACNetwork.agentControllerHostList.size());

        ACNetwork.hostMessageQueueLookup.put(
                queueProperties.getProperty("queueName"), queueParameters);
        ACNetwork.ACMessageQueueParameters = queueParameters;
    }
}
