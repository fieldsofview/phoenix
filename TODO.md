2 April, 2013

Modules
-------
* Add globally available list of loaded modules

database module
----------------
* Remove DBAccess.java and port code into DatabaseModule.java

Communications
--------------
* Test the dedicated channel on RabbitMQ for Agent Controller communication.
* Implement the communication code for agent to agent communication.
	- Modify the ACNetwork.java file.
	- Modify the Boot.java, readConfigurations() function to read the second queue parameters.
	- Modify the machineConfig file with details for the new queue for agent communications along with routing key and channels.
	- Modify the AgentController.java, Agent.java and any additional communication files.

General
-------
* Write a hello world simulation for testing.

Pending Integrations
--------------------
* JUnit Test Cases
* Clean-up Javadoc
* Java Live Graph integration
* Set-up Wiki for Installation and Operations

