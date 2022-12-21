package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;

public class ChatRoomGUI extends JFrame implements ActionListener{

    private static final long serialVersionUID = 1L;
    private JPanel textPanel, inputPanel;
    private String newRoom;
    private String deleteRoom;
    private final Font meiryoFont = new Font("Meiryo", Font.PLAIN, 14);
    private final Border blankBorder = BorderFactory.createEmptyBorder(10,10,20,10);//top,r,b,l
    public ChatClient chatClient ;
    private JList<String> list;
    private DefaultListModel<String> listModel;
    protected JFrame frame;
    protected JButton signupButton, createRoomButton, startButton,showrooms,removeRoom,logout;
    protected JPanel RoomPanel, userPanel;

    private String roomName ;
    private TextField roomname;
    private String username ="";
    private String password="";
    public SignupGUI sign;
    /**
     * Main method to start client GUI app.
     * @param args
     */
    public static void main(String[] args) throws RemoteException {
        //set the look and feel to 'Nimbus'

        try{
            for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
                if("Nimbus".equals(info.getName())){
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch(Exception e){
        }
        new ChatRoomGUI();

    }//end main


    /**
     * GUI Constructor
     */
    public ChatRoomGUI() throws RemoteException {
        frame = new JFrame("Client Chat Console");

        //-----------------------------------------
        /*
         * intercept close method, inform server we are leaving
         * then let the system exit.
         */
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                System.exit(0);
            }
        });
        //-----------------------------------------
        //remove window buttons and border frame
        //to force user to exit on a button
        //- one way to control the exit behaviour
        //frame.setUndecorated(true);
        //frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

        Container c = getContentPane();
        JPanel outerPanel = new JPanel(new BorderLayout());
        c.setLayout(new BorderLayout());
        c.add(outerPanel, BorderLayout.CENTER);
        c.add(getRoomPanel(), BorderLayout.WEST);

        frame.add(c);
        frame.pack();
        frame.setAlwaysOnTop(true);
        frame.setLocation(150, 150);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


    /**
     * Method to set up the JPanel to display the chat text
     * @return
     */

    /**
     * Method to build the panel with input field
     * @return inputPanel
     */
    /**
     * Method to build the panel displaying currently connected users
     * with a call to the button panel building method
     * @return
     */
    public JPanel getRoomPanel(){

        userPanel = new JPanel(new BorderLayout());
        String  userStr = " Current Rooms     ";

        JLabel userLabel = new JLabel(userStr, JLabel.CENTER);
        userPanel.add(userLabel, BorderLayout.NORTH);
        userLabel.setFont(new Font("Meiryo", Font.PLAIN, 16));

        String[] noRoomsYet = {"No other Rooms"};
        setRoomPanel(noRoomsYet);

        RoomPanel.setFont(meiryoFont);
        userPanel.add(makeButtonPanel(), BorderLayout.SOUTH);
        userPanel.setBorder(blankBorder);

        return userPanel;
    }

    /**
     * Populate current user panel with a
     * selectable list of currently connected users
     * @param currRooms
     */
    public void setRoomPanel(String[] currRooms) {
        RoomPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<String>();

        for(String s : currRooms){
            listModel.addElement(s);
        }

        //Create the list and put it in a scroll pane.
        list = new JList<String>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(8);
        list.setFont(meiryoFont);
        JScrollPane listScrollPane = new JScrollPane(list);

        RoomPanel.add(listScrollPane, BorderLayout.CENTER);
        userPanel.add(RoomPanel, BorderLayout.CENTER);
    }

    /**
     * Make the buttons and add the listener
     * @return
     */
    public JPanel makeButtonPanel() {
        signupButton = new JButton("Signup ");
        logout=new JButton(("Sign out"));
        logout.addActionListener(this);
        signupButton.addActionListener(this);
        removeRoom=new JButton("delete Room");
        removeRoom.addActionListener(this);
        createRoomButton = new JButton("Create Room");
        createRoomButton.addActionListener(this);
        showrooms=new JButton("Rooms");
        showrooms.addActionListener(this);
        startButton = new JButton("Start ");
        startButton.addActionListener(this);
        roomname =new TextField();
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
        buttonPanel.add(signupButton);
        buttonPanel.add(startButton);
        buttonPanel.add(showrooms);
        buttonPanel.add(new JLabel("room name:"));
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(roomname);
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(new JLabel(""));
        //buttonPanel.add(new JLabel(""));
        buttonPanel.add(createRoomButton);

        buttonPanel.add(removeRoom);
        buttonPanel.add(logout);
        return buttonPanel;
    }

