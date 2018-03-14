import java.util.Random;

import org.apache.activemq.ActiveMQConnection;

public class Constantes {

	static final Random RANDOM = new Random(System.currentTimeMillis());    

	static final int MESSAGES_TO_SEND = 20;

	static final String URL = "tcp://localhost:61616";

	static final String USER = ActiveMQConnection.DEFAULT_USER;

	static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;

	static final String DESTINATION_QUEUE = "PRUEBAS.IBR.ACTIVE_MQ.QUEUE";    

	static final int TIMEOUT = 1000;



}
