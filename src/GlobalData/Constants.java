package GlobalData;

/**
 * This class contains constant values that will be used throughout the
 * simulation and will not change.
 */
public interface Constants {

    /**
     * Throughout the local system IP address
     */
    /**
     * TODO: Replace this with a function to find the local system IP using a
     * function.
     */
    public final String localHost = "192.168.0.12";
    /**
     * The file name for the launch settings containing values for bounding
     * boxes, agents, etc,.
     */
    public final String configFile = "launchConfig";
    /**
     * The file name for communication settings
     */
    public final String machineFile = "machineConfig";
    /**
     * Name of the cost column for calculating shortest path based on some set
     * values
     */
    public final String COST_COLUMN_REGULAR_VEHICLE = "cost";
    /**
     * Name of the cost column for calculating shortest path for emergency
     * vehicles
     */
    public final String COST_COLUMN_EMERGENCY_VEHICLE = "costemergency";
    /**
     * Name of the cost column for calculating shortest path based on length
     */
    public final String COST_COLUMN_LENGTH_BASED = "length";
    /**
     * The value for identifying a AC as a PEOPLE AC
     */
    public final int AC_TYPE_PEOPLE = 1;
    /**
     * The value for identifying a AC as a VEHICLE AC
     */
    public final int AC_TYPE_VEHICLE = 2;
    /**
     * The value for identifying a AC as a TRAFFIC LIGHT AC
     */
    public final int AC_TYPE_TRAFFIC_LIGHT = 3;
    /**
     * The value for identifying a AC as a EMERGENCY SERVICE AC
     */
    public final int AC_TYPE_EMERGENCY_SERVICE = 4;
    /**
     * The name of the table containing cells for vehicles
     */
    public final String TABLE_VEHICLE_CELLS = "lanecells";
    /**
     * The name of the table containing cells for people
     */
    public final String TABLE_PEOPLE_CELLS = "foothpathcells";
    /**
     * The value for identifying a emergency vehicle as a ambulance.
     *
     * @see Vehicle
     * @see EmergencyServiceAC
     */
    public static final int EMERGENCY_VEHICLE_TYPE_AMBULANCE = 1;
    /**
     * The value for identifying a emergency vehicle as a police.
     *
     * @see Vehicle
     * @see EmergencyServiceAC
     */
    public static final int EMERGENCY_VEHICLE_TYPE_POLICE = 2;
    /**
     * The value for identifying a emergency vehicle as a fire.
     *
     * @see Vehicle
     * @see EmergencyServiceAC
     */
    public static final int EMERGENCY_VEHICLE_TYPE_FIRE = 3;
    /**
     * The smallest distance that a person agent moves.
     */
    public static double AGENT_STEP_SIZE = 0.000005;
    /**
     * The value for signalling RED by TrafficLight agent.
     *
     * @see TrafficLight
     */
    public static final int SIGNAL_RED = 0;
    /**
     * The value for signalling GREEN by TrafficLight agent.
     *
     * @see TrafficLight
     */
    public static final int SIGNAL_GREEN = 1;
    /**
     * The duration for which the Agent displays the signal in terms of number
     * of TICKS. NOTE: Timeout is not real time.
     *
     * @see TrafficLight
     */
    public static final int SIGNAL_TIMEOUT = 2;
    //Hex values for various Signal colors
    /**
     * hex value for colour red for signal
     */
    public static final String SIGNAL_COLOR_RED = "ff0000ff"; //Color RED
    /**
     * hex value for colour green for signal
     */
    public static final String SIGNAL_COLOR_GREEN = "ff00ff00";// Color GREEN
    /**
     * size of a signal icon
     */
    public static final String SIGNAL_SCALE = "1.1d";
    /**
     * location for the image of the signal icon
     */
    public static final String SIGNAL_IMAGE = "img/signal.png";
    //Hex values for person icon colors.
    /**
     * hex value for colour of a healthy person icon
     */
    public static final String PERSON_HEALTHY = "ffaa0000";
    /**
     * hex value for colour of a injured person icon
     */
    public static final String PERSON_INJURED = "ff555500";
    /**
     * hex value for colour of a critically injured person icon
     */
    public static final String PERSON_CRITICAL = "ff0000aa";
    /**
     * hex value for colour of a healthy dead icon
     */
    public static final String PERSON_DEAD = "ff000000";
    /**
     * size of a person icon
     */
    public static final String PERSON_SCALE = "0.5d";
    /**
     * location for the image of the person icon
     */
    public static final String PERSON_IMAGE = "img/man.png";
    //Hex values for vehicle icons.
    /**
     * Colour for a civilian vehicle icon
     */
    public static final String VEHICLE_DEFAULT = "ff555500";
    /**
     * Colour for a ambulance vehicle icon
     */
    public static final String VEHICLE_AMBULANCE = "ffffffff";
    /**
     * Colour for a police vehicle icon
     */
    public static final String VEHICLE_POLICE = "ffffffff";
    /**
     * location for the image of the civilian vehicle icon
     */
    public static final String VEHICLE_DEFAULT_IMAGE = "img/car.png";
    /**
     * location for the image of the ambulance icon
     */
    public static final String VEHICLE_AMBULANCE_IMAGE = "img/ambulance.png";
    /**
     * location for the image of the police vehicle icon
     */
    public static final String VEHICLE_POLICE_IMAGE = "img/police.png";
    /**
     * Colour for a fire vehicle icon
     */
    public static final String VEHICLE_FIRE = "ffffffff";
    /**
     * location for the image of the fire vehicle icon
     */
    public static final String VEHICLE_FIRE_IMAGE = "img/fire.png";
    /**
     * size of a civilian vehicle icon
     */
    public static final String VEHICLE_SCALE = "1.0d";
    /**
     * size of a ambulance icon
     */
    public static final String AMBULANCE_SCALE = "1.5d";
    /**
     * size of a police vehicle icon
     */
    public static final String POLICE_SCALE = "1.5d";
    /**
     * size of a fire vehicle icon
     */
    public static final String FIRE_SCALE = "2.0d";
    //String values to identify the type of vehicle
    /**
     * String constant to identify a civilian vehicle
     */
    public static final String VEHICLE_TYPE_CIVIL = "civil";
    /**
     * String constant to identify a ambulance
     */
    public static final String VEHICLE_TYPE_AMBULANCE = "ambulance";
    /**
     * String constant to identify a police vehicle
     */
    public static final String VEHICLE_TYPE_POLICE = "police";
    /**
     * String constant to identify a fire vehicle
     */
    public static final String VEHICLE_TYPE_FIRE = "fire";
    /**
     * The integer represents the distance in terms of number of cells beyond
     * which a traffic light signal will be ignored.
     */
    public static final int OBEY_TRAFFIC_LIGHT_DISTANCE = 10;
}
