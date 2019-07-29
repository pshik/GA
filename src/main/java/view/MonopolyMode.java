package view;

import controller.ServerController;
import dao.Base;
import log.Event;
import log.LogParser;
import log.LoggerFiFo;
import model.*;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class MonopolyMode extends JFrame{
    private static final int ROW_HEIGHT = 105;
    private static final int BLOCKED_DAYS = 4;
    private static int currentRackRowCount;
    private static int currentRackColumnCount;
    private static Base base;
    private static ConcurrentMap users;
    private static ConcurrentMap references;
    private static ConcurrentMap racks;
    private static ConcurrentMap cells;
    private JPanel pnlMain;
    private JTable tbl;
    private JComboBox<Integer> cmbSelectRow;
    private JComboBox<Integer> cmbSelectCol;
    private JComboBox<String> cmbRackNames;
    private JComboBox<String> cmbSAPReference;
    private JButton btnCreate;
    private JButton btnSave;
    private JButton btnLoad;
    private JButton btnUpdate;
    private JButton btnGet;
    private JButton btnPut;
    private JTextField txtNameTable;
    private JLabel lblTableName;
    private JLabel lblSelectedCell;
    private JButton btnShowFree;
    private JButton btnShowPickUp;
    private JButton btnCancelLoading;
    private JScrollPane scrollPane;
    private JTextArea txtaCellInfo;
    private JTextArea txtaHistory;
    private ServerController serverController;
    private String currentUser;

    private final String[] colNames = new String[]{"A","B","C","D","E","F","G","H","I"};
    private final String[] rowNames = new String[]{"0","1","2","3","4","5","6","7","8","9"};
    private static int position;

    public MonopolyMode(ServerController serverController, Base base, String currentUser){

        // Start Frame
        this.base = base;
        this.users = base.getBase("Users");
        this.references = base.getBase("References");
        this.racks = base.getBase("Racks");
        this.cells = base.getBase("Cells");
        this.currentUser = currentUser;
        this.serverController = serverController;
        setTitle("GA Warehouse Management System");
        setBounds(20,0,1600,1024);
        setContentPane(pnlMain);
        //setLayout(new GridBagLayout());
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
              /* int confirm = JOptionPane.showOptionDialog(
                        null, "Are You Sure to Close Application?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {*/
                    base.closeDB();
                    System.exit(0);
               // }
            }
        };
        addWindowListener(exitListener);
        // end Start Frame //

        //Starting values//
        // table//
        tbl.setVisible(false);
        // end table//



        // ComboBoxes //
        cmbSelectCol.setSize(80,20);
        cmbSelectCol.setMaximumRowCount(5);
        cmbSelectRow.setSize(80,20);
        cmbSelectRow.setMaximumRowCount(5);
        for (int i = 1; i < 10; i ++){
            cmbSelectCol.addItem(i);
            cmbSelectRow.addItem(i);
        }
        cmbRackNames.setMaximumRowCount(10);
        cmbRackNames.setEditable(false);
        ArrayList<String> rackNames = getStringsFromMap(racks);
        for (int i = 0; i < rackNames.size(); i ++){
            cmbRackNames.addItem(rackNames.get(i));
        }
        cmbSAPReference.setMaximumRowCount(15);
        cmbSAPReference.setEditable(false);
        ArrayList<String> referenceList = getStringsFromMap(references);
        for (int i = 0; i < referenceList.size(); i ++){
            cmbSAPReference.addItem(referenceList.get(i));
        }
        // end ComboBoxes //

        // Labels //
        lblTableName.setText("");
        lblSelectedCell.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // end Labels //

        // History
       //     LoggerFiFo.getInstance().getRootLogger();
        txtaHistory.setFont(new Font("Arial",Font.PLAIN,10));
        loadHistory();
        // End History

        // Listeners //
        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printTable(txtNameTable.getText());
                tbl.setVisible(true);
            }
        });
        tbl.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tbl.rowAtPoint(e.getPoint());
                int col = tbl.columnAtPoint(e.getPoint());
                lblSelectedCell.setText(colNames[col] + rowNames[tbl.getRowCount() - row - 1]);
                tbl.getModel().isCellEditable(row,col);
            }
        });
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        btnLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printTable(cmbRackNames.getSelectedItem().toString());
                tbl.setVisible(true);
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        btnPut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lblSelectedCell.getText().isEmpty()) JOptionPane.showMessageDialog(pnlMain,"Ячейка не выбрана, выберети ячейку.");
                else {
                    loadPallet(lblSelectedCell.getText(),cmbSAPReference.getSelectedItem().toString(),lblTableName.getText());
                }
            }
        });
        btnGet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        btnShowFree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAvailableCells();
            }
        });
        btnShowPickUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMaterialForPickUP(cmbSAPReference.getSelectedItem().toString());
            }
        });
        //end Listeners //

        pnlMain.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                lblSelectedCell.setText("");
                if (lblTableName != null)
                    printTable(lblTableName.getText());
                //tbl.clearSelection();
            //    tbl.setBackground(Color.RED);
            }
        });

        btnCancelLoading.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAvailableCells();
            }
        });
    }

    private void showMaterialForPickUP(String material) {
        Map<Pallet,String> map = new TreeMap<>();
        LocalDateTime currentDate = LocalDateTime.now();
        currentDate = currentDate.minusDays(BLOCKED_DAYS);
        for(Object cell: cells.values()){
            Cell tmp = (Cell) cell;
            if (tmp.getPallets()!= null && tmp.isContainReference(material)){
                for (Pallet p: tmp.getPallets()){
                    if (p.getMaterial().equals(material)){
                        LocalDateTime loadingDate = p.getLoadingDate();
                        if (loadingDate.isBefore(currentDate)){
                            map.put(p,tmp.getRack() + "," + tmp.getRow() + "," + tmp.getCol());
                        }
                    }
                }
            }
        }
//        for (Pallet p: map.keySet())
//            System.out.println(p+ ":" + map.get(p));
        Pallet pallet = ((TreeMap<Pallet, String>) map).firstKey();
        String s = map.get(pallet);
        String rackString = s.split(",")[0];
        String rowString = s.split(",")[1];
        String colString = s.split(",")[2];
        if (rackString.equals(lblTableName.getText())) {
            int col = 0;
            for (int i = 0; i < colNames.length; i++) {
                if (colNames[i].equals(colString)) {
                    col = i;
                    break;
                }
            }
            int row = tbl.getRowCount() - Integer.parseInt(rowString);
            String valueAt = (String) tbl.getValueAt(row, col);
            DataBuilder data = new DataBuilder();
            data.fillValues(valueAt);
            data.highlightValue(pallet.getPosition(),"pickUp");
            tbl.setValueAt(data.toString(), row, col);

            Object[] options = {"Да, снять паллет",
                    "Нет, отменить"};
            int n = JOptionPane.showOptionDialog(pnlMain,
                    "Снять паллет с материалом " + pallet.getMaterial() + " из ячейки " + colString + rowString + " или отменить действие?",
                    "Снятие паллета",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
            switch (n) {
                case 0:
                    unloadPallet(colString + rowString + "[" + pallet.getPosition() + "]", pallet.getMaterial(), rackString);
                    printTable(rackString);
                    break;
                case 1:
                    data.removeHighlighting(pallet.getPosition());
                    tbl.setValueAt(data.toString(), row, col);
                    break;
            }
        } else {
            Object[] options = {"Да, переключиться на стеллаж " + rackString,
                    "Нет, остаться на текущем"};
            int n = JOptionPane.showOptionDialog(pnlMain,
                    "Паллет с материалом " + pallet.getMaterial() + " с более ранней датой найден на стелаже " + rackString + ". Переключиться на стеллаж " + rackString +" или остаться?",
                    "Снятие паллета",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
            switch (n) {
                case 0:
                    printTable(rackString);
                    showMaterialForPickUP(material);
                    break;
                case 1:
                    break;
            }
        }
    }

    private void showAvailableCells() {
        Rack rack = (Rack) base.getBase("Racks").get(lblTableName.getText());
        SAPReference tmp = (SAPReference) base.getBase("References").get(cmbSAPReference.getSelectedItem().toString());
        if (tbl != null){
            int row = rack.getRow();
            int col = rack.getCol();
            for (int i = 0; i < row ; i++){
                for (int j = 0; j < col; j++) {
                    String value = (String) tbl.getValueAt(i, j);
                    DataBuilder data = new DataBuilder();
                    if (value.equals("")) {
                        switch (tmp.getSize()) {
                            case 1:
                                data.setValue("*",0);
                                data.setValue("*",1);
                                data.setValue("*",2);
                                data.setValue("*",3);
                                data.setValue("*",4);
                                data.setValue("*",5);
                                break;
                            case 2:
                                data.setValue("*",0);
                                data.setValue("*",1);
                                data.setValue("*",2);
                                break;
                            case 3:
                                data.setValue("*",0);
                                break;
                        }
                    } else {
                        data.fillValues(value);
                        switch (tmp.getSize()){
                            case 1:
                                if (data.getValue(0).equals(" ")){
                                    data.setValue("*",0);
                                }
                                if (data.getValue(1).equals(" ")){
                                    data.setValue("*",1);
                                }
                                if (data.getValue(2).equals(" ")){
                                    data.setValue("*",2);
                                }
                                if (data.getValue(3).equals(" ")){
                                    data.setValue("*",3);
                                }
                                if (data.getValue(4).equals(" ")){
                                    data.setValue("*",4);
                                }
                                if (data.getValue(5).equals(" ")){
                                    data.setValue("*",5);
                                }
                                break;
                            case 2:
                                if (data.getValue(0).equals(" ")){
                                    data.setValue("*",0);
                                }
                                if (data.getValue(1).equals(" ")){
                                    data.setValue("*",1);
                                }
                                if (data.getValue(2).equals(" ")){
                                    data.setValue("*",2);
                                }
                                break;
                            case 3:
                                if (data.getValue(0).equals(" ")){
                                    data.setValue("*",0);
                                }
                                break;
                        }
                    }
                    tbl.setValueAt(data.toString(), i,j);
                }
            }
        }
    }

    private void unloadPallet(String cellFulPath, String refName, String lblTableName) {
        ConcurrentMap tmp = base.getBase("Cells");
        int pos = Integer.parseInt(cellFulPath.substring(cellFulPath.indexOf("[")+1,cellFulPath.indexOf("]")));
        String cellName = cellFulPath.substring(0,cellFulPath.indexOf("["));
        boolean isExist = true;
        try{
            Cell o;
            if( (o = (Cell) cells.get(lblTableName + ":" + cellName)) == null){
                JOptionPane.showMessageDialog(pnlMain,"В ячейке пусто.");
            }else {
                for (Pallet pallet: o.getPallets()){
                    if (pallet.getPosition() == pos){
                        if(pallet.getMaterial().equals(refName)){
                            o.pickUpPallet(pos,refName);
                            isExist = false;
                        }
                    }
                }
            }
            if (isExist){
                JOptionPane.showMessageDialog(pnlMain,"Ячейка не содержит нужный вам материал.");
            } else {
                cells.replace(lblTableName + ":" + cellName,o);
            }
            printTable(lblTableName);
            LoggerFiFo.getInstance().getRootLogger().info(String.format("User %s: UnLoad pallet. %s %s %s",currentUser,lblTableName,cellName,refName));
            LogParser.getInstance().updateLog();
            loadHistory();
        } catch (Exception e){
            cells = tmp;
        }
    }

    private void loadPallet(String cellFulPath, String refName, String lblTableName) {
        //check available space
        Rack tmpRack = (Rack) racks.get(lblTableName);
        SAPReference material = (SAPReference) references.get(refName);
        Pallet pallet = new Pallet(material.getReference(),material.getSize(),LocalDateTime.now());
        int pos = Integer.parseInt(cellFulPath.substring(cellFulPath.indexOf("[")+1,cellFulPath.indexOf("]")));
        int localSize = 0;
        String cellName = cellFulPath.substring(0,cellFulPath.indexOf("["));
        pallet.setPosition(pos);
        boolean isBusy = false;
        ArrayList<Integer> lockedPositions = new ArrayList<>();
        switch (pos){
            case 0:
                lockedPositions.add(1);
                lockedPositions.add(2);
                lockedPositions.add(3);
                lockedPositions.add(4);
                lockedPositions.add(5);
                localSize = 3;
                break;
            case 1:
                lockedPositions.add(0);
                lockedPositions.add(3);
                lockedPositions.add(4);
                localSize = 2;
                break;
            case 2:
                lockedPositions.add(0);
                lockedPositions.add(4);
                lockedPositions.add(5);
                localSize = 2;
                break;
            case 3:
                lockedPositions.add(0);
                lockedPositions.add(1);
                localSize = 1;
                break;
            case 4:
                lockedPositions.add(0);
                lockedPositions.add(1);
                lockedPositions.add(2);
                localSize = 1;
                break;
            case 5:
                lockedPositions.add(0);
                lockedPositions.add(2);
                localSize = 1;
                break;
        }
        Cell o;
        if( (o = (Cell) cells.get(lblTableName + ":" + cellName)) == null){
            o = new Cell(lblTableName,String.valueOf(tmpRack.getCol()+1),String.valueOf(tmpRack.getRow()+1), pallet);
        }else {
            if (o.getPallets() != null) {
                for (Pallet p : o.getPallets()) {
                    if (p.getPosition() == pos) {
                        JOptionPane.showMessageDialog(pnlMain, "Ячейка занята");
                        isBusy = true;
                    } else {
                        if (lockedPositions.contains(p.getPosition())) {
                            JOptionPane.showMessageDialog(pnlMain, "Не корректное размещение палета");
                            isBusy = true;
                        }
                    }
                }
                o.addPallet(pallet);
            } else {
                if (material.getSize() <= localSize) {
                    o.addPallet(pallet);
                }
                else {
                    JOptionPane.showMessageDialog(pnlMain, "Не корректное размещение палета");
                    isBusy = true;
                }
            }
        }

        if (!isBusy) {
            cells.replace(lblTableName + ":" + cellName, o);
            clearAvailableCells();
            printTable(lblTableName);
            LoggerFiFo.getInstance().getRootLogger().info(String.format("User %s: Load pallet. %s %s %s",currentUser,tmpRack.getName(),cellName,material.getReference()));
            LogParser.getInstance().updateLog();
            loadHistory();
            /// "User: currentUser: Successfully loading pallet. Rack1 cellA6 material
        }
    }

    private void clearAvailableCells() {
        if (tbl != null){
            int row = tbl.getRowCount();
            int col = tbl.getColumnCount();
            for (int i = 0; i < row ; i++){
                for (int j = 0; j < col; j++) {
                    String value = (String) tbl.getValueAt(i, j);
                    DataBuilder data = new DataBuilder();
                    if (!value.equals("")) {
                        data.fillValues(value);
                        for (int k = 0; k < 6; k++){
                            if (data.getValue(k).equals("*")){
                                data.setValue(" ",k);
                            }
                        }
                    }
                    tbl.setValueAt(data.toString(), i,j);
                }
            }
        }
    }

    private void printTable(String tableName){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm",Locale.ENGLISH);
        Rack rack = (Rack) racks.get(tableName);
        currentRackRowCount = rack.getRow();
        currentRackColumnCount = rack.getCol();
        ArrayList<String> selectedCells = new ArrayList<>();
        HashMap<String, Cell> cellsMap = new HashMap<>();
        for (Object o : cells.keySet()){
            String tmp = (String) o;
            if(tmp.startsWith(tableName)) {
                selectedCells.add(tmp);
                cellsMap.put(tmp.split(":")[1],(Cell) cells.get(tmp));
            }
        }

        cmbRackNames.setSelectedItem(tableName);
        lblTableName.setText(cmbRackNames.getSelectedItem().toString());
        String[] header = new String[currentRackColumnCount];
        for (int i = 0 ; i < currentRackColumnCount; i++){
            header[i] = colNames[i];
        }
        TableModel tblModel = new model.TableModel(currentRackRowCount,currentRackColumnCount,header);
        TableCellRenderer tableCellRenderer = new MyCellRender(scrollPane.getWidth(),currentRackColumnCount);
        TableCellEditor tableCellEditor = new MyCellEditor(new JCheckBox(),scrollPane.getWidth(),currentRackColumnCount);
        tbl.setModel(tblModel);
        JTable rowTable = new RowNumberTable(tbl);
        scrollPane.setRowHeaderView(rowTable);
        for (int i = 0; i < currentRackRowCount ; i++){
            for (int j = 0; j < currentRackColumnCount; j++) {
                String value = "";
                if (cellsMap.get(colNames[j] + rowNames[currentRackRowCount-i]).getPallets() != null){
                    ArrayList<Pallet> pallets = cellsMap.get(colNames[j] + rowNames[currentRackRowCount-i]).getPallets();
                    DataBuilder data = new DataBuilder();
                    for (Pallet pallet : pallets){
                        position = pallet.getPosition();
                        String currentValue = (String) tbl.getValueAt(i,j);
                        if (currentValue == null || currentValue.equals("") ) {
                            data.setValue(position,pallet.getMaterial() + "<br>" +  pallet.getLoadingDate().format(dateTimeFormatter));
                        } else{
                            for (int k = 0 ; k < currentValue.split(",").length;k++){
                                if (data.getValue(k).equals(" ")){
                                    if (!currentValue.split(",")[k].equals(" ")){
                                        data.setValue(k,currentValue.split(",")[k]);
                                    }
                                }else {
                                    System.out.println("Error #1, position not empty");
                                }
                            }
                            if (data.getValue(position).equals(" ")){
                                data.setValue(position,pallet.getMaterial() + "<br>" +  pallet.getLoadingDate().format(dateTimeFormatter));
                            } else {
                                System.out.println("Error #2, position not empty");
                            }
                        }

                    }
                    value = data.toString();
                 //   System.out.println(colNames[j] + rowNames[currentRackRowCount-i] + ":"+value +":"+position);
                }
                tbl.setValueAt(value,i,j);
                tbl.getColumnModel().getColumn(j).setCellRenderer(tableCellRenderer);
                tbl.getColumnModel().getColumn(j).setCellEditor( tableCellEditor);
            }
            tbl.setRowHeight(i,ROW_HEIGHT);
        }

        tbl.setRowHeight(0,ROW_HEIGHT);
    }

    private ArrayList<String> getStringsFromMap(ConcurrentMap map){
        ArrayList<String> result = new ArrayList<>();
        for (Object o : map.keySet()){
            result.add((String) o);
        }
        return result;
    }

    public void loadHistory() {
        txtaHistory.setText("");
        ArrayList<Event> events = LogParser.getInstance().getEvents();
        for (Event e: events){
            if (e.getLevel().equals("INFO")){
                txtaHistory.append(e.getMessage() + "\n");
            }
        }

    }

}
