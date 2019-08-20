package view;

import controller.ClientGuiController;
import exceptions.CloseWindow;
import model.*;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import server.MessageType;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static javax.swing.JOptionPane.YES_NO_OPTION;

public class ClientGUI extends JFrame{
    private JTable mainTable;
    private JComboBox cmbRackName;
    private JButton btnLoad;
    private JButton btnPickUp;
    private JComboBox cmbReference;
    private JTextArea txtCellInfo;
    private JPanel pnlMain;
    private JLabel lblTableName;
    private JScrollPane scrTable;
    private JLabel lblSelectedCell;
    private JRadioButton rbSelectDate;
    private JPanel pnlLogisticDriver;
    private JRadioButton rbShowRef;
    private JLabel lblCurrentUser;
    private JRadioButton rbAvailableCells;
    private JButton btnForcePickUp;
    private JPanel pnlStoreKeeper;
    private JPanel pnlManager;
    private JComboBox cmbManagerFunctions;
    private JButton btnRunManagerCommand;
    private JScrollPane scrDataPane;
    private ClientGuiController controller;
    private JComboBox cmbLogin = new JComboBox();
    private JPasswordField txtPassword = new JPasswordField();
    private String activeUser;

    private int position;
    private static final int ROW_HEIGHT = 90;

    public String getActiveUser() {
        return activeUser;
    }

