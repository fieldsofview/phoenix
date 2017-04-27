# Phoenix Agent-Based Simualtion Platform

## Install

### Requirements
* BoneCP
* Google Guava
* SLF4J
* RabbitMQ
* Java 1.8 or higher

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
