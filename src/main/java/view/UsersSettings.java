package view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import controller.ClientGuiController;
import model.Rack;
import model.User;
import server.MessageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;

public class UsersSettings extends JFrame {
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
        setLocation(200, 200);
        setAlwaysOnTop(true);
        setContentPane(pnlUsers);
        cmbRoles.addItem(" "); //index 0
        cmbRoles.addItem("Administrator");  //index 1
        cmbRoles.addItem("LogisticManager"); //index 2
        cmbRoles.addItem("StoreKeeper"); //index 3
        cmbRoles.addItem("Driver"); //index 4
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    public void initView(ClientGuiController controller) {
        if (controller != null) {
            controller.setBusy(true);
        }

        users.addAll(controller.getModel().getUsers());

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setBusy(false);
                controller.getView().refreshView();
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
                for (User u : users) {
                    if (u.getLogin().equals(cmbUsers.getSelectedItem())) {
                        displayingUser = u;
                    }
                }
                txtLogin.setText(displayingUser.getLogin());
                txtFirstName.setText(displayingUser.getFirstName());
                txtSecondName.setText(displayingUser.getSecondName());
                int i = 0;
                switch (displayingUser.getRole()) {
                    case "Administrator":
                        i = 1;
                        break; //index 1
                    case "LogisticManager":
                        i = 2;
                        break; //index 2
                    case "StoreKeeper":
                        i = 3;
                        break; //index 3
                    case "Driver":
                        i = 4;
                        break; //index 4
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
                    if (cmbRoles.getSelectedIndex() == 1) {
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
                        JOptionPane.showMessageDialog(null, "Пользователь " + txtLogin.getText().toLowerCase() + " уже существует. Если хотите изменить данные используйте кнопку обновить.");
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

                        for (User u : users) {
                            if (u.getLogin().equals(txtLogin.getText()) && !u.getRole().equals(cmbRoles.getSelectedItem())) {
                                if (checkLastAdmin()) isNotLastAdmin = false;
                            }
                        }

                        if (isNotLastAdmin) {
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
                        JOptionPane.showMessageDialog(null, "Пользователя " + txtLogin.getText().toLowerCase() + " не существует. Если хотите создать нового используйте кнопку создать.");
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
        for (User u : users) {
            if (u.getRole().equals("Administrator")) count++;
        }
        return count <= 1;
    }

    private void clearFields() {
        txtLogin.setText("");
        txtFirstName.setText("");
        txtSecondName.setText("");
        cmbRoles.setSelectedIndex(0);
        txtEmail.setText("");
        pswdField.setText("");
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void addUserToCmb(ArrayList<User> list) {
        Collections.sort(list);
        cmbUsers.removeAllItems();
        for (User u : list) {
            cmbUsers.addItem(u.getLogin());
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        pnlUsers = new JPanel();
        pnlUsers.setLayout(new GridLayoutManager(10, 6, new Insets(0, 0, 0, 0), -1, -1));
        pnlUsers.setBackground(new Color(-3276901));
        cmbUsers = new JComboBox();
        pnlUsers.add(cmbUsers, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtLogin = new JTextField();
        pnlUsers.add(txtLogin, new GridConstraints(2, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        pnlUsers.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final Spacer spacer2 = new Spacer();
        pnlUsers.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer3 = new Spacer();
        pnlUsers.add(spacer3, new GridConstraints(9, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer4 = new Spacer();
        pnlUsers.add(spacer4, new GridConstraints(4, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        btnExit = new JButton();
        this.$$$loadButtonText$$$(btnExit, ResourceBundle.getBundle("strings").getString("btn_Exit"));
        pnlUsers.add(btnExit, new GridConstraints(8, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnUpdate = new JButton();
        this.$$$loadButtonText$$$(btnUpdate, ResourceBundle.getBundle("strings").getString("btn_Update"));
        pnlUsers.add(btnUpdate, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnCreate = new JButton();
        this.$$$loadButtonText$$$(btnCreate, ResourceBundle.getBundle("strings").getString("btn_Create"));
        pnlUsers.add(btnCreate, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnDelete = new JButton();
        this.$$$loadButtonText$$$(btnDelete, ResourceBundle.getBundle("strings").getString("btn_Delete"));
        pnlUsers.add(btnDelete, new GridConstraints(8, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblLogin = new JLabel();
        this.$$$loadLabelText$$$(lblLogin, ResourceBundle.getBundle("strings").getString("txt_Login"));
        pnlUsers.add(lblLogin, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblFirstName = new JLabel();
        this.$$$loadLabelText$$$(lblFirstName, ResourceBundle.getBundle("strings").getString("txt_FirstName"));
        pnlUsers.add(lblFirstName, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblSecondName = new JLabel();
        this.$$$loadLabelText$$$(lblSecondName, ResourceBundle.getBundle("strings").getString("txt_SecondName"));
        pnlUsers.add(lblSecondName, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblRole = new JLabel();
        this.$$$loadLabelText$$$(lblRole, ResourceBundle.getBundle("strings").getString("txt_Group"));
        pnlUsers.add(lblRole, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblEmail = new JLabel();
        this.$$$loadLabelText$$$(lblEmail, ResourceBundle.getBundle("strings").getString("txt_Email"));
        pnlUsers.add(lblEmail, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtFirstName = new JTextField();
        pnlUsers.add(txtFirstName, new GridConstraints(3, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtSecondName = new JTextField();
        pnlUsers.add(txtSecondName, new GridConstraints(4, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtEmail = new JTextField();
        pnlUsers.add(txtEmail, new GridConstraints(6, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        cmbRoles = new JComboBox();
        pnlUsers.add(cmbRoles, new GridConstraints(5, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_Show = new JButton();
        this.$$$loadButtonText$$$(btn_Show, ResourceBundle.getBundle("strings").getString("txt_Show"));
        pnlUsers.add(btn_Show, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_Clear = new JButton();
        this.$$$loadButtonText$$$(btn_Clear, ResourceBundle.getBundle("strings").getString("btn_ClearFields"));
        pnlUsers.add(btn_Clear, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pswdField = new JPasswordField();
        pnlUsers.add(pswdField, new GridConstraints(7, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        lblPassword = new JLabel();
        this.$$$loadLabelText$$$(lblPassword, ResourceBundle.getBundle("strings").getString("txt_Password"));
        pnlUsers.add(lblPassword, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btn_Refresh = new JButton();
        btn_Refresh.setIcon(new ImageIcon(getClass().getResource("/icons/reload.png")));
        btn_Refresh.setIconTextGap(1);
        btn_Refresh.setText("");
        pnlUsers.add(btn_Refresh, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return pnlUsers;
    }

}
