package irc;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.jms.*;
import javax.naming.*;

public class Irc {
    private final String TopicName = "MonTopic";

    private Frame     mainFrame;
    private Button    writeButton;
    private TextArea  text;  // area where messages are displayed to the user.
    private TextField input; // input field where the user types.

    public String myName; // username (got from command line)

    public Connection        connection;
    public Session           sessionC;
    public Session           sessionP;
    public MessageProducer   producer;
    public MessageConsumer   consumer;

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.out.println("java Irc <name>");
            return;
        }
        new Irc(argv[0]);
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

        frame.add(buttons, BorderLayout.SOUTH);

        frame.setSize(550,300);
        text.setBackground(Color.black);
        this.mainFrame = frame;
        frame.setVisible(true);
    }

    /* allow to print something in the window */
    public void print(String msg) {
        text.append(msg+"\n");
    }

    private class ReadListener implements MessageListener {
        public void onMessage(Message msg)  {
            try {
                // XXXX
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
                // XXXX
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // action invoked when the "connect" button is clicked
    private class ConnectListener implements ActionListener {
        public void actionPerformed (ActionEvent ae) {
            try {
                ((Button) ae.getSource()).setEnabled(false);
                InitialContext ic = new InitialContext ();

                ConnectionFactory connectionFactory = (ConnectionFactory)ic.lookup("ConnFactory");
                Destination destination = (Destination)ic.lookup(TopicName);

                System.out.println("Bound to ConnFactory and MonTopic");

                Connection connection = connectionFactory.createConnection();
                connection.start();

                System.out.println("Created connection");

                System.out.println("Creating sessions: not transacted, auto ack");
                Session sessionP = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
                Session sessionC = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

                MessageProducer producer = sessionP.createProducer(destination);
                MessageConsumer consumer = sessionC.createConsumer(destination);

                consumer.setMessageListener(new ReadListener());

                Irc.this.connection = connection;
                Irc.this.sessionC = sessionC;
                Irc.this.sessionP = sessionP;
                Irc.this.producer = producer;
                Irc.this.consumer = consumer;

                System.out.println("Ready");

                writeButton.setEnabled(true);
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
                // XXXX
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
                // XXXX
                if (connection != null) connection.close();
                mainFrame.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
