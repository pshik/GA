package view;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.ClientGuiController;
import model.User;
import server.MessageType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class UsersSettings extends JFrame{
    private JComboBox cmbUsers;
    private JLabel lblLogin;
    private JLabel lblFirstName;
    private JLabel lblSecondName;
    private JLabel lblRole;
    private JLabel lblEmail;
    private JTextField txtLogin;
    private JTextField txtFirstName;
    private JTextField txtSecondName;
    private JTextField txtEmail;
    private JButton btnExit;
    private JButton btnUpdate;
    private JButton btnCreate;
    private JButton btnDelete;
    private JComboBox cmbRoles;
    private JPanel pnlUsers;
    private JButton btn_Show;
    private JButton btn_Clear;
    private JPasswordField pswdField;
    private JLabel lblPassword;
    private JButton btn_Refresh;
    private ArrayList<User> users = new ArrayList<>();
    public UsersSettings() {
        setTitle("Настройка пользователей");
        setVisible(false);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setLocation(200,200);
        // setAlwaysOnTop(true);
        setContentPane(pnlUsers);
        cmbRoles.addItem(" "); //index 0
        cmbRoles.addItem("Administrator");  //index 1
        cmbRoles.addItem("LogisticManager"); //index 2
        cmbRoles.addItem("StoreKeeper"); //index 3
        cmbRoles.addItem("Driver"); //index 4
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    public void initView(ClientGuiController controller){
        if (controller != null)
        {
            controller.setBusy(true);
        }

        users.addAll(controller.getModel().getUsers());

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setBusy(false);
                controller.getView().refreshRack();
                dispose();
            }
        });
        btn_Refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setBusy(false);
                clearFields();
                users.clear();
                users.addAll(controller.getModel().getUsers());
                addUserToCmb(users);
                controller.setBusy(true);
            }
        });
        btn_Show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User displayingUser = new User();
                for (User u: users){
                    if (u.getLogin().equals(cmbUsers.getSelectedItem())){
                        displayingUser = u;
                    }
                }
                txtLogin.setText(displayingUser.getLogin());
                txtFirstName.setText(displayingUser.getFirstName());
                txtSecondName.setText(displayingUser.getSecondName());
                int i = 0;
                switch (displayingUser.getRole()){
                    case  "Administrator": i=1;break; //index 1
                    case  "LogisticManager": i=2;break; //index 2
                    case  "StoreKeeper": i=3;break; //index 3
                    case  "Driver": i=4;break; //index 4
                }
                cmbRoles.setSelectedIndex(i);
                txtEmail.setText(displayingUser.getEmail());
                pswdField.setText(displayingUser.getPassword());
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        });
        btn_Clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtLogin.getText().equals(controller.getCurrentUser())) {
                    boolean isNotLastAdmin = true;
                    if (cmbRoles.getSelectedIndex() == 1){
                        if (checkLastAdmin()) isNotLastAdmin = false;
                    }
                    if (isNotLastAdmin) {
                        User tmp = new User(txtLogin.getText(), txtFirstName.getText(), txtSecondName.getText(), txtEmail.getText(), cmbRoles.getSelectedItem().toString(), pswdField.getPassword().toString());
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            mapper.writeValue(out, tmp);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        String data = "0" + out.toString();
                        controller.sendMessage(MessageType.CHANGE_USER, data);
                    } else {
                        JOptionPane.showMessageDialog(null, "Нельзя удалять последнего администратора!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Вы не можете изменить текущего пользователя!");
                }
                clearFields();
            }
        });
        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtLogin.getText().isEmpty() && !txtFirstName.getText().isEmpty() && !txtSecondName.getText().isEmpty() && !txtEmail.getText().isEmpty()
                        && cmbRoles.getSelectedIndex() != 0 && !pswdField.getPassword().toString().isEmpty()) {
                    boolean isNewUser = true;
                    for (User u : users) {
                        if (u.getLogin().equals(txtLogin.getText().toLowerCase())) {
                            isNewUser = false;
                            break;
                        }
                    }
                    if (isNewUser) {
                        User tmp = new User(txtLogin.getText().toLowerCase(), txtFirstName.getText(), txtSecondName.getText(), txtEmail.getText(), cmbRoles.getSelectedItem().toString(), String.valueOf(pswdField.getPassword()));
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            mapper.writeValue(out, tmp);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        String data = "1" + out.toString();
                        controller.sendMessage(MessageType.CHANGE_USER, data);
                    } else {
                        JOptionPane.showMessageDialog(null,"Пользователь " + txtLogin.getText().toLowerCase() + " уже существует. Если хотите изменить данные используйте кнопку обновить.");
                    }
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(null, "Заполните все поля!");
                }
                clearFields();
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtLogin.getText().isEmpty() && !txtFirstName.getText().isEmpty() && !txtSecondName.getText().isEmpty() && !txtEmail.getText().isEmpty()
                        && cmbRoles.getSelectedIndex() != 0 && !pswdField.getPassword().toString().isEmpty()) {
                    boolean isLoginExist = false;
                    for (User u : users) {
                        if (txtLogin.getText().toLowerCase().equals(u.getLogin())) {
                            isLoginExist = true;
                            break;
                        }
                    }
                    if (isLoginExist) {
                        boolean isNotLastAdmin = true;

                        for (User u: users){
                            if (u.getLogin().equals(txtLogin.getText()) && !u.getRole().equals(cmbRoles.getSelectedItem())) {
                                    if (checkLastAdmin()) isNotLastAdmin = false;
                            }
                        }

                        if(isNotLastAdmin) {
                            User tmp = new User(txtLogin.getText().toLowerCase(), txtFirstName.getText(), txtSecondName.getText(), txtEmail.getText(), cmbRoles.getSelectedItem().toString(), String.valueOf(pswdField.getPassword()));
                            if (controller != null) {
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                ObjectMapper mapper = new ObjectMapper();
                                try {
                                    mapper.writeValue(out, tmp);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                String data = "1" + out.toString();
                                controller.sendMessage(MessageType.CHANGE_USER, data);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Нельзя менять роль последнего администратора!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null,"Пользователя " + txtLogin.getText().toLowerCase() + " не существует. Если хотите создать нового используйте кнопку создать.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Заполните все поля!");
                }
                clearFields();
            }
        });
        addUserToCmb(users);

        pack();
        setVisible(true);
    }

    private boolean checkLastAdmin() {
        int count = 0;
        for (User u: users){
            if (u.getRole().equals("Administrator")) count++;
        }
        return count <= 1;
    }

    private void clearFields(){
        txtLogin.setText("");
        txtFirstName.setText("");
        txtSecondName.setText("");
        cmbRoles.setSelectedIndex(0);
        txtEmail.setText("");
        pswdField.setText("");
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
    private void addUserToCmb(ArrayList<User> list){
        Collections.sort(list);
        cmbUsers.removeAllItems();
        for (User u: list){
            cmbUsers.addItem(u.getLogin());
        }
    }
}
