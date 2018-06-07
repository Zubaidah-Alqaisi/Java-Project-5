/*********************************************************
 * Programmer: Zubaidah Alqaisi                          *
 *                                                       *
 * Course: CSCI 470  Spring 2018      Assignment 5       *
 *                                                       *
 * Program Function: Implement both the client and the   *
 *                server of a cooperating client-server  *
 *                database system.                       *
 * Class: CustomerClient                                 *
 *                                                       *
 * Private member: connectButton, getAllButton, addButton*
 *                 deleteButton, displayData, message,   *
 *                 name, address, ssn, zipCode, Socket,  *
 *           in, out, serialVersionUID, CustomerClient() *
 *            createAndShowGUI(), connect(), disconnect()*
 *            handleGetAll(), handleAdd(), handleDelete()*
 *            handleUpdate(), checkName(), checkAdress() *
 *            checkSsn(), checkZipCode().                *
 * Public members: main()                                *
 * Purpose: client-server class performs several         *
 *          operations on the database such as Add,      *
 *          Delete, Update, and Get All. Also it creates *
 *          the GUI and connect and disconnect to the    *
 *          server.                                      *
 ********************************************************/

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.*;

public class CustomerClient extends JFrame implements ActionListener {

    // GUI components
    private JButton connectButton = new JButton("Connect");
    private JButton getAllButton = new JButton("Get All");
    private JButton addButton = new JButton("Add");
    private JButton deleteButton = new JButton("Delete");
    private JButton updateButton = new JButton("Update Address");
    private JTextArea displayData = new JTextArea();
    private JLabel message = new JLabel("Not Connected.");
    private JTextField name = new JTextField();
    private JTextField address = new JTextField();
    private JTextField ssn = new JTextField();
    private JTextField zipCode = new JTextField();
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private static final long serialVersionUID = 1L;

