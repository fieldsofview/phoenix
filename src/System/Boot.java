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
 * This class loads the configurations. The data for individual agents, the
 * configuration for the area of interest, the various facilities in the area of
 * interest.
 */
//TODO : Set up Logger everywhere
public class Boot {

    /**
     * Load the configurations of location of interest.
     *
     * @param name file name
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NumberFormatException
     */
    public static void loadAgentConfigurations(String name) throws FileNotFoundException, IOException, NumberFormatException {

        BufferedReader inputFile;
        inputFile = new BufferedReader(new FileReader(name));

        //First line is number of agents
        String inputLine = inputFile.readLine();
        SharedData.numberOfAgents = Integer.parseInt(inputLine);
        System.Log.logger.info("Read Config number of agents = " + SharedData.numberOfAgents);

        BoundingBox boundingBox = new BoundingBox();

        //Second line is the Latitude for NW
        inputLine = inputFile.readLine();
        boundingBox.nw.x = Double.parseDouble(inputLine);

        //Third line is the Longitude for NW
        inputLine = inputFile.readLine();
        boundingBox.nw.y = Double.parseDouble(inputLine);

        //Fourth line is the Latitude for SE
        inputLine = inputFile.readLine();
        boundingBox.se.x = Double.parseDouble(inputLine);

        //Fifth line is the Latitude for NW
        inputLine = inputFile.readLine();
        boundingBox.se.y = Double.parseDouble(inputLine);

        //Sixth Line is the number of Person Care Taker agents
        inputLine = inputFile.readLine();
        SharedData.numberOfPeopleCTA = Integer.parseInt(inputLine);


        //add the bounding box to shared data
        SharedData.boundingBox = boundingBox;

        // Close the input file
        inputFile.close();
    }

    /**
     * Loads the configurations for the machine. Each machine runs a care taker
     * agent. A care taker agent handles communication and agent behaviour.
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

        //All all the communication details to the global data structure The agents will then contact the individual hosts
        while ((inputLine = inputFile.readLine()) != null) {
            String[] param = inputLine.split(":");
            int type = Integer.parseInt(param[0]);
            ACNetwork.hosts.add(param[1]);
            QueueParameters queueParameters = new QueueParameters(param[2], param[3], param[4], param[5], param[6], param[7], param[8]);


            ACNetwork.hostQueueMap.put(param[1], queueParameters);

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

    /**
     * Loads the configurations for the hospital facilities that are present in
     * the area of interest.
     *
     * @param name filename for the hospital configuration.
     * @throws IOException
     * @throws NumberFormatException
     * @throws FileNotFoundException
     */
    public static void loadHospitalLocations(String name) throws IOException, NumberFormatException, FileNotFoundException {
        BufferedReader inputFile;
        inputFile = new BufferedReader(new FileReader(name));
        String inputLine;
        int numberOfHospitals;

        //First line is number of hospital facilities.
        inputLine = inputFile.readLine();
        numberOfHospitals = Integer.parseInt(inputLine);

        //The consicutive lines contain the details of each hospital entity.
        for (int i = 0; i < numberOfHospitals; i++) {
            Sql sql = new Sql();
            IdPointPair idPair = sql.getIdPointPairOnRoad();
            /*Read the next hospital's configuration.
             * The hospital configuration is stored in the following form
             * lat:lon:name:address:capacity
             */
            inputLine = inputFile.readLine();
            String[] param = inputLine.split(":"); //split into individual patamenters
            //Point hosLatlon = new Point(Double.parseDouble(param[0]), Double.parseDouble(param[1])); //Create the lat lon values
            Point hosLatlon = idPair.point;
            Hospital newHospital = new Hospital(hosLatlon, param[0], Integer.parseInt(param[2]), idPair.id); //Create a new hopital.
            newHospital.setAddress(param[1]);

            System.Log.logger.info("Hospital: " + newHospital.getName() + " located at " + newHospital.getLatLon());
            SharedData.hospitals.add(newHospital); //Add it to the global store.
        }
        //Close the file
        inputFile.close();
    }

    /**
     * Loads the configurations for the disasters that are present in the area
     * of interest.
     *
     * @param name filename for the disaster configuration.
     * @throws IOException
     * @throws NumberFormatException
     * @throws FileNotFoundException
     */
    public static void loadDisasterLocations(String name) throws IOException, NumberFormatException, FileNotFoundException {
        BufferedReader inputFile;
        inputFile = new BufferedReader(new FileReader(name));
        String inputLine;
        int numberOfDisasters;

        //First line is number of disasters.
        inputLine = inputFile.readLine();
        numberOfDisasters = Integer.parseInt(inputLine);

        //The consicutive lines contain the details of each disaster.
        for (int i = 0; i < numberOfDisasters; i++) {

            /*Read the next disaster's configuration.
             * The disaster configuration is stored in the following form.
             * lat:lon:intensity
             */
            inputLine = inputFile.readLine();
            String[] param = inputLine.split(":"); //split into individual patamenters
            Point disLatlon = new Point(Double.parseDouble(param[0]), Double.parseDouble(param[1])); //Create the lat lon values
            Disaster newDisaster = new Disaster(disLatlon, Integer.parseInt(param[2]), Integer.parseInt(param[3]));
            SharedData.disasters.add(newDisaster); //Add it to the global store.            
        }
        //Close the file
        inputFile.close();
    }

    /**
     * Load the civilian vehicle configurations
     *
     * @param name file name
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NumberFormatException
     */
    public static void loadCivilVehicleConfigurations(String name) throws FileNotFoundException, IOException, NumberFormatException {

        BufferedReader inputFile;
        inputFile = new BufferedReader(new FileReader(name));

        //First line is the number of civilian vehicles.
        String inputLine = inputFile.readLine();
        SharedData.numberOfCivilVehicles = Integer.parseInt(inputLine);

        // Close the input file
        inputFile.close();
    }

    /**
     * Load the emergency vehicle configurations
     *
     * @param name file name
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NumberFormatException
     */
    public static void loadEmergencyVehicleConfigurations(String name) throws FileNotFoundException, IOException, NumberFormatException {

        BufferedReader inputFile;
        inputFile = new BufferedReader(new FileReader(name));

        //First line is number of Ambulances
        String inputLine = inputFile.readLine();
        String[] ambulanceList = inputLine.split(":");
        //SharedData.ambulancePerHospital = new ArrayList<Integer>();
        int noAmbulance = 0;
        for (String ambulance : ambulanceList) {
            noAmbulance += Integer.parseInt(ambulance);
            SharedData.ambulancePerHospital.add(Integer.parseInt(ambulance));
        }
        SharedData.numberOfAmbulances = noAmbulance;
        System.Log.logger.info("Read Config number of Ambulances = " + SharedData.numberOfAmbulances);

        //Second line is number of Police Vehicles
        inputLine = inputFile.readLine();
        SharedData.numberOfPoliceVehicles = Integer.parseInt(inputLine);
        System.Log.logger.info("Read Config number of Police Vehicles = " + SharedData.numberOfPoliceVehicles);

        //Third line is number of Police Vehicles
        inputLine = inputFile.readLine();
        SharedData.numberOfFireVehicles = Integer.parseInt(inputLine);
        System.Log.logger.info("Read Config number of Fire Vehicles = " + SharedData.numberOfFireVehicles);

        //Close the input file
        inputFile.close();
    }
}
