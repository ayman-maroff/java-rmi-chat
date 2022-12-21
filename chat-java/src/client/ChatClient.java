package client;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

import server.ChatServerIF;
public class ChatClient extends UnicastRemoteObject implements ChatClientIF {

    //private static final long serialVersionUID = 7468891722773409712L;
    ClientRMIGUI chatGUI;
    ChatRoomGUI gui ;
    private final String hostName = "localhost";
    private final String serviceName = "GroupChatService";
    private final String clientServiceName;
    private final String name;

    private final String roomName;

    protected ChatServerIF serverIF;
    protected boolean connectionProblem = false;

private String userName;
private String password;
    /**
     * class constructor,
     * note may also use an overloaded constructor with
     * a port no passed in argument to super
     * @throws RemoteException
     */
    public ChatClient(ClientRMIGUI aChatGUI, String userName, String roomName) throws RemoteException {
        super();
        this.chatGUI = aChatGUI;
        this.name = userName;
        this.roomName=roomName;
        this.clientServiceName = "ClientListenService_" + userName;
    }
    public ChatClient( ChatRoomGUI aChatGUI, String Name,String uname,String pass) throws RemoteException {
        this.name="";
        this.gui = aChatGUI;
        this.roomName = Name;
        this.userName=uname;
        this.password=pass;
        this.clientServiceName = "ClientListenService_" + Name;
    }


    /**
     * Register our own listening service/interface
     * lookup the server RMI interface, then send our details
     * @throws RemoteException
     */
    public void startClient() throws RemoteException {
        String[] details = {name, hostName, clientServiceName,roomName};

        try {
            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
            serverIF = ( ChatServerIF )Naming.lookup("rmi://" + hostName + "/" + serviceName);
        }
        catch (ConnectException  e) {
            JOptionPane.showMessageDialog(
                    chatGUI.frame, "The server seems to be unavailable\nPlease try later",
                    "Connection problem", JOptionPane.ERROR_MESSAGE);
            connectionProblem = true;
            e.printStackTrace();
        }
        catch(NotBoundException | MalformedURLException me){
            connectionProblem = true;
            me.printStackTrace();
        }
        if(!connectionProblem){
            registerWithServer(details);
        }
        System.out.println("Client Listen RMI Server is running...\n");
    }
    public void startRoom() throws RemoteException {
        String[] details = {roomName, hostName, clientServiceName,userName,password};

        try {
            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
            serverIF = ( ChatServerIF )Naming.lookup("rmi://" + hostName + "/" + serviceName);
        }
        catch (ConnectException  e) {

            connectionProblem = true;
            e.printStackTrace();
        }
        catch(NotBoundException | MalformedURLException me){
            connectionProblem = true;
            System.out.println("****************************.\n");
            me.printStackTrace();
        }
        if(!connectionProblem){
            AddRoomWithServer(details);
        }
        System.out.println("Client Listen RMI Server is running...\n");
    }
    public void AddRoomWithServer(String[] details) {
        try{
            serverIF.roomIDentity(this.ref);//now redundant ??
            serverIF.roomListener(details);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * pass our username, hostname and RMI service name to
     * the server to register out interest in joining the chat
     * @param details
     */
    public void registerWithServer(String[] details) {
        try{
            serverIF.passIDentity(this.ref);//now redundant ??
            serverIF.registerListener(details);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //=====================================================================
    /**
     * Receive a string from the chat server
     * this is the clients RMI method, which will be used by the server
     * to send messages to us
     */
    @Override
    public void messageFromServer(String message) throws RemoteException {
        System.out.println( message );
        chatGUI.textArea.append( message );
        //make the gui display the last appended text, ie scroll to bottom
        chatGUI.textArea.setCaretPosition(chatGUI.textArea.getDocument().getLength());
    }

    /**
     * A method to update the display of users
     * currently connected to the server
     */
    @Override
    public void updateUserList(String[] currentUsers) throws RemoteException {

        if(currentUsers.length < 2){
            chatGUI.privateMsgButton.setEnabled(false);
        }
        chatGUI.userPanel.remove(chatGUI.clientPanel);
        chatGUI.setClientPanel(currentUsers);
        chatGUI.clientPanel.repaint();
        chatGUI.clientPanel.revalidate();
    }
    @Override
    public void updateRoomList(String[] currentRooms) throws RemoteException {

        gui.userPanel.remove(gui.RoomPanel);
        gui.setRoomPanel(currentRooms);
        gui.RoomPanel.repaint();
        gui.RoomPanel.revalidate();
    }
    public void ShowRooms()throws RemoteException {
        serverIF.updateRoomList();
        serverIF.removetemproom();
    }
    public String removeRoom(String roomName,String userName,String password)throws RemoteException{
        return serverIF.removeRoom(roomName,userName,password);
    }
}//end class













