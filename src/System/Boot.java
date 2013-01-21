package System;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import Communication.ACNetwork;
import Communication.QueueParameters;

/**
 * This class loads the configurations for a given agent controller.
 */
//TODO : Set up Logger everywhere
public class Boot {

    /**
     * Loads the configurations for the machine. Each machine runs a 
     * AgentController. AgentControllerhandles communication and 
     * agent behaviour.
     *
     * @param name File name containing the care taker agent configuration.
     * @throws IOException
     * @throws FileNotFoundException
     * @throws NumberFormatException
     */
    public static void loadMachineConfigurations(String name) throws IOException, FileNotFoundException, NumberFormatException {

        BufferedReader inputFile;
        inputFile = new BufferedReader(new FileReader(name));
        String inputLine;

        while ((inputLine = inputFile.readLine()) != null) {
            String[] param = inputLine.split(":");
            int type = Integer.parseInt(param[0]);
            ACNetwork.agentControllerhostList.add(param[1]);
            QueueParameters queueParameters = new QueueParameters(param[2], param[3], param[4], param[5], param[6], param[7], param[8]);

            ACNetwork.hostMessageQueueLookup.put(param[1], queueParameters);

            //Build a directory of hosts based on their type for communication.
            Map<Integer, ArrayList<String>> hostMap = Communication.ACNetwork.hostTypeMap;
            ArrayList<String> hosts = new ArrayList<String>(); //The list of hosts of a certain type.
            if (hostMap.containsKey(type)) {
                hosts = hostMap.get(type); //If the type is already present append arraylist.
            }
            //If not add a new map entry.
            hosts.add(param[1]);
            hostMap.put(type, hosts);
        }

        //Close the file
        inputFile.close();
    }
}
