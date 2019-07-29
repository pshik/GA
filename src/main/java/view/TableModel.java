package view;

import javax.swing.table.AbstractTableModel;

public class TableModel extends AbstractTableModel {
    private int row;
    private int column;
    private Object[][] data;
    private static String[] COLUMN_NAMES = null;

    public TableModel(int row, int col,String[] headers) {
        this.row = row;
        this.column = col;
        data = new String[row][col];
        COLUMN_NAMES = headers;
    }
    @Override
    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data[rowIndex][columnIndex] = (String) aValue;
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return column;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        return data[rowIndex][columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        //  if (rowIndex < 1)
        return true;
    }

}