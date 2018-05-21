package it.upo.reti2.mqtt.publisher;

import org.eclipse.paho.client.mqttv3.*;

/**
 * Sample publisher for MQTT. It uses the Eclipse Paho library and Mosquitto as a broker.
 * Mosquitto is expected to be installed and launched locally
 * (public test servers are available, however).
 *
 * It connects to the Mosquitto broker, set up a Last Will and Testament for the connection,
 * and publish a sample temperature value (i.e., a string equal to "20 C") on a specific topic.
 *
 * @author <a href="mailto:luigi.derussis@uniupo.it">Luigi De Russis</a>
 * @version 1.0 (21/05/2018)
 */
public class MQTTPublisher {

    // the broker URL
    private static final String BROKER_URL = "tcp://localhost:1883";
    // the (only) MQTT topic of this example
    private static final String TOPIC_TEMPERATURE = "homestation/temperature";

    // init the client
    private MqttClient client;

    /**
     * Constructor. It generates a client id and instantiate the MQTT client.
     */
    public MQTTPublisher() {

        // A randomly generated client identifier based on the user's login
        // name and the system time
        String clientId = MqttClient.generateClientId();

        try {
            client = new MqttClient(BROKER_URL, clientId);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * The method to start the publisher. Currently, it sets a Last Will and Testament
     * message, open a non persistent connection, and publish a temperature value
     */
    public void start() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            // persistent, durable connection
            options.setCleanSession(false);
            options.setWill(client.getTopic("homestation/LWT"), "I'm gone. Bye.".getBytes(), 0, false);

            // connect the publisher to the broker
            client.connect(options);

            // publish something...
            publishTemperature();

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * It prepares and publish the temperature value to a specific topic (/homestation/temperature).
     * @throws MqttException
     */
    private void publishTemperature() throws MqttException {
        // get the topic
        MqttTopic temperatureTopic = client.getTopic(TOPIC_TEMPERATURE);

        // message content
        String temperature = "20 °C";

        // publish the message on the given topic
        // by default, the QoS is 1 and the message is not retained
        temperatureTopic.publish(new MqttMessage(temperature.getBytes()));

        // debug
        System.out.println("Published message on topic '" + temperatureTopic.getName() + "': " + temperature);
    }

    /**
     * The main
     */
    public static void main(String[] args) {
        MQTTPublisher publisher = new MQTTPublisher();
        publisher.start();
    }

}
