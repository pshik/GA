package view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import controller.ClientGuiController;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class History extends JFrame {
    private JPanel pnlMain;
    private JTextArea txtAHistory;
    private JButton btnShow;
    private JTextField txtFStartDate;
    private JTextField txtFEndDate;
    private JButton btnDataStart;
    private JButton btnDataFinish;
    private JButton btnExit;
    private LocalDateTime startDate = null;
    private LocalDateTime endDate = null;

    public History() {
        setTitle("Просмотр истории");
        setVisible(false);
        //  setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setLocation(200, 200);
        // setAlwaysOnTop(true);
        setContentPane(pnlMain);

    }

    public void initView(ClientGuiController controller) {

        TreeMap<LocalDateTime, String> log = controller.getLog();
        startDate = LocalDateTime.now();
        endDate = LocalDateTime.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd MMM YYYY", new Locale("ru"));
        for (LocalDateTime dateTime : log.keySet()) {
            if (dateTime.isBefore(startDate)) {
                startDate = dateTime;
            }
        }
        txtFStartDate.setText(startDate.format(f));
        txtFEndDate.setText(endDate.format(f));
        String manualDate = null;
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        btnDataStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                UtilDateModel modelDateUtil = new UtilDateModel();
                JDatePanelImpl datePanel = new JDatePanelImpl(modelDateUtil, p);
                JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
                JOptionPane.showConfirmDialog(null, datePicker, "Выбор даты", JOptionPane.PLAIN_MESSAGE);

                Calendar cal = Calendar.getInstance();
                cal.set(datePicker.getModel().getYear(), datePicker.getModel().getMonth(), datePicker.getModel().getDay(), 0, 0, 0);
                Instant instant = cal.getTime().toInstant();
                startDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                txtFStartDate.setText(startDate.format(f));
            }
        });
        btnDataFinish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                UtilDateModel modelDateUtil = new UtilDateModel();
                JDatePanelImpl datePanel = new JDatePanelImpl(modelDateUtil, p);
                JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
                JOptionPane.showConfirmDialog(null, datePicker, "Выбор даты", JOptionPane.PLAIN_MESSAGE);

                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(2);
                cal.set(datePicker.getModel().getYear(), datePicker.getModel().getMonth(), datePicker.getModel().getDay(), 23, 59, 59);
                endDate = LocalDateTime.ofInstant(cal.getTime().toInstant(), ZoneId.systemDefault());
                txtFEndDate.setText(endDate.format(f));
            }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnShow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtAHistory.setText("");
                for (LocalDateTime dateTime : log.keySet()) {
                    if (dateTime.isAfter(startDate) && dateTime.isBefore(endDate)) {
                        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM HH:mm");
                        dateTime.format(f);
                        String mes = dateTime.format(f) + " - " + log.get(dateTime) + "\n";
                        try {
                            txtAHistory.getDocument().insertString(0, mes, null);
                        } catch (BadLocationException exc) {
                            exc.printStackTrace();
                        }
                    }
                }
            }
        });

        pack();
        setVisible(true);
    }

    private class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

        private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.ENGLISH);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
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
        pnlMain = new JPanel();
        pnlMain.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(6, 3, new Insets(5, 0, 5, 5), -1, -1));
        pnlMain.add(panel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, true));
        txtFStartDate = new JTextField();
        txtFStartDate.setEditable(false);
        txtFStartDate.setToolTipText("");
        panel1.add(txtFStartDate, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(0);
        label1.setHorizontalTextPosition(0);
        label1.setText("История за период:");
        panel1.add(label1, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnDataStart = new JButton();
        btnDataStart.setIcon(new ImageIcon(getClass().getResource("/icons/calendar.png")));
        btnDataStart.setText("");
        panel1.add(btnDataStart, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtFEndDate = new JTextField();
        txtFEndDate.setEditable(false);
        panel1.add(txtFEndDate, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        btnDataFinish = new JButton();
        btnDataFinish.setIcon(new ImageIcon(getClass().getResource("/icons/calendar.png")));
        btnDataFinish.setText("");
        panel1.add(btnDataFinish, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Start:");
        panel1.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("End:");
        panel1.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnShow = new JButton();
        this.$$$loadButtonText$$$(btnShow, ResourceBundle.getBundle("strings").getString("txt_Show"));
        panel1.add(btnShow, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        btnExit = new JButton();
        this.$$$loadButtonText$$$(btnExit, ResourceBundle.getBundle("strings").getString("btn_Exit"));
        panel1.add(btnExit, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        pnlMain.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(500, 400), new Dimension(500, 400), new Dimension(500, 400), 1, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null));
        txtAHistory = new JTextArea();
        txtAHistory.setEditable(false);
        scrollPane1.setViewportView(txtAHistory);
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
