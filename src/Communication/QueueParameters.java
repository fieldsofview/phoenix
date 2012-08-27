package Communication;

import java.io.Serializable;

/**
 * This class contains the parameters required to set up a RabbitMQ message
 * queue for a CTA.
 */
public class QueueParameters implements Serializable {

    /**
     * The name of the queue for messaging.
     */
    public String queueName;
    /**
     * The username required to access the rabbitMQ server
     */
    public String username;
    /**
     * The password required to access the rabbitMQ password
     */
    public String password;
    /**
     * The virtual host contacted for the queue on the rabbitMQ server.
     */
    public String virtualHost;
    /**
     * The port to query on the rabbitMQ server
     */
    public String port;
    /**
     * The exchange name for the message queue on the rabbitMQ server
     */
    public String exchange;
    /**
     * The routing key for the message queue on the rabbitMQ server
     */
    public String routingKey;

    /**
     * Create an empty object
     */
    public QueueParameters() {
    }

    /**
     * Constructor for the queue parameters Create a QueueParameter with the
     * given parameters.
     *
     * @param queueName the name of the queue
     * @param username the username for the rabbitMQ server with access to the
     * given queue name
     * @param password the password for the rabbitMQ server with access to the
     * given queue name
     * @param virtualHost the virtual host on the rabbitMQ server where new
     * message queues can be created by the given user
     * @param port the port on which to communicate with a rabbitMQ server
     * @param exchange the message exchange on the virtual host responsible for
     * transfer of messages
     * @param routingKey the routing key / token that is exchanged when message
     * is transfered
     */
    public QueueParameters(String queueName, String username, String password, String virtualHost, String port, String exchange, String routingKey) {
        this.queueName = queueName;
        this.username = username;
        this.password = password;
        this.virtualHost = virtualHost;
        this.port = port;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    /**
     * String representation of QueueParameters
     *
     * @return string representation of the queue parameters
     */
    @Override
    public String toString() {
        return "queueName:" + this.queueName + " username:" + this.username + " password:" + this.password
                + " virtualHost:" + this.virtualHost + " port" + this.port + " exchange" + this.exchange
                + " routingKey:" + this.routingKey;
    }
}
