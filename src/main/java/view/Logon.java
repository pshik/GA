package view;

import controller.ServerController;
import dao.Base;
import log.LogParser;
import log.LoggerFiFo;
import model.User;
import org.apache.logging.log4j.Level;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ConcurrentMap;

public class Logon extends JFrame{
    private  JButton btnEnter;
    private  JComboBox cmbLogin;
    private  JPasswordField txtPassword;
    private  JPanel jpnlLogon;
    private  JLabel lblStatus;
    private Base base ;//= new Base();
    private  ConcurrentMap users ;
    private ServerController serverController;

   // private static final Logger logger = LoggerFactory.getLogger(Logon.class);
    public Logon(ServerController serverController, Base base) {
        this.base = base;
        this.users =  base.getBase("Users");
        this.serverController = serverController;
        setTitle("Logon");

        txtPassword.setEchoChar('*');
        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
        lblStatus.setBorder(border);
        lblStatus.setOpaque(true);
        lblStatus.setBackground(Color.lightGray);
            for (Object o: users.keySet()){
                cmbLogin.addItem(o);
            }

        JButton btnLogin = getBtnEnter();
        JComboBox cmbLogin = getCmbLogin();
        JLabel lblStatus = getLblStatus();
        JPasswordField txtfPassword = getTxtPassword();
        setContentPane(jpnlLogon);
        setBounds(200,100,500,300);
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                base.closeDB();
                System.exit(0);
            }
        };
        addWindowListener(exitListener);
        setVisible(true);
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (validation(txtfPassword,cmbLogin.getSelectedItem().toString())){
                    lblStatus.setText("Success");
                    lblStatus.setForeground(Color.BLACK);
                    lblStatus.setBackground(Color.GREEN);
                    serverController.setAccess(true);
                    new MonopolyMode(serverController,base,cmbLogin.getSelectedItem().toString()).setVisible(true);
                    dispose();
                  //  LoggerFiFo.getInstance().getRootLogger().log(350,"User " + cmbLogin.getSelectedItem() + " login");

                    LoggerFiFo.getInstance().getRootLogger().log(Level.forName("SECURITY",350),"User " + cmbLogin.getSelectedItem() + " login");
                    LogParser.getInstance().updateLog();
               //     LoggerFiFo.getInstance().getRootLogger().lo
                   // serverController.printLog();

                } else{
                    lblStatus.setText("Fail");
                    lblStatus.setForeground(Color.BLACK);
                    lblStatus.setBackground(Color.RED);
                    JOptionPane.showMessageDialog(btnLogin,"Не верный пароль, попробуйте еще раз.");
                }
            }
        });

    }
    private boolean validation(JPasswordField passwordField,String userName){
        User tmp = (User) users.get(userName);
        char[] password = passwordField.getPassword();
        if (String.valueOf(password).equals(tmp.getPassword())) return true;

        return false;
    }

    private JButton getBtnEnter() {
        return btnEnter;
    }

    private JComboBox getCmbLogin() {
        return cmbLogin;
    }

    private JLabel getLblStatus() {
        return lblStatus;
    }

    private JPasswordField getTxtPassword() {
        return txtPassword;
    }

}
