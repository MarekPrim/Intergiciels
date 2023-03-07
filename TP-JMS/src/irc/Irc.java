package irc;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.jms.*;
import javax.naming.*;

public class Irc {
    private final String TopicName = "MonTopic";

    private static Frame     mainFrame;
    private static Button    writeButton;
    private static TextArea  text;  // area where messages are displayed to the user.
    private static TextField input; // input field where the user types.
    private static TextField private_input;

    public static String myName; // username (got from command line)
    

    public static ArrayList<String> utilisateurs = new ArrayList<String>();

    public Connection        connection;
    public  Session           sessionC;
    public static Session           sessionP;
    public static MessageProducer   producer;
    public  MessageConsumer   consumer;
    public MessageConsumer myConsumer;

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.out.println("java Irc <name>");
            return;
        }
        new Irc(argv[0]);
    }
    
    public static boolean contains(String str, String src){
        //Check in str if src is present
        //If yes, return the string after src
        //If no, return null
        int index = str.indexOf(src);
        return index != -1;
    }

    private Irc(String name) {
        myName = name;

        // creation of the GUI
        Frame frame = new Frame(name);
        frame.setLayout(new FlowLayout());

        text = new TextArea(10,60);
        text.setEditable(false);
        text.setForeground(Color.red);
        frame.add(text);

        input = new TextField(60);
        frame.add(input);
        
        private_input = new TextField(5);
        frame.add(private_input);
        
        Panel buttons = new Panel();
        buttons.setLayout(new FlowLayout());

        Button write_button = new Button("write");
        write_button.addActionListener(new WriteListener());
        write_button.setEnabled(false);
        buttons.add(write_button);
        this.writeButton = write_button;

        Button connect_button = new Button("connect");
        connect_button.addActionListener(new ConnectListener());
        buttons.add(connect_button);

        Button who_button = new Button("who");
        who_button.addActionListener(new WhoListener());
        buttons.add(who_button);

        Button leave_button = new Button("leave");
        leave_button.addActionListener(new LeaveListener());
        buttons.add(leave_button);
        
        Button private_message = new Button("private");
        private_message.addActionListener(new PrivateListener());
        buttons.add(private_message);

        frame.add(buttons, BorderLayout.SOUTH);

        frame.setSize(550,300);
        text.setBackground(Color.black);
        this.mainFrame = frame;
        frame.setVisible(true);
    }

    /* allow to print something in the window */
    public static void print(String msg) {
        try {
			text.append(msg+"\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private class ReadListener implements MessageListener {
        public void onMessage(Message msg)  {
            try {
               StreamMessage sm_msg  = (StreamMessage) msg;
               String _msg = sm_msg.readString();
               System.out.println(_msg);

               if(_msg.startsWith("say")){
                Irc.print(_msg);
               } else if (_msg.startsWith("enter")){
                StreamMessage iamhere_msg = Irc.sessionP.createStreamMessage();

                iamhere_msg.writeString("iamhere "+Irc.myName);
                
                Irc.producer.send(iamhere_msg);
               } else if (_msg.startsWith("iamhere")){
                
                if (!Irc.utilisateurs.contains(_msg)) {
                	Irc.print("Arrivée de "+_msg);
                    Irc.utilisateurs.add(_msg);
                }
               } else if (_msg.startsWith("leave")){
                Irc.print("**** Bye from: " + _msg);
                Irc.utilisateurs.remove(_msg);
               } else if (_msg.startsWith("private_say")){
                    if (contains(_msg,Irc.myName)) {
                        Irc.print(_msg);
                    }
               }
               else {
                Irc.print("Pas compris");
               }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // action invoked when the "write" button is clicked
    private class WriteListener implements ActionListener {
        public void actionPerformed (ActionEvent ae) {
            System.out.println("write button pressed");
            try {
            	StreamMessage m = Irc.sessionP.createStreamMessage();
    			m.writeString("say :"+Irc.myName+" says "+Irc.input.getText());
    			Irc.producer.send(m);
    			Irc.input.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // action invoked when the "connect" button is clicked
    private class ConnectListener implements ActionListener {
        public void actionPerformed (ActionEvent ae) {
            try {
            	System.out.println(Irc.myName);
                ((Button) ae.getSource()).setEnabled(false);
                InitialContext ic = new InitialContext ();

                ConnectionFactory connectionFactory = (ConnectionFactory)ic.lookup("ConnFactory");
                Destination destination = (Destination)ic.lookup(TopicName);
                Destination me = (Destination)ic.lookup(Irc.myName);
                
                // Destination user1 = (Destination)ic.lookup("user1");
                // Destination user2 = (Destination)ic.lookup("user2");
                // Destination user3 = (Destination)ic.lookup("user3");
                

                System.out.println("Bound to ConnFactory and MonTopic");

                Connection connection = connectionFactory.createConnection();
                connection.start();

                System.out.println("Created connection");
                
                System.out.println("Creating sessions: not transacted, auto ack");
                Session sessionP = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
                Session sessionC = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

                MessageProducer producer = sessionP.createProducer(destination);
                
                MessageConsumer consumer = sessionC.createConsumer(destination);
                MessageConsumer meConsumer = sessionC.createConsumer(me);
                
                consumer.setMessageListener(new ReadListener());
                //meConsumer.setMessageListener(new ReadListener());

                Irc.this.connection = connection;
                Irc.this.sessionC = sessionC;
                Irc.sessionP = sessionP;
                Irc.producer = producer;
                Irc.this.consumer = consumer;
                //Irc.this.myConsumer = meConsumer;

                System.out.println("Ready");

                writeButton.setEnabled(true);
                
                StreamMessage m = Irc.sessionP.createStreamMessage();
    			m.writeString("enter "+Irc.myName);
    			Irc.producer.send(m);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // action invoked when the "who" button is clicked
    private class WhoListener implements ActionListener {
        public void actionPerformed (ActionEvent ae) {
            System.out.println("who button pressed");
            try {
            	try {
        			String res = "";
        			for (String e : Irc.utilisateurs)
        				res += " "+e;
        			Irc.print(res);
        		} catch (Exception ex) {
        			ex.printStackTrace();
        		}
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // action invoked when the "leave" button is clicked
    private class LeaveListener implements ActionListener {
        public void actionPerformed (ActionEvent ae) {
            System.out.println("leave button pressed");
            try {
            	StreamMessage m = Irc.sessionP.createStreamMessage();
    			m.writeString("leave "+Irc.myName);
    			Irc.producer.send(m);
                if (connection != null) connection.close();
                mainFrame.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private class PrivateListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			try {
				StreamMessage m = Irc.sessionP.createStreamMessage();
    			m.writeString("private_say "+Irc.input.getText()+" "+Irc.myName+" says "+Irc.input.getText() + "to "+private_input.getText());
    			// /onC.createConsumer(destination);
    			InitialContext ic = new InitialContext ();
                Destination destination = (Destination)ic.lookup(Irc.private_input.getText());
    			producer.send(m);
    			Irc.input.setText("");
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
		}
    	
    }

}
