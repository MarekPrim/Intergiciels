package irc;

import org.objectweb.joram.client.jms.admin.*;
import org.objectweb.joram.client.jms.*;
import org.objectweb.joram.client.jms.tcp.*;

public class CreateDestination {
  public static void main(String args[]) throws Exception {
    System.out.println();
    System.out.println("CreateDestination administration phase... ");

    // Connecting to JORAM server:
    AdminModule.connect("root", "root", 60);

    // Creating the JMS administered objects:        
    javax.jms.ConnectionFactory connFactory =
      TcpConnectionFactory.create("localhost", 16010);

    Destination destination = Topic.create(0);
    //Destination destination = Queue.create(0); // XXXX

    Destination user1 = Topic.create(0);
    Destination user2 = Topic.create(0);
    Destination user3 = Topic.create(0);
    
    // Creating an access for user anonymous:
    User.create("anonymous", "anonymous", 0);

    // Setting free access to the destination:
    destination.setFreeReading();
    destination.setFreeWriting();

    user1.setFreeReading();
    user1.setFreeWriting();
    user2.setFreeReading();
    user2.setFreeWriting();
    user3.setFreeReading();
    user3.setFreeWriting();
    
    

    // Binding objects in JNDI:
    javax.naming.Context jndiCtx = new javax.naming.InitialContext();
    jndiCtx.bind("ConnFactory", connFactory);
    jndiCtx.bind("MonTopic", destination);
    jndiCtx.bind("user1", user1);
    jndiCtx.bind("user2", user2);
    jndiCtx.bind("user3", user3);
    jndiCtx.close();
    
    AdminModule.disconnect();
    System.out.println("Admin closed.");
  }
}
