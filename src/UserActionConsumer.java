import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class UserActionConsumer {

	private static final boolean TRANSACTED_SESSION = false;

    private final Map<String, Integer> consumedMessageTypes;

    private int totalConsumedMessages = 0;

    public UserActionConsumer() {
        this.consumedMessageTypes = new HashMap<String, Integer>();
    }

    public void processMessages() throws JMSException {

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Constantes.USER, Constantes.PASSWORD, Constantes.URL);
        final Connection connection = connectionFactory.createConnection();

        connection.start();

        final Session session = connection.createSession(TRANSACTED_SESSION, Session.AUTO_ACKNOWLEDGE);
        final Destination destination = session.createQueue(Constantes.DESTINATION_QUEUE);
        final MessageConsumer consumer = session.createConsumer(destination);

        processAllMessagesInQueue(consumer);

        consumer.close();
        session.close();
        connection.close();

        showProcessedResults();
    }

    private void processAllMessagesInQueue(MessageConsumer consumer) throws JMSException {
        Message message;
        while ((message = consumer.receive(Constantes.TIMEOUT)) != null) {
            proccessMessage(message);
        }
    }

    private void proccessMessage(Message message) throws JMSException {
        if (message instanceof TextMessage) {
            final TextMessage textMessage = (TextMessage) message;
            final String text = textMessage.getText();
            incrementMessageType(text);
            totalConsumedMessages++;
        }
    }

    private void incrementMessageType(String message) {
        if (consumedMessageTypes.get(message) == null) {
            consumedMessageTypes.put(message, 1);
        } else {
            final int numberOfTypeMessages = consumedMessageTypes.get(message);
            consumedMessageTypes.put(message, numberOfTypeMessages + 1);
        }
    }

    private void showProcessedResults() {
        System.out.println("Procesados un total de " + totalConsumedMessages + " mensajes");
        for (String messageType : consumedMessageTypes.keySet()) {
            final int numberOfTypeMessages = consumedMessageTypes.get(messageType);
            System.out.println("Tipo " + messageType + " Procesados " + numberOfTypeMessages + " (" +
                    (numberOfTypeMessages * 100 / totalConsumedMessages) + "%)");
        }
    }

    public static void main(String[] args) throws JMSException {
        final UserActionConsumer userActionConsumer = new UserActionConsumer();
        userActionConsumer.processMessages();
    }
}