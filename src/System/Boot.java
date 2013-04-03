/**
 * The Boot Class sets up the start of a simulation process. This class contains
 * methods to perform checks for modules.
 */
package System;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import Communication.ACNetwork;
import Communication.QueueParameters;

/**
 * This class loads the configurations for a given agent controller.
 */
public class Boot {

	public Boot() {
		// TODO: Write the Log System initialization here.
		Log.ConfigureLogger();
	}

	/**
	 * Loads the configurations for the machine. Each machine runs a
	 * AgentController. AgentControllerhandles communication and agent
	 * behaviour.
	 * 
	 * @param name
	 *            File name containing the care taker agent configuration.
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws NumberFormatException
	 */
	public static void loadMachineConfigurations(String name)
			throws IOException, FileNotFoundException, NumberFormatException {

		BufferedReader inputFile;
		inputFile = new BufferedReader(new FileReader(name));
		String inputLine;

		while ((inputLine = inputFile.readLine()) != null) {
			String[] param = inputLine.split(":");
			// int type = Integer.parseInt(param[0]);
			ACNetwork.agentControllerhostList.add(param[1]);
			QueueParameters queueParameters = new QueueParameters(param[2],
					param[3], param[4], param[5], param[6], param[7], param[8]);

			ACNetwork.hostMessageQueueLookup.put(param[1], queueParameters);

			// TODO: This is the new code. Only queue parameters need to be
			// stored.
			ACNetwork.ACMessageQueueParameters = queueParameters;
			//TODO: Write the code for the agent queue message.

		}
		// Close the file
		inputFile.close();
	}
}