    // main() function
    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            CustomerClient client = new CustomerClient();
            client.createAndShowGUI();
        });
    }

    // setting up the frame title
    private CustomerClient() {
        super("Customer Database");
    }

    /********************************************************
     * Function: createAndShowGUI()                         *
     *                                                      *
     * Purpose: Sets up the layout for the application as a *
     *         whole and add the action listener to each of *
     *         button involved in the form.                 *
     * Argument: none                                       *
     * Return: void none                                    *
     */

    private void createAndShowGUI() {

        setLayout(new BorderLayout()); // set the layout for the frame

        // set the frame size
        setPreferredSize(new Dimension(800, 450));
        // set minimum size for the frame
        setMinimumSize(new Dimension(600, 450));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // creating the panel and set it to border layout
        JPanel upperPanel = new JPanel(new BorderLayout());

        // set the text area size
        displayData.setPreferredSize(new Dimension(550, 300));

        // create the scroll pane and add the text area to it
        JScrollPane scrollPane = new JScrollPane(displayData);

        // add the panels to the frame
        add(upperPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.PAGE_END);

        // new grid panel
        JPanel gridPanel = new JPanel(new GridLayout(2,4));

        // new panel for the buttons
        JPanel buttonPanel = new JPanel (new FlowLayout());

        // add components to the upper panel and set each to border layout
        upperPanel.add(gridPanel, BorderLayout.PAGE_START);
        upperPanel.add(buttonPanel, BorderLayout.CENTER);
        upperPanel.add(message, BorderLayout.PAGE_END);

        // adding the labesl and text fields to the grid panel
        gridPanel.add(new JLabel("Name:"));
        gridPanel.add(name);
        gridPanel.add(new JLabel("SSN:"));
        gridPanel.add(ssn);
        gridPanel.add(new JLabel("Address:"));
        gridPanel.add(address);
        gridPanel.add(new JLabel("Zip Code:"));
        gridPanel.add(zipCode);

        // adding the buttons to the button panels
        buttonPanel.add(connectButton);
        buttonPanel.add(getAllButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);

        //disable the text fields
        name.setEnabled(false);
        address.setEnabled(false);
        ssn.setEnabled(false);
        zipCode.setEnabled(false);

        pack();

        setLocationRelativeTo(null);
        setVisible(true);

        // listener for the buttons
        connectButton.addActionListener(this );
        addButton.addActionListener(this );
        deleteButton.addActionListener(this);
        getAllButton.addActionListener(this);
        updateButton.addActionListener(this);

        name.setEnabled(false);
        address.setEnabled(false);
        ssn.setEnabled(false);
        zipCode.setEnabled(false);

        // disable the buttons at first
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
        updateButton.setEnabled(false);
        getAllButton.setEnabled(false);

    } // end of createAndShowGUI()

    /****************************************************
     * Function: actionPerformed()                      *
     * Purpose: handel action events for each button so *
     *          when a user click on any button, it will*
     *          perform the requested task.             *
     * @param e                                         *
     * Return: none void                                *
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Connect")) {
            connect();
        } else if (e.getActionCommand().equals("Disconnect")) {
            disconnect();
        } else if (e.getSource() == getAllButton) {
            handleGetAll();
        } else if (e.getSource() == addButton) {
            handleAdd();
        } else if (e.getSource() == updateButton) {
            handleUpdate();
        } else if (e.getSource() == deleteButton) {
            handleDelete();
        }
    } // end of actionPerformed ()

    /**************************************************
     * Function: connect()                            *
     * Purpose: This function connects to the server. *
     *          provided by the instructor.           *
     * Arguments: none                                *
     * Return: none void                              *
     */

    private void connect() {
        try {
            // Replace 97xx with your port number
            socket = new Socket("turing.cs.niu.edu", 9704);

            System.out.println("LOG: Socket opened");

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("LOG: Streams opened");

            connectButton.setText("Disconnect");
            
            // Enable buttons and text fields
            message.setText("Connected");
            name.setEnabled(true);
            address.setEnabled(true);
            ssn.setEnabled(true);
            zipCode.setEnabled(true);
            addButton.setEnabled(true);
            deleteButton.setEnabled(true);
            updateButton.setEnabled(true);
            getAllButton.setEnabled(true);

        } catch (UnknownHostException e) {
            message.setText("Exception resolving host name: " + e);
        } catch (IOException e) {
            message.setText("Exception establishing socket connection: " + e);
        }
    } // end of connect()

    /************************************************
     * Function: disconnect()                       *
     * Purpose: disconnect the server. Provided by  *
     *          the instructor.                     *
     * Arguments: none                              *
     * Return: none void                            *
     */

    private void disconnect() {
        connectButton.setText("Connect");
        
        // Disable buttons
        message.setText("Disconnected");
        name.setEnabled(false);
        address.setEnabled(false);
        ssn.setEnabled(false);
        zipCode.setEnabled(false);
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
        updateButton.setEnabled(false);
        getAllButton.setEnabled(false);

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Exception closing socket: " + e);
        }
    } // end of disconnect()

    /*******************************************************
     * Function: handleGetAll()                            *
     * Purpose: Gets all the records. When the user presses*
     *        this button, the program should retrieve and *
     *        display all of the database records in a     *
     *        JTextArea.                                   *
     * Argument: none                                      *
     * Return: none void                                   *
     */

    private void handleGetAll() {

        // clear the text area before displaying the new info
        displayData.setText("");

        // create instance of the MessageObject class and pass the values of the text fields to it and specify the functionality of the button
        MessageObject messageObj = new MessageObject(null, null, null , null, "get all");

        // empty the text fields
        name.setText("");
        address.setText("");
        ssn.setText("");
        zipCode.setText("");

        try {

            // writing to the server and sending the get request
            out.writeObject(messageObj);

            // getting the array list from the server
            ArrayList<MessageObject> objectMsgs = (ArrayList<MessageObject>) in.readObject();

            // looping through the array list of objects
            for(MessageObject messageObject : objectMsgs)
            {
                // display the info per row
                displayData.append(messageObject.getName() + "; ");
                displayData.append(messageObject.getSsn() + "; ");
                displayData.append(messageObject.getAddress() + "; ");
                displayData.append(messageObject.getZipCode());
                displayData.append("\n");
            }

            // display the message in the label
            message.setText(objectMsgs.size() + " Found.");
        }
        catch ( IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    } // end of handelGetAll()

    /************************************************
     * Function: handleAdd()                        *
     * Purpose: adds a new customer. The user has   *
     *          entered information into four       *
     *          JTextFields in the client and has   *
     *          pressed an “add” button.            *
     * Argument: none                               *
     * Return: none void                            *
     */

    private void handleAdd() {

        // clear the text area before displaying the new info
        displayData.setText("");

        // getting the value from text fields and store it in a string variables
        String nameString = name.getText();
        String addressString = address.getText();
        String ssnString = ssn.getText();
        String zipCodeString = zipCode.getText();

        if (checkName(nameString) && checkAddress(addressString) && checkSsn(ssnString)  && checkZipCode(zipCodeString)) {

            // create instance of the MessageObject class and pass the values of the text fields to it
            MessageObject messageObj = new MessageObject(nameString, addressString, ssnString, zipCodeString, "add");

            // empty the text fields
            name.setText("");
            address.setText("");
            ssn.setText("");
            zipCode.setText("");

            try {
                // send the info or values to the server
                out.writeObject(messageObj);

                // response back from the server
                messageObj = (MessageObject) in.readObject();

                // setting the label to display the number of records added
                message.setText(messageObj.getServerMessage() + " Added.");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        else
        {
            message.setText("Bad Data. Please supply a valid values.");
        }
    } // end of handelAdd()

    /******************************************************
     * Function: handleDelete()                           *
     * Purpose: deletes a customer. The user has provided *
     *          an SSN in the JTextField and has pressed a*
     *         “delete” button. The client should verify  *
     *        that the SSN is valid, and if so, transmit a*
     *        delete request containing the SSN to the    *
     *        server.                                     *
     * Argument: none                                     *
     * Return: none void                                  *
     */

    private void handleDelete() {

        // clear the text area before displaying the new info
        displayData.setText("");

        // getting the value from text fields and store it in a string variables
        String ssnDelete = ssn.getText();

        if ( checkSsn(ssnDelete)) {
            // create instance of the MessageObject class and pass the values of the text fields to it
            MessageObject messageObj = new MessageObject(null, null, ssnDelete, null, "delete");

            // empty the text fields
            name.setText("");
            address.setText("");
            ssn.setText("");
            zipCode.setText("");

            try {

                // send the info or values to the server
                out.writeObject(messageObj);

                // response back from the server
                messageObj = (MessageObject) in.readObject();

                // display the server message in the label
                message.setText(messageObj.getServerMessage() + " Deleted.");

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        else
        {
            message.setText("Bad Data. Please supply a valid ssn.");
        }

    } // end handelDelete ()

    /*******************************************************
     * Function: handleUpdate()                            *
     * Purpose: update a customer address. The user has    *
     *       entered an SSN and the new address and pressed*
     *       an “update” button. The client should verify  *
     *       that the SSN and the address are valid and    *
     *       transmit an update request containing that    *
     *       info to the server. The server program should *
     *       change the address for that SSN if found and  *
     *   report to the client on update success or failure.*
     *                                                     *
     * Argument: none                                      *
     * Return: none void                                   *
     */

    private void handleUpdate() {

        // clear the text area before displaying the new info
        displayData.setText("");

        // getting the value from text fields and store it in a string variables
        String addressUpdate = address.getText();
        String ssnUpdate = ssn.getText();

        if ( checkSsn(ssnUpdate) && checkAddress(addressUpdate)) {
            // create instance of the MessageObject class and pass the values of the text fields to it
            MessageObject messageObj = new MessageObject(null, addressUpdate, ssnUpdate, null, "update");

            // empty the test fields
            name.setText("");
            address.setText("");
            ssn.setText("");
            zipCode.setText("");

            try {

                // send the info or values to the server
                out.writeObject(messageObj);

                // response back from the server
                messageObj = (MessageObject) in.readObject();

                // display the server message at the label
                message.setText(messageObj.getServerMessage() + " Updated.");

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        else
        {
            message.setText("Bad Data. Please supply a valid address and ssn.");
        }
    } // end of handelUpdate()

    /*****************************************************
     * Function: checkName()                             *
     * Purpose: this function check the name entered by  *
     *        the user and make sure that it is not empty*
     *       and should not be greater than 20 characters*
     *       in length.                                  *
     * @param name                                       *
     * @return  name                                     *
     */

    private boolean checkName(String name)
    {
        // if the name textfield is empty
        if (name.isEmpty())
        {
            return false;
        }
        // if the length of the name is less than 20
        if (name.length() > 20 )
        {
            return false;
        }

        return true;

    } // end of checkName()

    /*************************************************
     * Function: checkSsn()                          *
     * Purpose: this function check the ssn that it  *
     *         is not be empty, should be exactly 11 *
     *         characters in length, and be composed *
     *      solely of digits and the hyphen character*
     *      in the format 999-99-9999.               *
     * @param ssn                                    *
     * @return ssn                                   *
     */

    private boolean checkSsn( String ssn)
    {
        // if the ssn length is less that 11 characters
        if ( ssn.length() != 11)
        {
            return false;
        }
        // if the forth place of the ssn does not have -
        if ( ssn.charAt(3) != '-')
        {
            return false;
        }
        // if the sixth place of the ssn does not have -
        if ( ssn.charAt(6) != '-')
        {
            return false;
        }
        // loop through the ssn and split it
        for(int i = 0; i < ssn.length(); i++)
        {
            // if any digit is not a digit and if it does not have -
            if (!Character.isDigit(ssn.charAt(i)) && i != 3 && i != 6)
            {
                return false;
            }
        }

        return true;

    } // end of checkSsn()

    /************************************************
     * Function: checkAddress()                     *
     * Purpose: make sure that The address should   *
     *        not be empty and should not be greater*
     *        than 40 characters in length.         *
     * @param address                               *
     * @return address                              *
     */

    private boolean checkAddress(String address)
    {
        // if the address field is empty
        if (address.isEmpty())
        {
            return false;
        }
        // if the adress length is more than 40
        if (address.length() > 40 )
        {
            return false;
        }

        return true;

    } // end of checkAddress ()

    /********************************************
     * Function: checkZipCode()                 *
     * Purpose: makes sure the zipCode should   *
     *        not be empty, should be exactly 5 *
     *        characters in length, and should  *
     *        only contain numeric digits.      *
     * @param zipCode                           *
     * @return                                  *
     */

    private boolean checkZipCode( String zipCode)
    {
        // if the zipcode length is not equal to 5
        if ( zipCode.length() != 5)
        {
            return false;
        }
        // loop through the zipcode and make sure it is all digits
        for(int i = 0; i < zipCode.length(); i++)
        {
            if (!Character.isDigit(zipCode.charAt(i)) )
            {
                return false;
            }
        }

        return true;

    } // end of checkZipCode()

}
