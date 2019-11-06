package view;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.ClientGuiController;
import model.SAPReference;
import server.MessageType;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ImportData extends JFrame {
    private JButton btnBrowse;
    private JTable tblData;
    private JScrollPane scrTable;
    private JPanel pnlTable;
    private JPanel pnlPath;
    private JPanel pnlMain;
    private JButton btnImport;
    private JButton btnExit;
    private JTextArea txtaStatus;
    final JFileChooser fc = new JFileChooser();

    public ImportData() {
        setTitle("Настройка стеллажей");
        setVisible(false);
        //  setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setLocation(200, 200);
        // setAlwaysOnTop(true);
        setContentPane(pnlMain);
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".csv");
                }
            }

            @Override
            public String getDescription() {
                return "CSV Documents (*.csv)";
            }
        });

    }

    public void initView(ClientGuiController controller) {

        btnBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(pnlMain);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    ArrayList<String[]> records = new ArrayList<>();
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (!line.isEmpty()) {
                                String[] values = line.split(",");
                                records.add(values);
                            }
                        }
                        Object[] headers = new Object[]{"#", "SAP Reference", "Size", "Description", "Allowed Racks"};
                        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                        tblData.setModel(new DefaultTableModel(headers, 0));
                        DefaultTableModel model = (DefaultTableModel) tblData.getModel();
                        tblData.getColumn("#").setPreferredWidth(20);
                        tblData.getColumn("SAP Reference").setPreferredWidth(120);
                        tblData.getColumn("Size").setPreferredWidth(30);
                        tblData.getColumn("Description").setPreferredWidth(200);
                        tblData.getColumn("Allowed Racks").setPreferredWidth(120);
                        tblData.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
                        tblData.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
                        for (int i = 0; i < records.size(); i++) {
                            if (records.get(i).length == 4) {
                                model.addRow(new Object[]{i + 1, records.get(i)[0], records.get(i)[1], records.get(i)[2], records.get(i)[3]});
                                // System.out.println(records.get(i)[0] + " : " +records.get(i)[1]+ " : " +records.get(i)[2]+ " : " + records.get(i)[3]);
                            } else if (records.get(i).length > 4) {
                                model.addRow(new Object[]{i + 1, records.get(i)[0], records.get(i)[1], records.get(i)[2], records.get(i)[3]});
                                txtaStatus.append("Error in line:" + i + "\n");
                            } else {
                                switch (records.get(i).length) {
                                    case 1:
                                        model.addRow(new Object[]{i + 1, records.get(i)[0], "", "", ""});
                                        break;
                                    case 2:
                                        model.addRow(new Object[]{i + 1, records.get(i)[0], records.get(i)[1], "", ""});
                                        break;
                                    case 3:
                                        model.addRow(new Object[]{i + 1, records.get(i)[0], records.get(i)[1], records.get(i)[2], ""});
                                        break;
                                }
                                txtaStatus.append("Ошибка в процессе обработки файла. Строка : " + (i + 1) + "\n");
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        btnImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isDataCorrect = true;
                DefaultTableModel model = (DefaultTableModel) tblData.getModel();
                ArrayList<Object> list = new ArrayList<>();
                for (int i = 0; i < model.getRowCount(); i++) {
                    try {
                        String name = (String) model.getValueAt(i, 1);
                        int size = Integer.parseInt((String) model.getValueAt(i, 2));
                        String description = (String) model.getValueAt(i, 3);
                        String listOfRacks = (String) model.getValueAt(i, 4);
                        if (!name.trim().isEmpty() && !description.trim().isEmpty()) {
                            SAPReference newRef = new SAPReference(name, description, size, listOfRacks.split(";"));
                            list.add(newRef);
                        } else {
                            isDataCorrect = false;
                            JOptionPane.showMessageDialog(pnlMain, "Проверьте корректность данных в строке " + (i + 1));
                        }
                    } catch (NumberFormatException eSize) {
                        JOptionPane.showMessageDialog(pnlMain, "Проверьте корректность данных в строке " + (i + 1));
                        isDataCorrect = false;
                    }
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectMapper mapper = new ObjectMapper();
                try {
                    mapper.writeValue(out, list);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    isDataCorrect = false;
                }
                if (isDataCorrect) {
                    controller.sendMessage(MessageType.IMPORT_EXPORT, 0 + out.toString());
                }
            }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        ImportData importData = new ImportData();
        importData.initView(null);
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
        pnlMain.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        pnlTable = new JPanel();
        pnlTable.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(5, 5, 5, 5), -1, -1));
        pnlMain.add(pnlTable, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrTable = new JScrollPane();
        scrTable.setToolTipText("");
        pnlTable.add(scrTable, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrTable.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        tblData = new JTable();
        scrTable.setViewportView(tblData);
        final JScrollPane scrollPane1 = new JScrollPane();
        pnlTable.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 40), null, null, 0, false));
        txtaStatus = new JTextArea();
        scrollPane1.setViewportView(txtaStatus);
        pnlPath = new JPanel();
        pnlPath.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(5, 20, 0, 5), -1, -1));
        pnlMain.add(pnlPath, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnBrowse = new JButton();
        this.$$$loadButtonText$$$(btnBrowse, ResourceBundle.getBundle("strings").getString("btn_Browse"));
        pnlPath.add(btnBrowse, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnImport = new JButton();
        this.$$$loadButtonText$$$(btnImport, ResourceBundle.getBundle("strings").getString("btn_Import"));
        pnlPath.add(btnImport, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnExit = new JButton();
        this.$$$loadButtonText$$$(btnExit, ResourceBundle.getBundle("strings").getString("btn_Exit"));
        pnlPath.add(btnExit, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
