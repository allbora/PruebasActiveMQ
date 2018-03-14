import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class MessageSender {
	private static final boolean TRANSACTED_SESSION = true;

    public enum UserAction {

        CONFIGURACION("IR A OPCIONES DE CONFIGURACION"),
        PORTADA("VER PORTADA"),
        LOGIN("ACCEDER A LA APLICACION"),
        SUGERENCIA("ENVIAR SUGERENCIA");

        private final String userAction;

        private UserAction(String userAction) {
            this.userAction = userAction;
        }

        public String getActionAsString() {
            return this.userAction;
        }
    }

    public void sendMessages() throws JMSException {

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Constantes.USER, Constantes.PASSWORD, Constantes.URL);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        final Session session = connection.createSession(TRANSACTED_SESSION, Session.AUTO_ACKNOWLEDGE);
        final Destination destination = session.createQueue(Constantes.DESTINATION_QUEUE);

        final MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        sendMessages(session, producer);
        session.commit();

        session.close();
        connection.close();

        System.out.println("Mensajes enviados correctamente");
    }

    private void sendMessages(Session session, MessageProducer producer) throws JMSException {
        final MessageSender messageSender = new MessageSender();
        for (int i = 1; i <= Constantes.MESSAGES_TO_SEND; i++) {
            final UserAction userActionToSend = getRandomUserAction();
            messageSender.sendMessage(userActionToSend.getActionAsString(), session, producer);
        }
    }

    private void sendMessage(String message, Session session, MessageProducer producer) throws JMSException {
        final TextMessage textMessage = session.createTextMessage(message);
        System.out.println("Enviando Mensaje... -> " + textMessage.getText());
        producer.send(textMessage);
    }

    private static UserAction getRandomUserAction() {
        final int userActionNumber = (int) (Constantes.RANDOM.nextFloat() * UserAction.values().length);
        return UserAction.values()[userActionNumber];
    }

    public static void main(String[] args) throws JMSException {
        final MessageSender messageSender = new MessageSender();
        messageSender.sendMessages();
    }

}