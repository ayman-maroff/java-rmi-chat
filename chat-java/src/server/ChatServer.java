package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;

import client.ChatClientIF;

public class ChatServer extends UnicastRemoteObject implements ChatServerIF {
    String line = "---------------------------------------------\n";
    public Vector<Rooms> rooms;
    private static final long serialVersionUID = 1L;

    //Constructor
    public ChatServer() throws RemoteException {
        super();
        rooms = new Vector<Rooms>(10, 1);
    }

    //-----------------------------------------------------------

    /**
     * LOCAL METHODS
     */
    public static void main(String[] args) {
        startRMIRegistry();
        String hostName = "localhost";
        String serviceName = "GroupChatService";

        if (args.length == 2) {
            hostName = args[0];
            serviceName = args[1];
        }

        try {
            ChatServerIF hello = new ChatServer();
            Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
            System.out.println("Chat RMI Server is running...");
        } catch (Exception e) {
            System.out.println("Server had problems starting");
        }
    }


    /**
     * Start the RMI Registry
     */
    public static void startRMIRegistry() {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("RMI Server ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    //-----------------------------------------------------------
    /*
     *   REMOTE METHODS
     */

    /**
     * Return a message to client
     */
    public String sayHello(String ClientName) throws RemoteException {
        System.out.println(ClientName + " sent a message");
        return "Hello " + ClientName + " from  chat server";
    }


    /**
     * Send a string ( the latest post, mostly )
     * to all connected clients
     */
    public void updateChat(String name, String nextPost, String roomName) throws RemoteException {
        String message = name + " : " + nextPost + "\n";
        int index_of_room =0;
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getName().equals(roomName)) {
                index_of_room=i;
                break;
            }
        }
        sendToAll(message, index_of_room);
    }

    /**
     * Receive a new client remote reference
     */
    @Override
    public void passIDentity(RemoteRef ref) throws RemoteException {
        //System.out.println("\n" + ref.remoteToString() + "\n");
        try {
            System.out.println(line + ref.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end passIDentity


    /**
     * Receive a new client and display details to the console
     * send on to register method
     */
    @Override
    public void registerListener(String[] details) throws RemoteException {
        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println(details[0] + " has joined the chat session");
        System.out.println(details[0] + "'s hostname : " + details[1]);
        System.out.println(details[0] + "'sRMI service : " + details[2]);
        System.out.println(details[0] + "'sRMI Room : " + details[3]);
        registerChatter(details);
    }

    @Override
    public void roomIDentity(RemoteRef ref) throws RemoteException {
        try {
            System.out.println(line + ref.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void roomListener(String[] details) throws RemoteException {
        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println(details[0] + " created");
        System.out.println(details[0] + "'s hostname : " + details[1]);
        System.out.println(details[0] + "'sRMI service : " + details[2]);
        System.out.println(details[0] + "'sRMI service user : " + details[3]);
        registerRoom(details);
    }


    /**
     * register the clients interface and store it in a reference for
     * future messages to be sent to, ie other members messages of the chat session.
     * send a test message for confirmation / test connection
     *
     * @param details
     */
    private void registerChatter(String[] details) {
        try {
            ChatClientIF nextClient = (ChatClientIF) Naming.lookup("rmi://" + details[1] + "/" + details[2]);
            int index_of_room =0;
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).getName().equals(details[3])) {
                    System.out.println(i+details[0]);
                    index_of_room=i;
                    break;
                }
            }
            System.out.println(  rooms.get(index_of_room).getName());
            rooms.get(index_of_room).chatters.addElement(new Chatter(details[0], nextClient));
            nextClient.messageFromServer("[Server] : Hello " + details[0] + " you are now free to chat.\n");
            sendToAll("[Server] : " + details[0] + " has joined the group.\n", index_of_room);
            updateUserList(rooms.get(index_of_room));
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private void registerRoom(String[] details) {
        try {

            ChatClientIF nextroom = (ChatClientIF) Naming.lookup("rmi://" + details[1] + "/" + details[2]);
                 rooms.addElement(new Rooms(details[0], details[3], details[4], nextroom));
                 updateRoomList();
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update all clients by remotely invoking their
     * updateUserList RMI method
     */
    private void updateUserList(Rooms room) {
        String[] currentUsers = getUserList(rooms.indexOf(room));
        for (Chatter c : room.chatters) {
            try {
                c.getClient().updateUserList(currentUsers);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateRoomList() {
        String[] currentRooms = getRoomList();
        for (Rooms R : rooms) {
            try {
                R.getRoom().updateRoomList(currentRooms);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * generate a String array of current rooms
     *
     * @return
     */
    private String[] getUserList(int roomIndex) {
        // generate an array of current users
        String[] allUsers = new String[rooms.get(roomIndex).chatters.size()];
        for (int i = 0; i < allUsers.length; i++) {
            allUsers[i] = rooms.get(roomIndex).chatters.elementAt(i).getName();
        }
        return allUsers;
    }

    private String[] getRoomList() {
        // generate an array of current users
        String[] allRooms = new String[rooms.size()];
        System.out.println(rooms.size());
        for (int i = 0; i < allRooms.length; i++) {
            allRooms[i] = rooms.elementAt(i).getName();
        }
        return allRooms;
    }


    /**
     * Send a message to all users
     *
     * @param newMessage
     */
    public void sendToAll(String newMessage, int roomIndex) {
        for (Chatter c : rooms.get(roomIndex).chatters) {
            try {
                c.getClient().messageFromServer(newMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * remove a client from the list, notify everyone
     */
    @Override
    public void leaveChat(String userName, String roomName) throws RemoteException {

        int index_of_room =0;
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getName().equals(roomName)) {
                index_of_room=i;
                break;
            }
        }
        for (Chatter c : rooms.get(index_of_room).chatters) {
            if (c.getName().equals(userName)) {
                System.out.println(line + userName + " left the chat session");
                System.out.println(new Date(System.currentTimeMillis()));
                rooms.get(index_of_room).chatters.remove(c);
                break;
            }
        }
        if (!rooms.get(index_of_room).chatters.isEmpty()) {
            updateUserList(rooms.get(index_of_room));
        }
    }


    /**
     * A method to send a private message to selected clients
     * The integer array holds the indexes (from the chatters vector)
     * of the clients to send the message to
     */
    @Override
    public void sendPM(int[] privateGroup, String privateMessage, String roomName) throws RemoteException {
        Chatter pc;
        int index_of_room =0;
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getName().equals(roomName)) {
                index_of_room=i;
                break;
            }
        }
        for (int i : privateGroup) {
            pc = rooms.get(index_of_room).chatters.elementAt(i);
            pc.getClient().messageFromServer(privateMessage);
        }
    }

    public void removetemproom() throws RemoteException {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getName().equals("newRooms:")) {
                System.out.println(i);
                rooms.remove(i);
                break;
            }
        }
        this.updateRoomList();
    }

    @Override
    public String removeRoom(String roomName, String userName, String password) throws RemoteException {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getName().equals(roomName)) {
                if (rooms.get(i).getUsername().equals(userName) && rooms.get(i).getPassword().equals(password)) {
                    rooms.remove(i);
                    this.updateRoomList();
                    return "done";
                } else return "Failure";
            }
        }
        return "This room does not exist";

    }
}
//END CLASS



