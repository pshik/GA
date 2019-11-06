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
import java.util.ResourceBundle;

public class Settings extends JFrame {
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
        setLocation(200, 200);
        setAlwaysOnTop(true);
        setContentPane(pnlMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rbNetwork.setSelected(true);

    }

    private void initFrame(ClientGuiController controller) {
        JPanel cardNetwork = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        cardNetwork.setLayout(gridBagLayout);
        GridBagConstraints c = new GridBagConstraints();
        JTextField ip = new JTextField("172.25.217.30");
        ip.setPreferredSize(new Dimension(100, 20));
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        gridBagLayout.setConstraints(ip, c);
        JTextField port = new JTextField("4040");
        port.setPreferredSize(new Dimension(100, 20));
        c.gridx = 1;
        c.gridy = 1;
        gridBagLayout.setConstraints(port, c);
        JLabel lblIP = new JLabel("IP address: ");
        c.gridx = 0;
        c.gridy = 0;
        gridBagLayout.setConstraints(lblIP, c);
        JLabel lblPort = new JLabel("Port: ");
        c.gridx = 0;
        c.gridy = 1;
        gridBagLayout.setConstraints(lblPort, c);
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
        txtBlock.setPreferredSize(new Dimension(100, 20));
        JLabel lblBlock = new JLabel("Blocking period: ");
        cardBlockingDays.add(lblBlock);
        cardBlockingDays.add(txtBlock);

        JPanel cardHistory = new JPanel();
        cardHistory.add(new JButton(("History")));

        JPanel cardBackUp = new JPanel();
        cardBackUp.add(new JButton("BackUp"));

        pnlSettings.add(cardNetwork, rbNetwork.getText());
        pnlSettings.add(cardBlockingDays, rbBlockDays.getText());
        pnlSettings.add(cardHistory, rbHistory.getText());
        pnlSettings.add(cardBackUp, rbBackUp.getText());


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
        pnlMain = new JPanel();
        pnlMain.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        pnlRadBut = new JPanel();
        pnlRadBut.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(5, 5, 5, 0), -1, -1));
        pnlMain.add(pnlRadBut, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        rbNetwork = new JRadioButton();
        this.$$$loadButtonText$$$(rbNetwork, ResourceBundle.getBundle("strings").getString("rb_Settings"));
        pnlRadBut.add(rbNetwork, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rbBackUp = new JRadioButton();
        this.$$$loadButtonText$$$(rbBackUp, ResourceBundle.getBundle("strings").getString("rb_BackUp"));
        pnlRadBut.add(rbBackUp, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rbHistory = new JRadioButton();
        this.$$$loadButtonText$$$(rbHistory, ResourceBundle.getBundle("strings").getString("rb_History"));
        pnlRadBut.add(rbHistory, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rbBlockDays = new JRadioButton();
        this.$$$loadButtonText$$$(rbBlockDays, ResourceBundle.getBundle("strings").getString("rb_Block"));
        pnlRadBut.add(rbBlockDays, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlSettings = new JPanel();
        pnlSettings.setLayout(new CardLayout(0, 0));
        pnlMain.add(pnlSettings, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null));
        pnlButtons = new JPanel();
        pnlButtons.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 5, 5), -1, -1));
        pnlMain.add(pnlButtons, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnExit = new JButton();
        this.$$$loadButtonText$$$(btnExit, ResourceBundle.getBundle("strings").getString("btn_Exit"));
        pnlButtons.add(btnExit, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnApply = new JButton();
        this.$$$loadButtonText$$$(btnApply, ResourceBundle.getBundle("strings").getString("btn_Apply"));
        pnlButtons.add(btnApply, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(rbHistory);
        buttonGroup.add(rbBackUp);
        buttonGroup.add(rbNetwork);
        buttonGroup.add(rbBlockDays);
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
        return pnlMain;
    }
}
