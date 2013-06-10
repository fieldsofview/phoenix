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
		Log.ConfigureLogger();
	}

	// TODO: Modify this function to include code for reading the queue
	// parameters for the agent to agent communication as well.
	/**
	 * Read the configurations for the machine. Each machine runs a
	 * AgentController. AgentControllerhandles communication and agent
	 * behaviour. The file is a standard java properties file. The file
	 * 
	 * @param name
	 *            File name containing the care taker agent configuration.
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws NumberFormatException
	 */
	public static void readMachineConfigurations() throws IOException,
			FileNotFoundException, NumberFormatException {

		Properties queueProperties = new Properties();

		queueProperties.load(new FileInputStream(
				system.Constants.machineFile));

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
		Log.logger.debug("Host IP: " + queueProperties.getProperty("hostIP"));

		ACNetwork.agentControllerhostList.add(queueProperties
				.getProperty("hostIP"));
		QueueParameters queueParameters = new QueueParameters(
				queueProperties.getProperty("queueName"),
				queueProperties.getProperty("username"),
				queueProperties.getProperty("password"),
				queueProperties.getProperty("virtualHost"),
				queueProperties.getProperty("port"),
				queueProperties.getProperty("exchangeName"),
				queueProperties.getProperty("routingKey"));

		ACNetwork.hostMessageQueueLookup.put(
				queueProperties.getProperty("queueName"), queueParameters);
		ACNetwork.ACMessageQueueParameters = queueParameters;
	}
}
