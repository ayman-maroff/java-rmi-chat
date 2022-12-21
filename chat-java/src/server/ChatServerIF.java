package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;

/**
 * Server RMI interface
 *
 * @author Daragh Walshe 	B00064428
 * RMI Assignment 2		 	April 2015
 *
 */
public interface ChatServerIF extends Remote {

    void updateChat(String userName, String chatMessage,String roomName)throws RemoteException;

    void passIDentity(RemoteRef ref)throws RemoteException;

    void registerListener(String[] details)throws RemoteException;
    void roomIDentity(RemoteRef ref)throws RemoteException;

    void roomListener(String[] details)throws RemoteException;
    void leaveChat(String userName,String roomName)throws RemoteException;
    void updateRoomList()throws RemoteException;
    void removetemproom() throws RemoteException;
    String removeRoom(String roomName,String userName,String pass)throws RemoteException;
    void sendPM(int[] privateGroup, String privateMessage,String roomName)throws RemoteException;
}


