/*
 * Remove this file eventually
 */
package Communication;

import Agents.Agent;
import Agents.AgentController;
import System.Log;

import java.io.Serializable;
import java.util.List;

/**
 * This class implemented as a thread will interpret the message received
 * 
 */
public class ProcessReceivedMessage extends Thread implements MessageQueueProcessor, Serializable {

    /**
     * 
     */
    public List<Agent> agents;
    /**
     * 
     */
    public Message message;

    /**
     * 
     */
    public ProcessReceivedMessage() {
        Log.logger.info("Created Message Receiver");
        
    }

    /**
     * 
     */
    public void run() {
        receivedMessage();
    }


    /**
     * 
     * @param message
     */
    public void receivedMessageHelper(Message message) {
        this.message = message;
    }

    /**
     * 
     */
    public void receivedMessage() {
        //update status of the host from which message was received
        Log.logger.info("PeopleCTA: ReceivedMessage");
        switch (message.type) {
            case ACNetwork.RMQ_TYPE_STATUS_UPDATE:
                //Find out what type of status update it is
                Message statusType = (Message) message.messageObject;
                switch (statusType.type) {
                    case ACNetwork.AC_READY_FOR_NEXT_TICK:
                        AgentController.changeACStatus(statusType.hostName, statusType.type);
                        Log.logger.info("Received Next Tick from : " + statusType.hostName + ":" + statusType.type);
                        break;
                    case ACNetwork.AC_DONE_WITH_WORK:
                        AgentController.changeACStatus(statusType.hostName, statusType.type);
                        Log.logger.info("Received Done with work from : " + statusType.hostName + ":" + statusType.type);
                        break;
                   // case ACNetwork.AC_SAVING_STATE:
                    //    Log.logger.info("Received state save message from " + statusType.hostName + "at tick: " + CareTakerAgent.currentTickNumber);
                     //   AgentController.tickNumberForStateSave = (Integer) statusType.messageObject;
                      //  AgentController.initiateStateSave = true;
                       // AgentController.changeACStatus(statusType.hostName, statusType.type);
                        //break;
                  //  case ACNetwork.AC_SAVED_STATE:
                   //     Log.logger.info("Received state saved message from " + statusType.hostName + "at tick: " + CareTakerAgent.currentTickNumber);
                    //    AgentController.changeACStatus(statusType.hostName, statusType.type);
                     //   break;
                    //case AgentController.AC_RESTORED_STATE:
                      //  Log.logger.info("Received restored state message from " + statusType.hostName + "at tick: " + CareTakerAgent.currentTickNumber);
                       // AgentController.changeACStatus(statusType.hostName, statusType.type);
                        //break;
//                    case CTANetwork.CTA_COMPLETE_EXIT:
//                        Utilities.Log.logger.info("VehicleCTA: Done with work and Exitting.....");
//                        changeCTAStatus(statusType.hostName, statusType.type);
//                        break;
                }
                break;

            case ACNetwork.RMQ_TYPE_AGENT_DATA:
                //List<Person> pickedUpPersonList = (List<Person>) message.messageObject;
                //chcek for duplicates and update the critically injured list
                //Log.logger.info("Received Picked up People List : " + pickedUpPersonList.size());
                //for (Person person : pickedUpPersonList) {
                 //   if (agents.contains(person)) {
                  //      agents.remove(person);
                   // }
                //}
                //Utilities.Log.logger.info("Received Critically Injured People " + criticalPersonList.size() + "With values (id,value):" + criticalPersonList);
                break;
        }
    }
}
