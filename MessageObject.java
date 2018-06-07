/****************************************************
 * Class: MessageObject                             *
 *                                                  *
 * Private member: none                             *
 * Public member: MessageObject(), getName(), getAd-*
 *               dress(), getSsn(), getZipCode(),   *
 *           getButtonPressed(), getServerMessage() *
 *           setServerMessage().                    *
 * Purpose: Build object serializable to send       *
 *        messages back and forth between the client*
 *        and the server.                           *
 ***************************************************/

import java.io.Serializable;

public class MessageObject implements Serializable {

    // private data members
    private String name;
    private String address;
    private String ssn;
    private String zipCode;
    private String buttonPressed;
    private int serverMessage;

    // constructor
    public MessageObject(String nameString, String addressString, String ssnString, String zipCodeString, String inButtonPressed)
    {
        name = nameString;
        address = addressString;
        ssn = ssnString;
        zipCode = zipCodeString;
        serverMessage = 0;

        // message to the server to figure out what to do with the object
        buttonPressed = inButtonPressed;

    }

    // getters for private data members
    public String getName() {
        return name;
    }

    public String getAddress() {

        return address;
    }

    public String getSsn() {
        return ssn;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getButtonPressed() {
        return buttonPressed;
    }

    public int getServerMessage() {
        return serverMessage;
    }

    // setter tos set the server message to specific message
    public void setServerMessage(int serverMessage) {
        this.serverMessage = serverMessage;
    }
}
