
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.*;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

    public static final int PORT = 8765;

    ObjectInputStream myReader;
    ObjectOutputStream myWriter;
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
    Socket connection;

    private BigInteger E;
    private BigInteger N;
    private String encType; 
    private SymCipher cipher;
    private BigInteger key;
    private BigInteger encKey;
    private byte[] encName;

    public SecureChatClient() {
        try {

            myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
            serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
            InetAddress addr = InetAddress.getByName(serverName);
            connection = new Socket(addr, PORT); // connect to server with socket
            myWriter = new ObjectOutputStream(connection.getOutputStream()); // writer
            myWriter.flush();
            myReader = new ObjectInputStream(connection.getInputStream()); // reader 

            this.E = (BigInteger) myReader.readObject(); // read E
            this.N = (BigInteger) myReader.readObject(); // read N
            System.out.println("E: " + this.E);
            System.out.println("N: " + this.N);

            this.encType = (String) myReader.readObject(); // read cipher type
            switch (this.encType) {
                case "Sub":
                    System.out.println("Using Substitute encryption...");
                    this.cipher = new Substitute();
                    break;
                case "Add":
                    System.out.println("Using Add128 encryption...");
                    this.cipher = new Add128();
                    break;
            }
            this.key = new BigInteger(1, this.cipher.getKey());
            this.encKey = this.key.modPow(this.E, this.N); // key must be < N
            System.out.println("Creating key: " + this.key);
            System.out.println("Sending encrypted key: " + this.encKey);
            System.out.println();
            myWriter.writeObject(this.encKey);
            myWriter.flush();
            
            this.encName = this.cipher.encode(myName);
            myWriter.writeObject(this.encName);
            myWriter.flush();

            System.out.println("Connection established");

            // GUI Stuff 
            
            this.setTitle(myName); // Set title to identify 

            Box b = Box.createHorizontalBox(); // Set up graphical environment for
            outputArea = new JTextArea(8, 30); // user
            outputArea.setEditable(false);
            b.add(new JScrollPane(outputArea));

            outputArea.append("Welcome to the Chat Group, " + myName + "\n");

            inputField = new JTextField(""); // This is where user will type input
            inputField.addActionListener(this);

            prompt = new JLabel("Type your messages below:");
            Container c = getContentPane();

            c.add(b, BorderLayout.NORTH);
            c.add(prompt, BorderLayout.CENTER);
            c.add(inputField, BorderLayout.SOUTH);

            Thread outputThread = new Thread(this); // Thread is to receive strings
            outputThread.start(); // from Server

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    try {
                        byte[] closeMsg = cipher.encode("CLIENT CLOSING");
                        myWriter.writeObject(closeMsg);
                        myWriter.flush();
                    } catch (IOException f) {
                        System.out.println(f);
                    }
                    System.exit(0);
                }
            });

            setSize(500, 200);
            setVisible(true);

        } catch (Exception e) {
            System.out.println("Problem starting client!");
        }
    }

    public void run() {
        while (true) {
            try {
                byte[] encMsg = (byte[]) myReader.readObject();
                String decMsg = this.cipher.decode(encMsg);
                outputArea.append(decMsg + "\n");
            } catch (Exception e) {
                System.out.println(e + ", closing client!");
                break;
            }
        }
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e) {
        String currMsg = e.getActionCommand(); // Get input value
        currMsg = this.myName + ": " + currMsg; // add name
        byte[] encMsg = this.cipher.encode(currMsg);
        inputField.setText("");
        try {
            myWriter.writeObject(encMsg); // send it
            myWriter.flush();
        } catch (IOException f) { 
            System.out.println(f);
        }
        
    } // to Server

    public static void main(String[] args) {
        SecureChatClient JR = new SecureChatClient();
        JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}
