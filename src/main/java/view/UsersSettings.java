package view;

import controller.ClientGuiController;
import model.User;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class UsersSettings extends JFrame{
    private JComboBox comboBox1;
    private JLabel lblLogin;
    private JLabel lblFirstName;
    private JLabel lblSecondName;
    private JLabel lblRole;
    private JLabel lblEmail;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField5;
    private JButton btnCancel;
    private JButton btnUpdate;
    private JButton btnCreate;
    private JButton btnDelete;
    private JComboBox cmbRoles;
    private JPanel pnlUsers;

    public UsersSettings() {
        setTitle("Настройка пользователей");
        setVisible(false);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setLocation(200,200);
        // setAlwaysOnTop(true);
        setContentPane(pnlUsers);
    }

    public void initView(ClientGuiController controller, ArrayList<User> users){
        if (controller != null)
        {
            controller.setBusy(true);
        }
        Collections.sort(users);


    }
}
