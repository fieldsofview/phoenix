# Phoenix Agent-Based Simualtion Platform

## Install

This installation procedure describes the prerequisites, installation and configuration of the agent simulation software. This section provides only limited help with the following software. Please refer to their original manuals for further help. The following software are assumed to be installed and working in their standard configurations:

* Git
* Oracle Java JDK 1.6 or above / OpenJDK 7
* Your Favourite Java IDE

### Requirements

* BoneCP
* Google Guava
* SLF4J
* RabbitMQ
* Java 1.8 or higher

### Installing Phoenix

1. Modify the configurations present in the _config_ folder to reflect the current simulation scenario. Check configuration help below to set up the simulation configuration as well as the interface with the other programs.
2. Run the appropriate _AgentController_ as per the simulation configuration.
3. Check the logs folder for the output files.

### Configuring Phoenix

The configuration consists of 3 configuration files. Most of the configuration files are currently specific to a single type of simulation. Please note that the configuration system may be simplified and merged to suite a more general need in the future releases. The configuration files are:

1. machineConfig: RabbitMQ Server Details
2. logger.config: log4j configuration
3. dbconfig: Database configuration if the database module is used.

#### Constants.java

Please generate the corresponding _javadoc_ for the code to find more details about modifying this file. The only value that has to be set manually is the IP address of the local machine. Please change the value of _public final String localHost = "0.0.0.0"_ to the machine's local IP ( NOTE: the same IP has to used in the _RabbitMQ_ server configuration). This operation will be made automatic in the future releases.

### High Availability of RabbitMQ broker service

Since this is a server side configuration, the different options available to us for the first release are

1. Leave it up to the simulation writing team (the platform will be distributed but number of points of failure is upto the team to decide. Simplest option for the immediate first release. Point to the RMQ documentation in our documentation). 
2. Make it optional, provide information about how to go about it (this makes the platform fully distributed but with a single point of failure). 
3. Make it compulsory and provide information about how to go about it (this makes the platform fully distributed with no single point of failure).

#### Other options for redundancy

Use active/passive mirroring: passive node will come up and start once the active node fails. But this means there will be some delay. This feature, although available still, is deprecated in its use.

#### Further Readings

* Clustering on RMQ: [http://www.rabbitmq.com/clustering.html](http://www.rabbitmq.com/clustering.html)
* RabbitMQ active/active high availability of queues [http://www.rabbitmq.com/ha.html](http://www.rabbitmq.com/ha.html): This is Cluster + mirroring

## A basic agent Based simulation

Writing a basic agent based simulation using the Phoenix platform involves three steps:

1. __Creating the simulation class__ by extending the _agents.AgentController_ class. This will require you to override the _setUp()_ and _cleanUp()_ methods.
    1. __Setup the AgentController by writing the constructor:__ The constructor is used to set up the _simulation backend_ (adding QueueListeners, configuring loggers, reading properties, etc..)
    2. __Setup the simulation by overriding setUp() method__: This method is used to set up the simulation. Once the configuration properties are read, this method is used to initialise them in the simulation.
2. __Write the agent classes:__ Create agents by extending the _agents.Agent_ class. This will require you to override the run() method. Agents will now have an object of the AgentAttributes class, which will allow you to access all the attributes of the agent through a HashMap\<Object,Object\>.
    1. __Add behaviours to the agents:__ Using containment, create classes which extend "agents.behaviour.Behaviour". You will have to override the run() method. This method will allow you to describe the behaviour of the agent, which will be run in every tick until the agent completes its objective. An agent can have any number of behaviours.
    2. __Override the run() method:__ In this method, you will now have to add the run() methods of all the required behaviours, and any other function the agent needs to perform at every tick. This method is called once every tick.
3. __Create agents within the AC:__ Instantiate the agents within the simulation class, and define their properties.
    1. __Call the runAC() method:__ This method is called once, and runs until the simulation ends. This will call the necessary methods within the agents. This method is written within the main() method of the simulation.

## Modules in Phoenix

Phoenix has been designed as a modular structure, with only the most used features being in the core while the additional functionality added through modules. Phoenix modules inherit the Module interface.

### Current modules

1. Database Module

### Modules in production (9-May-2013)

1. GIS module
2. Modules for the output system

## Documentation

* Latex documentation avaliable in doc folder. It requires _Xetex_ to comple. Will add more information on documentation soon.
