package irc;

import javax.jms.*;
import javax.naming.*;

public class HelloWorld {

    public static void main(String argv[]) {

        try {
            InitialContext ic = new InitialContext ();

            ConnectionFactory connectionFactory = (ConnectionFactory)ic.lookup("ConnFactory");
            Destination destination = (Destination)ic.lookup("MonTopic");

            System.out.println("Bound to ConnFactory and MonTopic");

            Connection connection = connectionFactory.createConnection();
            connection.start();

            System.out.println("Created connection");

            System.out.println("Creating sessions: not transacted, auto ack");
            Session sessionP = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            Session sessionS = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = sessionP.createProducer(destination);
            MessageConsumer consumer = sessionS.createConsumer(destination);

            consumer.setMessageListener(new MessageListener() {
                    public void onMessage(Message msg)  {
                        try {
                            TextMessage textmsg = (TextMessage)msg;
                            System.out.println("I have received : " + textmsg.getText());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        
                    }});

            System.out.println("Ready");

            TextMessage textmsg = sessionP.createTextMessage();
            textmsg.setText("Hello World !!!");
            producer.send(textmsg);

        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

}
