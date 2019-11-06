package view;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.ClientGuiController;
import model.CheckListItem;
import model.Rack;
import model.SAPReference;
import server.MessageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ReferenceSettings extends JFrame {
    private JComboBox cmbReferences;
    private JButton btnUpdate;
    private JButton btnCreate;
    private JButton btnExit;
    private JLabel lblSize;
    private JLabel lblNewRef;
    private JLabel lblDescription;
    private JComboBox cmbSize;
    private JTextField txtNewRef;
    private JTextField txtDescription;
    private JList lstRacks;
    private JPanel pnlReferences;
    private JButton btnDelete;
    private JButton btnRefresh;
    private ArrayList<Rack> racks = new ArrayList<>();
    private ArrayList<SAPReference> references = new ArrayList<>();

    public ReferenceSettings() {
        setTitle("Настройка материалов");
        setVisible(false);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setLocation(200, 200);
        setAlwaysOnTop(true);
        setContentPane(pnlReferences);
        cmbSize.addItem("Small");
        cmbSize.addItem("Medium");
        cmbSize.addItem("Large");


    }

    public void initView(ClientGuiController controller) {

        if (controller != null) {
            controller.setBusy(true);
        }

        Collections.sort(references);
        racks.addAll(controller.getModel().getRacks());
        references.addAll(controller.getModel().getReferences());
        CheckListItem[] checkBoxes = new CheckListItem[racks.size()];

        cmbReferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cmbReferences.getItemCount() != 0) {
                    SAPReference currentRef = null;
                    for (SAPReference r : references) {
                        if (r.getReference().equals(cmbReferences.getSelectedItem())) {
                            currentRef = r;
                        }
                    }

                    for (int i = 0; i < checkBoxes.length; i++) {
                        checkBoxes[i] = null;
                    }
                    for (int i = 0; i < racks.size(); i++) {
                        CheckListItem tmpCheckItem = new CheckListItem(racks.get(i).getName());
                        if (currentRef != null && currentRef.isAllowedRack(racks.get(i).getName())) {
                            tmpCheckItem.setSelected(true);
                        }
                        checkBoxes[i] = tmpCheckItem;
                    }
                    Arrays.sort(checkBoxes);
                    lstRacks.setListData(checkBoxes);
                    lstRacks.setCellRenderer(new CheckListRenderer());
                    lstRacks.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    cmbSize.setSelectedIndex(currentRef.getSize() - 1);
                    txtDescription.setText(currentRef.getDescription());
                }
            }
        });
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setBusy(false);
                references.clear();
                references.addAll(controller.getModel().getReferences());
                addRefToCmb(references);
                controller.setBusy(true);
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] rackList = allowedRacks();
                if (txtDescription.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Заполните описание!");
                } else {
                    SAPReference tmp = new SAPReference(cmbReferences.getSelectedItem().toString(), txtDescription.getText(), cmbSize.getSelectedIndex() + 1, rackList);
                    if (controller != null) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            mapper.writeValue(out, tmp);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        String data = "1" + out.toString();
                        controller.sendMessage(MessageType.CHANGE_REFERENCE, data);
                    }
                }
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] rackList = allowedRacks();
                SAPReference tmp = new SAPReference(cmbReferences.getSelectedItem().toString(), txtDescription.getText(), cmbSize.getSelectedIndex() + 1, rackList);
                if (controller != null) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        mapper.writeValue(out, tmp);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    String data = "0" + out.toString();
                    controller.sendMessage(MessageType.CHANGE_REFERENCE, data);
                }
            }
        });
        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] rackList = allowedRacks();
                if (txtNewRef.getText().isEmpty() || txtDescription.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Введите название материала и описание!");
                } else {
                    SAPReference tmp = new SAPReference(txtNewRef.getText(), txtDescription.getText(), cmbSize.getSelectedIndex() + 1, rackList);
                    if (controller != null) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            mapper.writeValue(out, tmp);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        String data = "1" + out.toString();
                        controller.sendMessage(MessageType.CHANGE_REFERENCE, data);
                    }
                }
            }
        });
        lstRacks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                JList list = (JList) event.getSource();
                // clicked
                if (event.getClickCount() == 2) {
                    // Double-click detected
                    int index = list.locationToIndex(event.getPoint());
                    CheckListItem item = (CheckListItem) list.getModel().getElementAt(index);
                    item.setSelected(!item.isSelected()); // Toggle selected state
                    list.repaint(list.getCellBounds(index, index));// Repaint cell
                }
            }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.setBusy(false);
                    controller.getView().refreshView();
                }
                dispose();
            }
        });

        addRefToCmb(references);

        pack();
        setVisible(true);
    }

    private void addRefToCmb(ArrayList<SAPReference> list) {
        Collections.sort(list);
        cmbReferences.removeAllItems();
        for (SAPReference r : list) {
            cmbReferences.addItem(r.getReference());
        }
    }

    private String[] allowedRacks() {

        ListModel model = lstRacks.getModel();
        List<CheckListItem> selectedValuesList = new ArrayList();
        for (int i = 0; i < model.getSize(); i++) {
            CheckListItem element = (CheckListItem) model.getElementAt(i);
            if (element.isSelected()) {
                selectedValuesList.add(element);
            }
        }
        String[] rackList = new String[selectedValuesList.size()];
        int next = 0;
        for (CheckListItem o : selectedValuesList) {
            rackList[next++] = o.toString();
        }
        return rackList;
    }

    public static void main(String[] args) {
//        String d = "D4";
//        System.out.println(d.substring(0,1));
//
//        System.out.println(d.substring(1,2));
        char c = 65;
        System.out.println(c);
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
        pnlReferences = new JPanel();
        pnlReferences.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 7, new Insets(0, 0, 0, 0), -1, -1));
        pnlReferences.setBackground(new Color(-3276901));
        pnlReferences.setEnabled(true);
        cmbReferences = new JComboBox();
        pnlReferences.add(cmbReferences, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnUpdate = new JButton();
        this.$$$loadButtonText$$$(btnUpdate, ResourceBundle.getBundle("strings").getString("btn_Update"));
        pnlReferences.add(btnUpdate, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnCreate = new JButton();
        this.$$$loadButtonText$$$(btnCreate, ResourceBundle.getBundle("strings").getString("btn_Create"));
        pnlReferences.add(btnCreate, new com.intellij.uiDesigner.core.GridConstraints(5, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnExit = new JButton();
        this.$$$loadButtonText$$$(btnExit, ResourceBundle.getBundle("strings").getString("btn_Exit"));
        pnlReferences.add(btnExit, new com.intellij.uiDesigner.core.GridConstraints(5, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblSize = new JLabel();
        this.$$$loadLabelText$$$(lblSize, ResourceBundle.getBundle("strings").getString("txt_Size"));
        pnlReferences.add(lblSize, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblNewRef = new JLabel();
        this.$$$loadLabelText$$$(lblNewRef, ResourceBundle.getBundle("strings").getString("txt_NewReference"));
        pnlReferences.add(lblNewRef, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblDescription = new JLabel();
        this.$$$loadLabelText$$$(lblDescription, ResourceBundle.getBundle("strings").getString("txt_Description"));
        pnlReferences.add(lblDescription, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbSize = new JComboBox();
        pnlReferences.add(cmbSize, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtNewRef = new JTextField();
        pnlReferences.add(txtNewRef, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtDescription = new JTextField();
        pnlReferences.add(txtDescription, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        pnlReferences.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 4, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(200, 400), new Dimension(200, 400), new Dimension(200, 400), 0, false));
        lstRacks = new JList();
        scrollPane1.setViewportView(lstRacks);
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        pnlReferences.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        pnlReferences.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        pnlReferences.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(6, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        pnlReferences.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(1, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        btnDelete = new JButton();
        this.$$$loadButtonText$$$(btnDelete, ResourceBundle.getBundle("strings").getString("btn_Delete"));
        pnlReferences.add(btnDelete, new com.intellij.uiDesigner.core.GridConstraints(5, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnRefresh = new JButton();
        btnRefresh.setIcon(new ImageIcon(getClass().getResource("/icons/reload.png")));
        btnRefresh.setText("");
        pnlReferences.add(btnRefresh, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        return pnlReferences;
    }
}
