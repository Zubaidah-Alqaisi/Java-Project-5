/*********************************************************
 * Programmer: Zubaidah Alqaisi                          *
 *                                                       *
 * Course: CSCI 470  Spring 2018      Assignment 5       *
 *                                                       *
 * Program Function: Implement both the client and the   *
 *                server of a cooperating client-server  *
 *                database system.                       *
 * Class: CustomerServer                                 *
 *                                                       *
 * Private Members: CustomerServer()                     *
 * Public members: main(), run()                         *
 *                                                       *
 * Purpose: The Server program should be stored and run  *
 *          on turing.cs.niu.edu. The server program     *
 *       should connect to the mysql database management *
 *      system. Each student will have their own database*
 *      to manipulate. Each database contains a single   *
 *      table whose name is customer, so this is the     *
 *      table name you should specify in all of your SQL *
 *      queries.                                         *
 ********************************************************/

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CustomerServer extends Thread {
    private ServerSocket listenSocket;

    public static void main(String args[]) {
        new CustomerServer();
    }

    private CustomerServer() {
        // Replace 97xx with your port number
        int port = 9704;
        try {
            listenSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Exception creating server socket: " + e);
            System.exit(1);
        }

        System.out.println("LOG: Server listening on port " + port);
        this.start();
    }

    /**
     * run()
     * The body of the server thread. Loops forever, listening for and
     * accepting connections from clients. For each connection, create a
     * new Conversation object to handle the communication through the
     * new Socket.
     */

    public void run() {
        try {
            while (true) {
                Socket clientSocket = listenSocket.accept();

                System.out.println("LOG: Client connected");

                // Create a Conversation object to handle this client and pass
                // it the Socket to use.  If needed, we could save the Conversation
                // object reference in an ArrayList. In this way we could later iterate
                // through this list looking for "dead" connections and reclaim
                // any resources.
                new Conversation(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Exception listening for connections: " + e);
        }
    }
}

/**
 * The Conversation class handles all communication with a client.
 */
class Conversation extends Thread {

    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    // Where JavaCust04 is your database name
    private static final String URL = "jdbc:mysql://courses:3306/JavaCust04";

    // private prepared statements for the queries
    private Statement getAllStatement = null;
    private PreparedStatement addStatement = null;
    private PreparedStatement deleteStatement = null;
    private PreparedStatement updateStatement = null;

    /**
     * Constructor
     *
     * Initialize the streams and start the thread.
     */
    Conversation(Socket socket) {
        clientSocket = socket;

        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("LOG: Streams opened");
        } catch (IOException e) {
            try {
                clientSocket.close();
            } catch (IOException e2) {
                System.err.println("Exception closing client socket: " + e2);
            }

            System.err.println("Exception getting socket streams: " + e);
            return;
        }

        try {
            System.out.println("LOG: Trying to create database connection");
            Connection connection = DriverManager.getConnection(URL);

            // Create your Statements and PreparedStatements here
            getAllStatement = connection.createStatement();
            addStatement = connection.prepareStatement("insert into customer(name, ssn, address, zipCode) values(?, ?, ?, ?)");
            deleteStatement = connection.prepareStatement("delete from customer where ssn= ?");
            updateStatement = connection.prepareStatement("update customer set address = ? where ssn = ?");

            System.out.println("LOG: Connected to database");

        } catch (SQLException e) {
            System.err.println("Exception connecting to database manager: " + e);
            return;
        }

        // Start the run loop.
        System.out.println("LOG: Connection achieved, starting run loop");
        this.start();
    }

    /**
     * run()
     *
     * Reads and processes input from the client until the client disconnects.
     */
    public void run() {
        System.out.println("LOG: Thread running");

         try {
            while (true) {

                // Read and process input from the client.
                MessageObject message = (MessageObject)  in.readObject();

                // get the message from the object
                String buttonPressedMessage = message.getButtonPressed();

                if ( buttonPressedMessage.equals("add"))
                {
                    // calling the handel add and pass message object to it
                    handleAdd(message);
                    out.writeObject(message);

                }
                if ( buttonPressedMessage.equals("update"))
                {
                    // calling the handel update and pass message object to it
                    handleUpdate(message);
                    out.writeObject(message);

                }
                if ( buttonPressedMessage.equals("delete"))
                {
                    // calling the handel delete and pass message object to it
                    handleDelete(message);
                    out.writeObject(message);

                }
                if ( buttonPressedMessage.equals("get all") )
                {
                    // calling the getAll function
                    handleGetAll();
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("IOException: " + e);
            System.out.println("LOG: Client disconnected");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Exception closing client socket: " + e);
            }
        }
    } // end of run()

    /*******************************************************
     * Function: handleGetAll()                            *
     * Purpose: Gets all the records. This is the get all  *
     *          the server side which will hard code the   *
     *         query, and loop through the rows to retrieve*
     *          the requested info and send it back to the *
     *         client side.                                *
     * Argument: none                                      *
     * Return: none void                                   *
     */

    private void handleGetAll() {

        // declaring arraylist to hold the message objects
        ArrayList<MessageObject> objects = new ArrayList<>();

        try {
            // hard code the query and store it in the result set
            ResultSet result = getAllStatement.executeQuery(("select * from customer"));

            // move the pointer to the next row in the result set and loop through it
            while ( result.next() )
            {
                // get the information from the row
                String name = result.getString("name");
                String address = result.getString("address");
                String ssn = result.getString("ssn");
                String zipCode = result.getString("zipCode");

                // create a new instance of the message object
                MessageObject messageObj = new MessageObject(name, address, ssn, zipCode, null);

                // add the message objects to the array list
                objects.add(messageObj);
            }

            // sent the array list with the info
            out.writeObject(objects);

        }
        catch (SQLException | IOException e)
        {
            e.printStackTrace();
        }

    } // end of handelGetAll()

    /************************************************
     * Function: handleAdd()                        *
     * Purpose: adds a new customer. This is server *
     *          side add() that will run the query  *
     *          and process number of row changed   *
     *          in the customer table.              *
     * Argument: MessageObject clientMsg            *
     * Return: none void                            *
     */

    private void handleAdd(MessageObject clientMsg) {

        try {
            // hard code the query to add the info passed by the user into the table
            addStatement.setString(1, clientMsg.getName());
            addStatement.setString(2, clientMsg.getSsn());
            addStatement.setString(3, clientMsg.getAddress());
            addStatement.setString(4, clientMsg.getZipCode());

            // run the query and return # of rows changed
            clientMsg.setServerMessage(addStatement.executeUpdate());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    } // end of handelAdd()

    /*********************************************************
     * Function: handleDelete                                *
     * Purpose: Delete a record from the table. This is the  *
     *          server side handelDelete() that will delete a*
     *          record from the customer table.              *
     * @param clientMsg                                      *
     * Return: none                                          *
     */

    private void handleDelete(MessageObject clientMsg) {

        try {
            // hard code the query to delete a record
            deleteStatement.setString(1, clientMsg.getSsn());

            // calling the statement and run the query and return # of rows changed
            clientMsg.setServerMessage(deleteStatement.executeUpdate());

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    } // end of handelDelete()

    /*********************************************************
     * Function: handelUpdate()                              *
     * Purpose: Update a record. This is the server side     *
     *          handelUpdate() that will update a record in  *
     *          the customer table based on user selection.  *
     *                                                       *
     * @param clientMsg                                      *
     * Return: none                                          *
     */

    private void handleUpdate(MessageObject clientMsg) {

        try {
            // hard code the query to update the table
            updateStatement.setString(1, clientMsg.getAddress());
            updateStatement.setString(2, clientMsg.getSsn());

            // calling the statement and run the query and return # of rows changed
            clientMsg.setServerMessage(updateStatement.executeUpdate());

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

    } // end of handelUpdate
}
