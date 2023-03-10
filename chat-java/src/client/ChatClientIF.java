package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for client classes
 * A method to receive a string
 * A method to update changes to user list
 *
 * @author Daragh Walshe 	B00064428
 * RMI Assignment 2		 	April 2015
 *
 */
public interface ChatClientIF extends Remote{

    void messageFromServer(String message) throws RemoteException;

    void updateUserList(String[] currentUsers) throws RemoteException;
    void updateRoomList(String[] currentRoom) throws RemoteException;
}
/**
 *
 *
 *
 *
 */