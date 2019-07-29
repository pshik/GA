package view;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;


public class MyCellRender extends JPanel implements TableCellRenderer {
    private JButton button1= new JButton();
    private JButton button2= new JButton();
    private JButton button3= new JButton();
    private JButton button4= new JButton();
    private JButton button5= new JButton();
    private JButton button6= new JButton();
    private String label;
    private ViewCellHelper viewCellHelper;

    public MyCellRender(int fullWidth,int column) {
        setOpaque(true);
        setLayout(new GridBagLayout());
        viewCellHelper = new ViewCellHelper(this,button1,button2,button3,button4,button5,button6,fullWidth,column);
        viewCellHelper.configureButtons();
        viewCellHelper.addButtons();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setDefaultButtonRender();
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
        return this;
    }

    private void setDefaultButtonRender() {
        viewCellHelper.setDefaultBackgrounds();
        button1.setText("<HTML></HTML>");
        button2.setText("<HTML></HTML>");
        button3.setText("<HTML></HTML>");
        button4.setText("<HTML></HTML>");
        button5.setText("<HTML></HTML>");
        button6.setText("<HTML></HTML>");
    }


}
