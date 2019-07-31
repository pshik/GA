package view;

import controller.ClientGuiController;
import model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class UsersSettings extends JFrame{
    private JComboBox cmbUsers;
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
        cmbRoles.addItem("Administrator");
        cmbRoles.addItem("LogisticManager");
        cmbRoles.addItem("StoreKeeper");
        cmbRoles.addItem("Driver");

    }

    public void initView(ClientGuiController controller, ArrayList<User> users){
        if (controller != null)
        {
            controller.setBusy(true);
        }
        Collections.sort(users);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller !=null) {
                    controller.setBusy(false);
                    controller.getView().refreshRack();
                }
                dispose();
            }
        });
        for (User u: users){
            cmbUsers.addItem(u.getFirstName() + " " + u.getSecondName());
        }

        pack();
        setVisible(true);
    }
    public static void main(String[] args) {
        ArrayList<User> users = new ArrayList<>();
        User user1 = new User("dmitriy.suslennikov","Dmitriy","Suslennikov",	"dmitriy.suslennikov@grupoantolin.com",	"Administrator",	"12345");
        User user2 = new User("alexander.tebenkov","Alexander","Tebenkov",	"Alexander.Tebenkov@grupoantolin.com",	"LogisticManager",	"12345");
        User user3 = new User("alexander.bush","Alexander","Bush",	"alexander.bush@grupoantolin.com",	"StoreKeeper",	"12345");
        User user4 = new User("vasya.pupkin","Vasya","Pupkin",	"vasya.pupkin@grupoantolin.com",	"Driver",	"12345");

        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);

        new UsersSettings().initView(null,users);
    }
}
