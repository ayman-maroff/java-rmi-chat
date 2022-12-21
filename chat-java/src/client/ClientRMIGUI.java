package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;

public class ClientRMIGUI extends JFrame implements ActionListener{

    private static final long serialVersionUID = 1L;
    private JPanel textPanel, inputPanel;
    private JTextField textField;
    private JTextField passwordTxt;
    private String name, message,passLogin;
    private final Font meiryoFont = new Font("Meiryo", Font.PLAIN, 14);
    private final Border blankBorder = BorderFactory.createEmptyBorder(10,10,20,10);//top,r,b,l
    private ChatClient chatClient;
    private JList<String> list;
    private DefaultListModel<String> listModel;

    protected JTextArea textArea, userArea;
    protected JFrame frame;
    protected JButton privateMsgButton, startButton, sendButton;
    protected JPanel clientPanel, userPanel;
    protected JLabel user,pass;
    private final String roomName;
private final String userName;
private final String password;

    /**
     * GUI Constructor
     */
    public ClientRMIGUI(String Rname,String username,String pass){
        this.roomName=Rname;
        this.userName=username;
        this.password=pass;
        frame = new JFrame("Client Chat Console");
        //-----------------------------------------
        /*
         * intercept close method, inform server we are leaving
         * then let the system exit.
         */
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                if(chatClient != null){
                    try {
                        sendMessage("Bye all, I am leaving");
                        chatClient.serverIF.leaveChat(name,roomName);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                frame.dispose();
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

        outerPanel.add(getInputPanel(), BorderLayout.CENTER);
        outerPanel.add(getTextPanel(), BorderLayout.NORTH);

        c.setLayout(new BorderLayout());
        c.add(outerPanel, BorderLayout.CENTER);
        c.add(getUsersPanel(), BorderLayout.WEST);

        frame.add(c);
        frame.pack();
        frame.setAlwaysOnTop(true);
        frame.setLocation(150, 150);
        textField.requestFocus();

        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


    /**
     * Method to set up the JPanel to display the chat text
     * @return
     */
    public JPanel getTextPanel(){
        String welcome = "Welcome enter your name and press Start to begin\n";
        textArea = new JTextArea(welcome, 14, 34);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setFont(meiryoFont);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textPanel = new JPanel();
        textPanel.add(scrollPane);

        textPanel.setFont(new Font("Meiryo", Font.PLAIN, 14));
        return textPanel;
    }

    /**
     * Method to build the panel with input field
     * @return inputPanel
     */
    public JPanel getInputPanel(){
        inputPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        inputPanel.setBorder(blankBorder);
        textField = new JTextField();
        user=new JLabel("username:");
        pass=new JLabel("password:");
        passwordTxt=new JTextField();
        inputPanel.add(user);
        textField.setFont(meiryoFont);
        inputPanel.add(textField);
        inputPanel.add(pass);
        inputPanel.add(passwordTxt);

        return inputPanel;
    }

    /**
     * Method to build the panel displaying currently connected users
     * with a call to the button panel building method
     * @return
     */
    public JPanel getUsersPanel(){

        userPanel = new JPanel(new BorderLayout());
        String  userStr = " Current Users      ";

        JLabel userLabel = new JLabel(userStr, JLabel.CENTER);
        userPanel.add(userLabel, BorderLayout.NORTH);
        userLabel.setFont(new Font("Meiryo", Font.PLAIN, 16));

        String[] noClientsYet = {"No other users"};
        setClientPanel(noClientsYet);

        clientPanel.setFont(meiryoFont);
        userPanel.add(makeButtonPanel(), BorderLayout.SOUTH);
        userPanel.setBorder(blankBorder);

        return userPanel;
    }

    /**
     * Populate current user panel with a
     * selectable list of currently connected users
     * @param currClients
     */
    public void setClientPanel(String[] currClients) {
        clientPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<String>();

        for(String s : currClients){
            listModel.addElement(s);
        }
        if(currClients.length > 1){
            privateMsgButton.setEnabled(true);
        }

        //Create the list and put it in a scroll pane.
        list = new JList<String>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(8);
        list.setFont(meiryoFont);
        JScrollPane listScrollPane = new JScrollPane(list);

        clientPanel.add(listScrollPane, BorderLayout.CENTER);
        userPanel.add(clientPanel, BorderLayout.CENTER);
    }

    /**
     * Make the buttons and add the listener
     * @return
     */
    public JPanel makeButtonPanel() {
        sendButton = new JButton("Send ");
        sendButton.addActionListener(this);
        sendButton.setEnabled(false);

        privateMsgButton = new JButton("Send PM");
        privateMsgButton.addActionListener(this);
        privateMsgButton.setEnabled(false);

        startButton = new JButton("Start ");
        startButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
        buttonPanel.add(privateMsgButton);
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(startButton);
        buttonPanel.add(sendButton);

        return buttonPanel;
    }

    /**
     * Action handling on the buttons
     */
    @Override
    public void actionPerformed(ActionEvent e){

        try {
            //get connected to chat service
            if(e.getSource() == startButton){
                name = textField.getText();
                passLogin = passwordTxt.getText();
                if(name.length() != 0&&passLogin.length()!=0) {
                    if (name.equals(userName)&&password.equals(passLogin)) {
                        frame.setTitle(name + "'s console in "+roomName);
                        textField.setText("");
                        textArea.append("username : " + name + " connecting to chat...\n");
                        getConnected(name);
                        if (!chatClient.connectionProblem) {
                            startButton.setEnabled(false);
                            sendButton.setEnabled(true);
                            inputPanel.remove(user);
                            inputPanel.remove(pass);
                            inputPanel.remove(passwordTxt);

                        }
                    }else{
                        JOptionPane.showMessageDialog(frame, "Incorrect username or password");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(frame, "Enter your name and password to Start");
                }
            }

            //get text and clear textField
            if(e.getSource() == sendButton){
                message = textField.getText();
                textField.setText("");
                sendMessage(message);
                System.out.println("Sending message : " + message);
            }

            //send a private message, to selected users
            if(e.getSource() == privateMsgButton){
                int[] privateList = list.getSelectedIndices();

                for(int i=0; i<privateList.length; i++){
                    System.out.println("selected index :" + privateList[i]);
                }
                message = textField.getText();
                textField.setText("");
                sendPrivate(privateList);
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
    private void sendMessage(String chatMessage) throws RemoteException {
        chatClient.serverIF.updateChat(name, chatMessage,roomName);
    }


    private void sendPrivate(int[] privateList) throws RemoteException {
        String privateMessage = "[PM from " + name + "] :" + message + "\n";
        chatClient.serverIF.sendPM(privateList, privateMessage,roomName);
    }

    /**
     * Make the connection to the chat server
     * @param userName
     * @throws RemoteException
     */
    private void getConnected(String userName) throws RemoteException{
        //remove whitespace and non word characters to avoid malformed url
        String cleanedUserName = userName.replaceAll("\\s+","_");
        cleanedUserName = userName.replaceAll("\\W+","_");
        try {
            chatClient = new ChatClient(this, cleanedUserName,roomName);
            chatClient.startClient();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}//end class










