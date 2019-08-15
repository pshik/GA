package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MyCellEditor extends DefaultCellEditor {
    private JButton button1= new JButton();
    private JButton button2= new JButton();
    private JButton button3= new JButton();
    private JButton button4= new JButton();
    private JButton button5= new JButton();
    private JButton button6= new JButton();
    private JPanel panel = new JPanel();
    private String label;
    private boolean isPushed;
    private ViewCellHelper viewCellHelper;

    public MyCellEditor(JCheckBox checkBox,int fullWidth,int column) {
        super(checkBox);
        panel.setOpaque(true);
        panel.setLayout(new GridBagLayout());
        viewCellHelper = new ViewCellHelper(panel,button1,button2,button3,button4,button5,button6,fullWidth,column);
        viewCellHelper.configureButtons();
        viewCellHelper.addButtons();
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAddress(0);
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAddress(1);
            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAddress(2);
            }
        });
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAddress(3);
            }
        });
        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAddress(4);
            }
        });
        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAddress(5);
            }
        });
    }


    private void setAddress(int i) {
        JTable mainTable = (JTable) panel.getParent();
        int row = mainTable.getEditingRow();
        int col = mainTable.getEditingColumn();
        Container parent = mainTable.getParent();
        JScrollPane pane = (JScrollPane) parent.getParent();
        JPanel panel = (JPanel) pane.getParent();
        Component[] components = panel.getComponents();
        for (Component c: components){
            if(c.getName() != null && c.getName().equals("lblSelectedCell")){
                JLabel label = (JLabel) c;
                label.setText(mainTable.getColumnName(col) + (mainTable.getRowCount() - row) + "[" + i + "]");
            }
            if(c.getName() != null && c.getName().equals("scrDataPane")){
                JScrollPane tmpPanel = (JScrollPane) c;
                JTextArea txtArea = new JTextArea();
                for (Component comp: tmpPanel.getComponents()) {
                    if (comp instanceof JViewport) {
                        txtArea = (JTextArea) ((JViewport) comp).getComponent(0);
                    }
                }
                String string = mainTable.getValueAt(row, col).toString();
                if (!string.equals("")) {
                    String value = string.split(",")[i];
                    if (value != null && !value.equals("") && !value.equals("#") && !value.equals("*") && !value.equals("^") && !value.startsWith("@") && !value.startsWith(" ")) {
                        String[] data = value.split("<br>");
                        txtArea.setText(data[0] + "\n" + data[1]);
                    } else {
                        txtArea.setText("");
                    }
                } else {
                    txtArea.setText("");
                }
            }
        }
        if (label != null && !label.equals("")) {
            if (!label.split(",")[i].replace(" ", "").equals("")) {
            //    JOptionPane.showMessageDialog(button1, label.split(",")[i]);
            }
        }
    }

    public Object getCellEditorValue() {
        if (isPushed) {
            JOptionPane.showMessageDialog(panel, label + ": Ouch!");
        }
            isPushed = false;
            return label;
    }
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        viewCellHelper.setDefaultBackgrounds();
        label = (value == null) ? "" : value.toString();
        if (!label.equals("")) {
            String[] labels = label.split(",");
            viewCellHelper.renderButton(labels[0],1);
            viewCellHelper.renderButton(labels[1],2);
            viewCellHelper.renderButton(labels[2],3);
            viewCellHelper.renderButton(labels[3],4);
            viewCellHelper.renderButton(labels[4],5);
            viewCellHelper.renderButton(labels[5],6);
        } else {
            viewCellHelper.setDefaultBackgrounds();
            viewCellHelper.setDefaultText();
        }
        isPushed = true;
        return panel;
    }

}