    public ClientGUI(ClientGuiController controller) {
        this.controller = controller;
        initView();


        btnLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lblSelectedCell.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(pnlMain,"Ячейка не выбрана, выберите ячейку.");
                } else {
                    if (controller.getCellStatus(lblTableName.getText(),lblSelectedCell.getText())) {

                    } else {
                        String manualDate = null;
                        if (rbSelectDate.isSelected()) {

                            Properties p = new Properties();
                            p.put("text.today", "Today");
                            p.put("text.month", "Month");
                            p.put("text.year", "Year");
                            UtilDateModel modelDateUtil = new UtilDateModel();
                            JDatePanelImpl datePanel = new JDatePanelImpl(modelDateUtil, p);
                            JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
                            JOptionPane.showConfirmDialog(null, datePicker, "Выбор даты", JOptionPane.PLAIN_MESSAGE);

                            Calendar cal = Calendar.getInstance();
                            cal.set(datePicker.getModel().getYear(), datePicker.getModel().getMonth(), datePicker.getModel().getDay());
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            manualDate = format.format(cal.getTime());
                            rbSelectDate.setSelected(false);
                        }
                        String message = lblSelectedCell.getText() + controller.getMESSAGE_DELIMITER()
                                + cmbReference.getSelectedItem().toString() + controller.getMESSAGE_DELIMITER()
                                + lblTableName.getText() + controller.getMESSAGE_DELIMITER()
                                + manualDate;
                        controller.sendMessage(MessageType.LOAD_PALLET,message);
                        if (rbAvailableCells.isSelected()) rbAvailableCells.doClick();
                    }
                }
            }
        });
        btnPickUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMaterialForPickUP(cmbReference.getSelectedItem().toString(), false);
            }
        });

        rbAvailableCells.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshRack();
                if (rbShowRef.isSelected()){
                    rbShowRef.doClick();
                }
                if (rbAvailableCells.isSelected()){
                    cmbReference.setEnabled(false);
                    btnPickUp.setEnabled(false);
                    controller.setBusy(true);
                    showAvailableCells(cmbReference.getSelectedItem().toString(),lblTableName.getText(),0);
                } else {
                    cmbReference.setEnabled(true);
                    btnPickUp.setEnabled(true);
                    controller.setBusy(false);
                    showAvailableCells(cmbReference.getSelectedItem().toString(),lblTableName.getText(),1);
                    refreshRack();
                }
            }
        });
        rbShowRef.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshRack();
                if (rbAvailableCells.isSelected()){
                    rbAvailableCells.doClick();
                }
                if (rbShowRef.isSelected()){
                    cmbReference.setEnabled(false);
                    btnLoad.setEnabled(false);
                    btnPickUp.setEnabled(false);
                    controller.setBusy(true);
                    showAllRefOnRack(cmbReference.getSelectedItem().toString(),lblTableName.getText(),0);
                } else {
                    cmbReference.setEnabled(true);
                    btnLoad.setEnabled(true);
                    btnPickUp.setEnabled(true);
                    controller.setBusy(false);
                    showAllRefOnRack(cmbReference.getSelectedItem().toString(),lblTableName.getText(),1);
                    refreshRack();
                }
            }
        });
        btnForcePickUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (rbAvailableCells.isSelected()){
                    rbAvailableCells.doClick();
                }
                if (rbShowRef.isSelected()){
                    rbShowRef.doClick();
                }
                if (lblSelectedCell.getText().isEmpty()){
                    JOptionPane.showMessageDialog(pnlMain,"Ячейка не выбрана, выберите ячейку.");
                }
                else {
                    String text = txtCellInfo.getText();
                    System.out.println(text);
                    if (txtCellInfo.equals("")){
                        JOptionPane.showMessageDialog(pnlMain,"Ячейка пустая, выберите другую ячейку.");
                    } else {
                        showMaterialForPickUP(text.split("\\n")[0], true);
                    }
                }
            }
        });
        cmbRackName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rbShowRef.isSelected()){
                    rbShowRef.doClick();
                }
                if (rbAvailableCells.isSelected()){
                    rbAvailableCells.doClick();
                }
                if (rbSelectDate.isSelected()){
                    rbSelectDate.doClick();
                }
                if (cmbRackName.getItemCount() != 0) {
                    printTable(cmbRackName.getSelectedItem().toString());
                }
            }
        });


        pnlMain.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                txtCellInfo.setText("");
                lblSelectedCell.setText("");
            }
        });
    }

    private void showAvailableCells(String reference, String rackName, int stage) {
        int size = 0;
        Set<SAPReference> references = controller.getModel().getReferences();
        for (SAPReference r: references){
            if (r.getReference().equals(reference)){
                size = r.getSize();
                break;
            }
        }
        DataBuilder data = new DataBuilder();
        for (Rack rack : controller.getModel().getRacks()) {
            if (rack.getName().equals(rackName)) {
                Cell[][] cells = rack.getCells();
                for (int i = 0; i < rack.getRow(); i++) {
                    for (int j = 0; j < rack.getCol(); j++) {
                        int col = cells[i][j].getCol();
                        int row = cells[i][j].getRow();
                        data.clear();
                        if (cells[i][j].getPallets() == null && !cells[i][j].isBlocked()){
                            data.highlightFreeCell(size);
                            mainTable.setValueAt(data.toString(), row, col);
                        }else {
                            String valueAt = (String) mainTable.getValueAt(row, col);
                            data.fillValues(valueAt);
                            switch (stage) {
                                case 0:
                                    data.checkAvailablePositions(size);
                                    mainTable.setValueAt(data.toString(), row, col);
                                    break;
                                case 1:
                                    data.removeHighlighting();
                                    mainTable.setValueAt(data.toString(), row, col);
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void showAllRefOnRack(String reference, String rackName, int stage) {
        for (Rack rack : controller.getModel().getRacks()) {
            if (rack.getName().equals(rackName)){
                Cell[][] cells = rack.getCells();
                for (int i = 0; i < rack.getRow(); i++){
                    for (int j = 0; j < rack.getCol(); j++){
                        if (cells[i][j].isContainReference(reference)){
                            for (Pallet pallet : cells[i][j].getPallets()) {
                                if (pallet.getMaterial().equals(reference)) {
                                    DataBuilder data = new DataBuilder();

                                    int col = cells[i][j].getCol();
                                    //int row = mainTable.getRowCount() - cells[i][j].getRow()-1;
                                    int row = cells[i][j].getRow();
                                    String valueAt = (String) mainTable.getValueAt(row, col);
                                    data.fillValues(valueAt);
                                    switch (stage) {
                                        case 0:
                                            data.highlightValue(pallet.getPosition(), "showAll");
                                            mainTable.setValueAt(data.toString(), row, col);
                                            break;
                                        case 1:
                                            data.removeHighlighting(pallet.getPosition());
                                            mainTable.setValueAt(data.toString(), row, col);
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void showMaterialForPickUP(String material, boolean isForced) {
        if (isForced){
            controller.sendMessage(MessageType.FORCED_PICKUP, lblSelectedCell.getText()
                    + controller.getMESSAGE_DELIMITER() + material
                    + controller.getMESSAGE_DELIMITER() + lblTableName.getText());
        } else {
            TreeMap<Pallet, String> map = new TreeMap<>();
            LocalDateTime currentDate = LocalDateTime.now();
            currentDate = currentDate.minusDays(controller.BLOCKED_DAYS);
            for (Rack rack: controller.getModel().getRacks()){
                Cell[][] cells = rack.getCells();
                for (int i = 0; i < rack.getRow(); i++) {
                    for (int j = 0; j < rack.getCol(); j++) {
                        if (cells[i][j].getPallets() != null && cells[i][j].isContainReference(material)) {
                            for (Pallet p : cells[i][j].getPallets()) {
                                if (p.getMaterial().equals(material)) {
                                    LocalDateTime loadingDate = p.getLoadingDate();
                                    if (loadingDate.isBefore(currentDate)) {
                                        map.put(p, rack.getName() + "," + cells[i][j].getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!map.isEmpty()) {
                Pallet pallet = map.firstKey();
                String s = map.get(pallet);
                String rackString = s.split(",")[0];
                String cellString = s.split(",")[1];
                Rack rack = null;
                for (Rack r : controller.getModel().getRacks()){
                    if (r.getName().equals(rackString)){
                        rack = r;
                    }
                }
                Cell cell = rack.getCellByName(cellString);
                if (rackString.equals(lblTableName.getText())) {
                    int col = cell.getCol();
                    int row = mainTable.getRowCount() - cell.getRow();
                    String valueAt = (String) mainTable.getValueAt(row, col);
                    DataBuilder data = new DataBuilder();
                    data.fillValues(valueAt);
                    data.highlightValue(pallet.getPosition(), "pickUp");
                    mainTable.setValueAt(data.toString(), row, col);

                    Object[] options = {"Да, снять паллет",
                            "Нет, отменить"};
                    int n = JOptionPane.showOptionDialog(pnlMain,
                            "Снять паллет с материалом " + pallet.getMaterial() + " из ячейки " + cellString + " или отменить действие?",
                            "Снятие паллета",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);
                    switch (n) {
                        case 0:
                            controller.sendMessage(MessageType.PICKUP_PALLET, cellString + "[" + pallet.getPosition() + "]" + "-_-" + pallet.getMaterial() + "-_-" + rackString);
                            break;
                        case 1:
                            data.removeHighlighting(pallet.getPosition());
                            mainTable.setValueAt(data.toString(), row, col);
                            break;
                    }
                } else {
                    Object[] options = {"Да, переключиться на стеллаж " + rackString,
                            "Нет, остаться на текущем"};
                    int n = JOptionPane.showOptionDialog(pnlMain,
                            "Паллет с материалом " + pallet.getMaterial() + " с более ранней датой найден на стелаже " + rackString + ". Переключиться на стеллаж " + rackString + " или остаться?",
                            "Снятие паллета",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);
                    switch (n) {
                        case 0:
                            printTable(rackString);
                            cmbRackName.setSelectedItem(rackString);
                            showMaterialForPickUP(material, true);
                            break;
                        case 1:
                            break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(pnlMain, "На стеллажах не найдено паллет с материалом " + material + ".\n" +
                        "Или материал блокирован в соответствии с периодом блокировки " + controller.BLOCKED_DAYS + " суток.");
            }
        }
    }


    private void initView() {
        setTitle("GA Warehouse Management System");
        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(400,800));
        TreeMap<Integer, String> listOfManagersCommands = controller.getListOfManagersCommands();
        Collection<String> values = listOfManagersCommands.values();

        for(String s: values) {
            cmbManagerFunctions.addItem(s);
        }
        cmbManagerFunctions.setMaximumRowCount(4);

        // setBounds(20,0,1600,1024);
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                controller.sendMessage(MessageType.GOODBYE,null);
                controller.closeConnection();
              /* int confirm = JOptionPane.showOptionDialog(
                        null, "Are You Sure to Close Application?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {*/
                System.exit(0);
                // }
            }
        };
        addWindowListener(exitListener);
        setVisible(true);
    }

    public void serverStatus(){
        UIManager.put("OptionPane.yesButtonText"   , "Подключиться"    );
        UIManager.put("OptionPane.noButtonText"    , "Выход"   );
        int input =  JOptionPane.showConfirmDialog(this,"Сервер не доступен!","Подключение",YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        switch (input){
            case 1:
                controller.exit();
                break;
            default:
                break;

        }
    }

    public boolean loginView() throws CloseWindow {
        UIManager.put("OptionPane.yesButtonText"   , "Да"    );
        UIManager.put("OptionPane.noButtonText"    , "Нет"   );
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
        Set<User> users = controller.getModel().getUsers();
        int itemCount = cmbLogin.getItemCount();
        String[] logins = new String[itemCount];
        for(int i = 0 ; i < itemCount; i++){
            logins[i] = (String) cmbLogin.getItemAt(i);
        }
        Object result = JOptionPane.showInputDialog(
                this,
                "Выберите пользователя :",
                "Выбор пользователя",
                JOptionPane.QUESTION_MESSAGE,
                null, logins, logins[0]);
        if (result == null) {
            close();
           // throw new CloseWindow();
        }
        String name=null;
        for (User o: users){
            if (o.getLogin().equals(result)){
                name = o.getFirstName() + " " + o.getSecondName();
            }
        }

        String password = JOptionPane.showInputDialog(
                this,
                name + " введите ваш пароль :",
                "");
        if (validation(password,result.toString())){
            JOptionPane.showMessageDialog(this,"Добро пожаловать!");
            activeUser = result.toString();
            return true;
        } else {
            JOptionPane.showMessageDialog(this,"Не верный пароль, попробуйте еще раз.");
            return false;
        }
    }

    public void refreshUsers() {
        cmbLogin.removeAllItems();
        ClientGuiModel model = controller.getModel();
        Set<User> users = model.getUsers();
        for (User o: users){
            cmbLogin.addItem(o.getLogin());
        }
    }
    private boolean validation(String password,String userName){
        User tmp = null;
        for (User u : controller.getModel().getUsers()){
            if (u.getLogin().equals(userName)){
                tmp = u;
            }
        }
        if (String.valueOf(password).equals(tmp.getPassword())) return true;

        return false;
    }
    private void close(){
        this.dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
    }

    public void mainView() {
        setContentPane(pnlMain);
        cmbRackName.setMaximumRowCount(10);
        cmbReference.setMaximumRowCount(15);
        User tmpUser = null;
        for (User u : controller.getModel().getUsers()){
            if (u.getLogin().equals(controller.getCurrentUser())){
                tmpUser = u;
            }
        }
        lblCurrentUser.setText("Пользователь: " + tmpUser.getFirstName() + " " + tmpUser.getSecondName());

        switch (tmpUser.getRole()){
            case "Administrator":
                setupGUI(1);
                break;
            case "LogisticManager":
                setupGUI(2);
                break;
            case "StoreKeeper":
                setupGUI(3);
                break;
            case "Driver":
                setupGUI(4);
                break;
        }

        setVisible(true);
        refreshRackList();

        btnRunManagerCommand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = cmbManagerFunctions.getSelectedItem().toString();
                Object[] options = {"Да",
                        "Нет"};
                int n = JOptionPane.showOptionDialog(pnlMain,
                        "Вы действительно хотите " + command.toLowerCase() + " или отменить действие?",
                        "Функционал менеджера",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);
                switch (n) {
                    case 0:
                        TreeMap<Integer, String> listOfManagersCommands = controller.getListOfManagersCommands();
                        for (Integer k: listOfManagersCommands.keySet()){
                            if (listOfManagersCommands.get(k).equals(command)){
                                switch (k){
                                    //"Создать стеллаж"
                                    case 1:
                                        break;
                                    //"Удалить стеллаж"
                                    case 2:
                                       break;
                                    //"Управление материалами"
                                    case 3:
                                        ReferenceSettings referenceSettings = new ReferenceSettings();
                                        referenceSettings.initView(controller);
                                        break;
                                    //"Управление стеллажами"
                                    case 4:
                                        RackSettings rackSettings = new RackSettings();
                                        rackSettings.initView(controller);
                                        break;
                                    //"Управление пользователями"
                                    case 5:
                                        UsersSettings usersSettings = new UsersSettings();
                                        usersSettings.initView(controller);
                                         break;
                                     //"Удалить пользователя"
                                    case 6:
                                        break;
                                    case 8:
                                        ImportData importData = new ImportData();
                                        importData.initView(controller);
                                        break;
                                    //Привязка стеллажей к материалам
                                    case 9:
                                        break;
                                }
                            }
                        }
                        break;
                    case 1:
                        break;
                }
            }
        });
        printTable(cmbRackName.getSelectedItem().toString());

    }



    private void setupGUI(int i) {
        btnForcePickUp.setEnabled(false);
        pnlManager.setVisible(false);
        pnlStoreKeeper.setVisible(false);
        pnlLogisticDriver.setVisible(true);
        switch (i){
            case 1:
                btnForcePickUp.setEnabled(true);
            case 2:
                pnlManager.setVisible(true);
            case 3:
                pnlStoreKeeper.setVisible(true);
            case 4:
               pnlLogisticDriver.setVisible(true);
                break;
        }
    }

    private void printTable(String tableName){
        selectReferenceList(tableName);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        Rack rack = null;
        for (Rack r: controller.getModel().getRacks()){
            if (tableName.equals(r.getName())){
                rack = r;
            }
        }

        int currentRackRowCount = rack.getRow();
        int currentRackColumnCount = rack.getCol();
        Cell[][] cells = rack.getCells();

        lblTableName.setText(rack.getName());

        String[] header = new String[currentRackColumnCount];
        for (int j = 0 ; j < currentRackColumnCount; j++){
            header[j] = String.valueOf((char) (65 + j));
        }
        TableModel tblModel = new TableModel(currentRackRowCount,currentRackColumnCount,header);
        TableCellRenderer tableCellRenderer = new MyCellRender(scrTable.getWidth(),currentRackColumnCount);
        TableCellEditor tableCellEditor = new MyCellEditor(new JCheckBox(), scrTable.getWidth(),currentRackColumnCount);
        mainTable.setModel(tblModel);
        JTable rowTable = new RowNumberTable(mainTable);
        scrTable.setRowHeaderView(rowTable);
        for (int i = 0; i < currentRackRowCount ; i++){
            for (int j = 0; j < currentRackColumnCount; j++) {
                String value = "";
                if (!cells[i][j].isBlocked()) {
                    ArrayList<Pallet> pallets = cells[i][j].getPallets();
                    if (pallets != null) {
                        DataBuilder data = new DataBuilder();
                        for (Pallet pallet : pallets) {
                            position = pallet.getPosition();
                            String currentValue = (String) mainTable.getValueAt(i, j);
                            if (currentValue == null || currentValue.equals("")) {
                                data.setValue(position, pallet.getMaterial() + "<br>" + pallet.getLoadingDate().format(dateTimeFormatter));
                            } else {
                                for (int k = 0; k < currentValue.split(",").length; k++) {
                                    if (data.getValue(k).equals(" ")) {
                                        if (!currentValue.split(",")[k].equals(" ")) {
                                            data.setValue(k, currentValue.split(",")[k]);
                                        }
                                    } else {
                                        System.out.println("Error #1, position not empty");
                                    }
                                }
                                if (data.getValue(position).equals(" ")) {
                                    data.setValue(position, pallet.getMaterial() + "<br>" + pallet.getLoadingDate().format(dateTimeFormatter));
                                } else {
                                    System.out.println("Error #2, position not empty");
                                }
                            }

                        }
                        value = data.toString();
                        //  System.out.println(colNames[j] + rowNames[currentRackRowCount-i] + ":"+value +":"+position);
                    } else {
                        //    System.out.println(cellsMap.get(colNames[j] + rowNames[currentRackRowCount - i]) + " | "+ i + " | " + j);
                    }
                } else {
                    value = "=,=,=,=,=,=";
                }
                mainTable.setValueAt(value,i,j);
                mainTable.getColumnModel().getColumn(j).setCellEditor(tableCellEditor);
                mainTable.getColumnModel().getColumn(j).setCellRenderer(tableCellRenderer);
            }
            mainTable.setRowHeight(i,ROW_HEIGHT);
        }

        mainTable.setRowHeight(0,ROW_HEIGHT);
    }

    private void selectReferenceList(String tableName) {
        ArrayList<String> listOfReferences = new ArrayList<>();
        cmbReference.removeAllItems();
        for (SAPReference s : controller.getModel().getReferences()){
            String[] allowedRacks = s.getAllowedRacks();
            for (String rack : allowedRacks) {
                if (rack.equals(tableName)) {
                    listOfReferences.add(s.getReference());
                    break;
                }
            }
        }
        Collections.sort(listOfReferences);
        for (String s: listOfReferences){
            cmbReference.addItem(s);
        }
    }
    public void refreshRack() {
        printTable(cmbRackName.getSelectedItem().toString());
        //for (int i = 0; i < cmbRackName )
        //printTable(cmbRackName.getItemAt(0).toString());
    }

    public void refreshRackList() {
        ArrayList<String> listOfRacks = new ArrayList<>();
        for (Rack r : controller.getModel().getRacks()){
                listOfRacks.add(r.getName());
        }
        cmbRackName.removeAllItems();
        Collections.sort(listOfRacks);
        for (String s: listOfRacks){
            cmbRackName.addItem(s);
        }
    }

    public void refreshView() {
        refreshRackList();
        refreshRack();
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
}
