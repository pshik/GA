package view;

import com.google.common.graph.Network;
import controller.ClientGuiController;
import org.apache.logging.log4j.core.appender.db.jpa.JpaAppender;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class Settings extends JFrame{
    private JRadioButton rbNetwork;
    private JRadioButton rbBackUp;
    private JRadioButton rbHistory;
    private JRadioButton rbBlockDays;
    private JButton btnExit;
    private JButton btnApply;
    private JPanel pnlSettings;
    private JPanel pnlRadBut;
    private JPanel pnlButtons;
    private JPanel pnlMain;

    public Settings() {
        setTitle("Настройки");
        setVisible(false);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setLocation(200,200);
        // setAlwaysOnTop(true);
        setContentPane(pnlMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rbNetwork.setSelected(true);

    }

    private void initFrame(ClientGuiController controller) {
        JPanel cardNetwork = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        cardNetwork.setLayout(gridBagLayout);
        GridBagConstraints c =  new GridBagConstraints();
        JTextField ip = new JTextField("172.25.217.30");
        ip.setPreferredSize(new Dimension(100,20));
        c.gridx = 1;
        c.gridy = 0;
        c.fill   = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        gridBagLayout.setConstraints(ip,c);
        JTextField port = new JTextField("4040");
        port.setPreferredSize(new Dimension(100,20));
        c.gridx = 1;
        c.gridy = 1;
        gridBagLayout.setConstraints(port,c);
        JLabel lblIP = new JLabel("IP address: ");
        c.gridx = 0;
        c.gridy = 0;
        gridBagLayout.setConstraints(lblIP,c);
        JLabel lblPort = new JLabel("Port: ");
        c.gridx = 0;
        c.gridy = 1;
        gridBagLayout.setConstraints(lblPort,c);
        cardNetwork.add(ip);
        cardNetwork.add(port);
        cardNetwork.add(lblIP);
        cardNetwork.add(lblPort);

        JPanel cardBlockingDays = new JPanel();
        JFormattedTextField txtBlock = new JFormattedTextField(4);
        NumberFormat integerFieldFormatter = NumberFormat.getIntegerInstance();
        integerFieldFormatter.setGroupingUsed(false);
        DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(new NumberFormatter());
        txtBlock.setFormatterFactory(formatterFactory);
        txtBlock.setPreferredSize(new Dimension(100,20));
        JLabel lblBlock = new JLabel("Blocking period: ");
        cardBlockingDays.add(lblBlock);
        cardBlockingDays.add(txtBlock);

        JPanel cardHistory = new JPanel();
        cardHistory.add(new JButton(("History")));

        JPanel cardBackUp = new JPanel();
        cardBackUp.add(new JButton("BackUp"));

        pnlSettings.add(cardNetwork,rbNetwork.getText());
        pnlSettings.add(cardBlockingDays,rbBlockDays.getText());
        pnlSettings.add(cardHistory,rbHistory.getText());
        pnlSettings.add(cardBackUp,rbBackUp.getText());


        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                controller.setBusy(false);
//                controller.getView().refreshView();
                dispose();
            }
        });
        rbNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) pnlSettings.getLayout();
                layout.show(pnlSettings, rbNetwork.getText());
            }
        });
        rbBackUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) pnlSettings.getLayout();
                layout.show(pnlSettings, rbBackUp.getText());
            }
        });
        rbHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) pnlSettings.getLayout();
                layout.show(pnlSettings, rbHistory.getText());
            }
        });
        rbBlockDays.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) pnlSettings.getLayout();
                layout.show(pnlSettings, rbBlockDays.getText());
            }
        });

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        Settings settings = new Settings();
        settings.initFrame(null);
    }
}