    /**
     * Action handling on the buttons
     */
    @Override
    public void actionPerformed(ActionEvent e){

        try {
            //get connected to chat service
            if(e.getSource() == createRoomButton){
                if(sign!=null){
                    username=this.sign.getUsername();
                    password=this.sign.getPassword();
                    signupButton.setEnabled(false);
                }

                if(username.equals("")==false&&password.equals("")==false) {
                    newRoom = roomname.getText();
                    System.out.println(newRoom);
                    if (newRoom.length() != 0) {
                        roomname.setText("");

                        Addroom(newRoom);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Enter room name to create");
                    }
                }
                else {
                    JOptionPane.showMessageDialog(frame, "sign up first");
                }
            }

            //get text and clear textField
            if(e.getSource() == signupButton){
                this.sign = new SignupGUI();
                this. sign.setContentPane(sign.sign);
                this.sign.setVisible(true);

            }
            if(e.getSource() == showrooms){

                DisplayRooms();

            }
            if(e.getSource() == logout){
                if(sign!=null){
                    username=this.sign.getUsername();
                    password=this.sign.getPassword();
                }
                if(username.equals("")==false&&password.equals("")==false) {
                    frame.dispose();
                }
                else {
                    JOptionPane.showMessageDialog(frame, "sign up first");
                }

            }
            if(e.getSource() == removeRoom){
                if(sign!=null){
                    username=this.sign.getUsername();
                    password=this.sign.getPassword();
                }

                if(username.equals("")==false&&password.equals("")==false) {
                    deleteRoom = roomname.getText();
                    System.out.println(deleteRoom);
                    if (deleteRoom.length() != 0) {
                        roomname.setText("");
                        String answer = deleteRoom(deleteRoom,username,password);
                        if(answer.equals("done"))
                            JOptionPane.showMessageDialog(frame, "The room has been successfully deleted");
                        if(answer.equals("Failure"))
                            JOptionPane.showMessageDialog(frame, "You do not have the right to delete this room");
                        if(answer.equals("This room does not exist"))
                            JOptionPane.showMessageDialog(frame, "This room does not exist");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Enter room name to delete");
                    }
                }
                else {
                    JOptionPane.showMessageDialog(frame, "sign up first");
                }

            }
            //send a private message, to selected users
            if(e.getSource() == startButton){
                if(sign!=null){
                    username=this.sign.getUsername();
                    password=this.sign.getPassword();
                }
                if(username.equals("")==false&&password.equals("")==false) {
                    int[] privateList = list.getSelectedIndices();

                    for(int i=0; i<privateList.length; i++){
                        System.out.println("selected index :" + privateList[i]);
                    }
                    if(privateList.length==0)
                    {
                        JOptionPane.showMessageDialog(frame, "Please select a room!",
                                "start chat", JOptionPane.WARNING_MESSAGE);
                    }
                    else {
                        roomName = list.getSelectedValue();
                        ClientRMIGUI client = new ClientRMIGUI(roomName,username,password);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(frame, "sign up first");
                }

            }

        }
        catch (RemoteException remoteExc) {
            remoteExc.printStackTrace();
        }

    }//end actionPerformed

    // --------------------------------------------------------------------

    /**
     * Send a message, to be relayed to all chatters
     * @param chatMessage
     * @throws RemoteException
     */


    /**
     * Make the connection to the chat server
     * @param RoomName
     * @throws RemoteException
     */
    private void Addroom(String RoomName) throws RemoteException{
        //remove whitespace and non word characters to avoid malformed url
        String cleanedRoomName = RoomName.replaceAll("\\s+","_");
        cleanedRoomName = RoomName.replaceAll("\\W+","_");
        System.out.println(cleanedRoomName);
        try {
            this.chatClient  = new ChatClient(this, cleanedRoomName,username,password);
            this.chatClient.startRoom();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private void DisplayRooms()throws RemoteException{
        this.chatClient  = new ChatClient(this, "newRooms:","default","123456");
        this.chatClient.startRoom();
        this.chatClient.ShowRooms();
    }
    private String deleteRoom(String roomName,String username,String password)throws RemoteException{
       return this.chatClient.removeRoom(roomName,username,password);
    }
}//end class










