package client;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SignupGUI extends JFrame {
    private JTextField userName;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField password;
    private JButton signUpButton;
    protected JPanel sign;
    protected  JFrame frame;
    public String username;
    public String Password;
    public SignupGUI() {

        frame = new JFrame("Client Signup");
        frame=this;
        frame.setAlwaysOnTop(true);
        this.setContentPane(sign);
        this.setSize(400,300);
        signUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(!userName.getText().equals("") && !textField2.getText().equals("") && !textField3.getText().equals("") && !password.getText().equals(""))
                {  username=userName.getText();
                    Password =password.getText();
                    System.out.println(username+Password);
                    JOptionPane.showMessageDialog(frame, "signup done!",
                            "start chat", JOptionPane.INFORMATION_MESSAGE);

                    frame.dispose();
                }
                else {
                    JOptionPane.showMessageDialog(frame, "Please enter all fields!",
                            "start chat", JOptionPane.WARNING_MESSAGE);
                }

                }


        });
    }
    public String getUsername(){
        System.out.println(username+Password);
        return username;
    }
    public String getPassword(){
        System.out.println(username+Password);
        return Password;
    }
}
