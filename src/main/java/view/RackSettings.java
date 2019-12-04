package view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import controller.ClientGuiController;
import model.Cell;
import model.CheckListItem;
import model.Rack;
import model.SAPReference;
import server.MessageType;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class RackSettings extends JFrame {
    private JComboBox cmbRacks;
    private JButton btnCreate;
    private JButton btnExit;
    private JButton btnDelete;
    private JButton btnUpdate;
    private JList lstReferences;
    private JPanel pnlRacks;
    private JButton btnRefresh;
    private JFormattedTextField fTxtRowsNum;
    private JFormattedTextField fTxtRackName;
    private JFormattedTextField fTxtColumnNum;
    private JList lstCells;
    private ArrayList<Rack> racks = new ArrayList<>();
    private ArrayList<SAPReference> references = new ArrayList<>();

    public RackSettings() {
        setTitle("Настройка стеллажей");
        setVisible(false);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setLocation(200, 200);
        setAlwaysOnTop(true);
        setContentPane(pnlRacks);
        NumberFormat integerFieldFormatter = NumberFormat.getIntegerInstance();
        integerFieldFormatter.setGroupingUsed(false);
        DefaultFormatterFactory formatterFactory =
                new DefaultFormatterFactory(new NumberFormatter());
        fTxtRowsNum.setFormatterFactory(formatterFactory);
        fTxtColumnNum.setFormatterFactory(formatterFactory);
    }

    public void initView(ClientGuiController controller) {
        if (controller != null) {
            controller.setBusy(true);
        }
        racks.addAll(controller.getModel().getRacks());
        references.addAll(controller.getModel().getReferences());
        CheckListItem[] checkBoxesReferences = new CheckListItem[references.size()];

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setBusy(false);
                controller.getView().refreshView();
                dispose();
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (racks.size() > 1) {
                    sendData(controller, 0, true);
                } else {
                    JOptionPane.showMessageDialog(null, "Не рекомендуется удалять последний стеллаж!");
                }
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fTxtRackName.getText().length() <= 10) {
                    if (!fTxtRackName.getText().isEmpty() && !fTxtColumnNum.getText().isEmpty() && !fTxtRowsNum.getText().isEmpty()) {
                        boolean isRackExist = false;
                        for (Rack r : racks) {
                            if (r.getName().equals(fTxtRackName.getText())) {
                                isRackExist = true;
                                break;
                            }
                        }
                        if (isRackExist) {
                            sendData(controller, 1, false);
                        } else {
                            JOptionPane.showMessageDialog(null, "Стеллажа " + fTxtRackName.getText() + " не существует. Если хотите создать новый стеллаж, используйте кнопку создать.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Заполните все поля!");
                    }
                }
            }
        });
        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fTxtRackName.getText().length() <= 10) {
                    if (!fTxtRackName.getText().isEmpty() && !fTxtColumnNum.getText().isEmpty() && !fTxtRowsNum.getText().isEmpty()) {
                        boolean isRackExist = false;
                        for (Rack r : racks) {
                            if (r.getName().equals(fTxtRackName.getText())) {
                                isRackExist = true;
                                break;
                            }
                        }
                        if (!isRackExist) {
                            sendData(controller, 1, true);
                        } else {
                            JOptionPane.showMessageDialog(null, "Стеллаж " + fTxtRackName.getText() + " уже существует. Если хотите изменить данные используйте кнопку обновить.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Заполните все поля!");
                    }
                }
            }
        });
        cmbRacks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cmbRacks.getItemCount() != 0) {
                    Rack currentRack = null;
                    for (Rack r : racks) {
                        if (r.getName().equals(cmbRacks.getSelectedItem())) {
                            currentRack = r;
                        }
                    }
                    Cell[][] cells = currentRack.getCells();

                    CheckListItem[] checkBoxesCells = new CheckListItem[cells.length * cells[0].length];
                    Arrays.fill(checkBoxesReferences, null);
                    Arrays.fill(checkBoxesCells, null);

                    for (int i = 0; i < references.size(); i++) {
                        CheckListItem tmpCheckItem = new CheckListItem(references.get(i).getReference());
                        if (references.get(i).isAllowedRack(cmbRacks.getSelectedItem().toString())) {
                            tmpCheckItem.setSelected(true);
                        }
                        checkBoxesReferences[i] = tmpCheckItem;
                    }
                    int count = 0;
                    for (int i = 0; i < currentRack.getRow(); i++) {
                        for (int j = 0; j < currentRack.getCol(); j++) {
                            CheckListItem tmpCheckItem = new CheckListItem(cells[i][j].getName());
                            if (cells[i][j].isBlocked()) {
                                tmpCheckItem.setSelected(true);
                            }
                            checkBoxesCells[count] = tmpCheckItem;
                            count++;
                        }
                    }
                    Arrays.sort(checkBoxesReferences);
                    Arrays.sort(checkBoxesCells);
                    lstReferences.setListData(checkBoxesReferences);
                    lstReferences.setCellRenderer(new CheckListRenderer());
                    lstCells.setListData(checkBoxesCells);
                    lstCells.setCellRenderer(new CheckListRenderer());
                    lstReferences.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    lstCells.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    fTxtRackName.setText(currentRack.getName());
                    fTxtRowsNum.setText(String.valueOf(currentRack.getRow()));
                    fTxtColumnNum.setText(String.valueOf(currentRack.getCol()));
                }
            }
        });
        lstReferences.addMouseListener(new MouseAdapter() {
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
        lstCells.addMouseListener(new MouseAdapter() {
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
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setBusy(false);
                references.clear();
                references.addAll(controller.getModel().getReferences());
                racks.clear();
                racks.addAll(controller.getModel().getRacks());
                addRacksToCmb(racks);
                controller.setBusy(true);
            }
        });
        addRacksToCmb(racks);
        pack();
        setVisible(true);
    }


    private void addRacksToCmb(ArrayList<Rack> list) {
        Collections.sort(list);
        cmbRacks.removeAllItems();
        for (Rack r : list) {
            cmbRacks.addItem(r.getName());
        }
    }

    private void sendData(ClientGuiController controller, int action, boolean isNew) {
        Rack tmpRack = new Rack(fTxtRackName.getText(), Integer.parseInt(fTxtRowsNum.getText()), Integer.parseInt(fTxtColumnNum.getText()), null);


        ListModel referencesModel = lstReferences.getModel();
        List<CheckListItem> selectedReferencesList = new ArrayList();
        for (int i = 0; i < referencesModel.getSize(); i++) {
            CheckListItem element = (CheckListItem) referencesModel.getElementAt(i);
            if (element.isSelected()) {
                selectedReferencesList.add(element);
            }
        }
        String refString = "";
        for (CheckListItem o : selectedReferencesList) {
            refString = refString + o.toString() + ",";
        }
        if (!isNew) {
            ListModel cellsModel = lstCells.getModel();
            for (int i = 0; i < cellsModel.getSize(); i++) {
                CheckListItem element = (CheckListItem) cellsModel.getElementAt(i);
                if (element.isSelected()) {
                    Cell tmpCell = tmpRack.getCellByName(element.toString());
                    tmpCell.setBlocked(true);
                }
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(out, tmpRack);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String data = action + out.toString() + controller.getMESSAGE_DELIMITER() + refString;
        controller.sendMessage(MessageType.CHANGE_RACK, data);
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
        pnlRacks = new JPanel();
        pnlRacks.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        pnlRacks.setBackground(new Color(-3276901));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 3, new Insets(10, 10, 0, 10), -1, -1));
        panel1.setBackground(new Color(-3276901));
        pnlRacks.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(24, 195), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(200, 400), new Dimension(200, 400), new Dimension(200, 400), 0, false));
        lstReferences = new JList();
        scrollPane1.setViewportView(lstReferences);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setBackground(new Color(-3276901));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cmbRacks = new JComboBox();
        panel2.add(cmbRacks, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnRefresh = new JButton();
        btnRefresh.setIcon(new ImageIcon(getClass().getResource("/icons/reload.png")));
        btnRefresh.setText("");
        panel2.add(btnRefresh, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setBackground(new Color(-3276901));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("strings").getString("txt_RackName"));
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("strings").getString("txt_RackColumns"));
        panel3.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        this.$$$loadLabelText$$$(label3, ResourceBundle.getBundle("strings").getString("txt_RackRows"));
        panel3.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fTxtRackName = new JFormattedTextField();
        panel3.add(fTxtRackName, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fTxtColumnNum = new JFormattedTextField();
        panel3.add(fTxtColumnNum, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fTxtRowsNum = new JFormattedTextField();
        panel3.add(fTxtRowsNum, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(60, 400), new Dimension(60, 400), new Dimension(60, 400), 0, false));
        lstCells = new JList();
        scrollPane2.setViewportView(lstCells);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 4, new Insets(0, 10, 10, 10), -1, -1));
        panel4.setBackground(new Color(-3276901));
        pnlRacks.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        btnCreate = new JButton();
        this.$$$loadButtonText$$$(btnCreate, ResourceBundle.getBundle("strings").getString("btn_Create"));
        panel4.add(btnCreate, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnExit = new JButton();
        this.$$$loadButtonText$$$(btnExit, ResourceBundle.getBundle("strings").getString("btn_Exit"));
        panel4.add(btnExit, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnDelete = new JButton();
        this.$$$loadButtonText$$$(btnDelete, ResourceBundle.getBundle("strings").getString("btn_Delete"));
        panel4.add(btnDelete, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnUpdate = new JButton();
        this.$$$loadButtonText$$$(btnUpdate, ResourceBundle.getBundle("strings").getString("btn_Update"));
        panel4.add(btnUpdate, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        return pnlRacks;
    }

}
