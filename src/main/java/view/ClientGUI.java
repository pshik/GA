package view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
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
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

import static javax.swing.JOptionPane.YES_NO_OPTION;

public class ClientGUI extends JFrame {
    private JTable mainTable;
    private JComboBox<String> cmbRackName;
    private JButton btnLoad;
    private JButton btnPickUp;
    private JComboBox<String> cmbReference;
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
    private JComboBox<String> cmbManagerFunctions;
    private JButton btnRunManagerCommand;
    private JScrollPane scrDataPane;
    private JPanel pnlRight;
    private JPanel pnlTopInfo;
    private JButton historyButton;
    private ClientGuiController controller;
    private JComboBox<String> cmbLogin = new JComboBox<String>();
    private JPasswordField txtPassword = new JPasswordField();
    private String activeUser;
    private static TreeMap<Integer, String> listOfManagersCommands = new TreeMap<>();

    static {
        //  listOfManagersCommands.put(1,"Создать стеллаж");
        // listOfManagersCommands.put(2,"Удалить стеллаж");
        listOfManagersCommands.put(3, "Управление материалами");
        listOfManagersCommands.put(4, "Управление стеллажами");
        listOfManagersCommands.put(5, "Управление пользователями");
        listOfManagersCommands.put(6, "Загрузить из .CSV палеты");
        //  listOfManagersCommands.put(7,"Отчеты");
        listOfManagersCommands.put(8, "Загрузить материалы из .CSV");
        //  listOfManagersCommands.put(9,"Привязать материалы к стеллажу");
    }

    private int position;
    private static final int ROW_HEIGHT = 96;

    public String getActiveUser() {
        return activeUser;
    }

    //    public ClientGUI() {
//        createUIComponents();
//    }
    public ClientGUI(ClientGuiController controller) {
        this.controller = controller;
        $$$setupUI$$$();
        initView();
        Collection<String> values = listOfManagersCommands.values();

        try {
            for (String s : values) {
                cmbManagerFunctions.addItem(s);
                cmbManagerFunctions.setMaximumRowCount(4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lblSelectedCell.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(pnlMain, "Ячейка не выбрана, выберите ячейку.");
                } else {
                    if (controller.getCellStatus(lblTableName.getText(), lblSelectedCell.getText())) {

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
                        controller.sendMessage(MessageType.LOAD_PALLET, message);
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
                String material = cmbReference.getSelectedItem().toString();
                refreshRack();
                if (rbShowRef.isSelected()) {
                    rbShowRef.doClick();
                }
                if (rbAvailableCells.isSelected()) {
                    cmbReference.setEnabled(false);
                    btnPickUp.setEnabled(false);
                    controller.setBusy(true);
                    showAvailableCells(material, lblTableName.getText(), 0);
                    selectMaterial(material);
                } else {
                    cmbReference.setEnabled(true);
                    btnPickUp.setEnabled(true);
                    controller.setBusy(false);
                    showAvailableCells(material, lblTableName.getText(), 1);
                    refreshRack();
                    selectMaterial(material);
                }
            }
        });
        rbShowRef.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String material = cmbReference.getSelectedItem().toString();
                refreshRack();
                if (rbAvailableCells.isSelected()) {
                    rbAvailableCells.doClick();
                }
                if (rbShowRef.isSelected()) {
                    cmbReference.setEnabled(false);
                    btnLoad.setEnabled(false);
                    btnPickUp.setEnabled(false);
                    controller.setBusy(true);
                    showAllRefOnRack(material, lblTableName.getText(), 0);
                    selectMaterial(material);
                } else {
                    cmbReference.setEnabled(true);
                    btnLoad.setEnabled(true);
                    btnPickUp.setEnabled(true);
                    controller.setBusy(false);
                    showAllRefOnRack(material, lblTableName.getText(), 1);
                    refreshRack();
                    selectMaterial(material);
                }
            }
        });
        btnForcePickUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (rbAvailableCells.isSelected()) {
                    rbAvailableCells.doClick();
                }
                if (rbShowRef.isSelected()) {
                    rbShowRef.doClick();
                }
                if (lblSelectedCell.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(pnlMain, "Ячейка не выбрана, выберите ячейку.");
                } else {
                    String text = txtCellInfo.getText();
                    System.out.println(text);
                    if (txtCellInfo.equals("")) {
                        JOptionPane.showMessageDialog(pnlMain, "Ячейка пустая, выберите другую ячейку.");
                    } else {
                        showMaterialForPickUP(text.split("\\n")[0], true);
                    }
                }
            }
        });
        cmbRackName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rbShowRef.isSelected()) {
                    rbShowRef.doClick();
                }
                if (rbAvailableCells.isSelected()) {
                    rbAvailableCells.doClick();
                }
                if (rbSelectDate.isSelected()) {
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
        //lblSelectedCell.addPropertyChangeListener();
        PropertyChangeListener l = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                boolean isExist = false;
                for (Rack rack : controller.getModel().getRacks()) {
                    if (rack.getName().equals(lblTableName.getText())) {
                        String cellName = lblSelectedCell.getText().split("\\[")[0];
                        int pos = Integer.parseInt(lblSelectedCell.getText().split("\\[")[1].substring(0, 1));
                        Cell cellByName = rack.getCellByName(cellName);
                        if (cellByName.getPallets() != null) {
                            for (Pallet p : cellByName.getPallets()) {
                                if (p.getPosition() == pos) {

                                    //  DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH);
                                    LocalDateTime loadingDate = p.getLoadingDate();
                                    String date =
                                            DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                                                    .withLocale(new Locale("ru", "RU"))
                                                    .format(loadingDate);
                                    //txtCellInfo.setText(p.getMaterial() + "\n" + loadingDate.format(dateTimeFormatter));
                                    txtCellInfo.setText(p.getMaterial() + "\n" + date);
                                    isExist = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (!isExist) {
                    txtCellInfo.setText("");
                }
            }
        };
        lblSelectedCell.addPropertyChangeListener("text", l);
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                History history = new History();
                history.initView(controller);
            }
        });
    }

    private void selectMaterial(String material) {
        int itemCount = cmbReference.getItemCount();
        int index = 0;
        for (int i = 0; i < itemCount; i++) {
            if (cmbReference.getItemAt(i).equals(material)) {
                index = i;
                break;
            }
        }
        cmbReference.setSelectedIndex(index);
    }

    private void showAvailableCells(String reference, String rackName, int stage) {
        int size = 0;
        Set<SAPReference> references = controller.getModel().getReferences();
        for (SAPReference r : references) {
            if (r.getReference().equals(reference)) {
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
                        if (cells[i][j].getPallets() == null && !cells[i][j].isBlocked()) {
                            data.highlightFreeCell(size);
                            mainTable.setValueAt(data.toString(), row, col);
                        } else {
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
            if (rack.getName().equals(rackName)) {
                Cell[][] cells = rack.getCells();
                for (int i = 0; i < rack.getRow(); i++) {
                    for (int j = 0; j < rack.getCol(); j++) {
                        if (cells[i][j].isContainReference(reference)) {
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
        if (isForced) {
            controller.sendMessage(MessageType.FORCED_PICKUP, lblSelectedCell.getText()
                    + controller.getMESSAGE_DELIMITER() + material
                    + controller.getMESSAGE_DELIMITER() + lblTableName.getText());
        } else {
            //   TreeMap<Pallet, String> map = new TreeMap<>();
            HashMap<String, Pallet> map = new HashMap<>();
            LocalDateTime currentDate = LocalDateTime.now();
            currentDate = currentDate.minusDays(controller.BLOCKED_DAYS);
            int count = 0;
            for (Rack rack : controller.getModel().getRacks()) {
                Cell[][] cells = rack.getCells();
                for (int i = 0; i < rack.getRow(); i++) {
                    for (int j = 0; j < rack.getCol(); j++) {
                        if (cells[i][j].getPallets() != null && cells[i][j].isContainReference(material)) {
                            for (Pallet p : cells[i][j].getPallets()) {
                                if (p.getMaterial().equals(material)) {
                                    count++;
                                    LocalDateTime loadingDate = p.getLoadingDate();
                                    if (loadingDate.isBefore(currentDate)) {
                                        //                 map.put(p, rack.getName() + "," + cells[i][j].getName());
                                        map.put(count + "," + rack.getName() + "," + cells[i][j].getName() + "," + "[" + position + "]", p);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!map.isEmpty()) {
                if (count < 2) {
                    JOptionPane.showMessageDialog(pnlMain, "На стеллажах останеться последний паллет с материалом " + material, "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
                    String event = String.format("Пользователь %s, предупрежден о том что остается последний паллет c материалом %s на стеллажах", activeUser, material);
                    controller.sendMessage(MessageType.EVENT, controller.events.get("WARN") + "-_-" + event);
                }
                LocalDateTime first = currentDate;
                Pallet pallet = new Pallet();
                String key = "";
                for (String s : map.keySet()) {
                    Pallet p = map.get(s);
                    if (p.getLoadingDate().isBefore(first)) {
                        pallet = p;
                        key = s;
                        first = p.getLoadingDate();
                    }
                }
                String rackString = key.split(",")[1];
                String cellString = key.split(",")[2];
                Rack rack = null;
                for (Rack r : controller.getModel().getRacks()) {
                    if (r.getName().equals(rackString)) {
                        rack = r;
                        break;
                    }
                }
                Cell cell = rack.getCellByName(cellString);
                if (rackString.equals(lblTableName.getText())) {
                    int col = cell.getCol();
                    int row = cell.getRow();
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
                            showMaterialForPickUP(material, false);
                            break;
                        case 1:
                            break;
                    }
                }
            } else {
                if (count == 0) {
                    JOptionPane.showMessageDialog(pnlMain, "На стеллажах не найдено паллет с материалом " + material);
                } else {
                    JOptionPane.showMessageDialog(pnlMain, "Материал блокирован в соответствии с периодом блокировки " + controller.BLOCKED_DAYS + " суток.");
                }
            }
        }
    }


    private void initView() {
        setTitle("GA Warehouse Management System");
        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(400, 800));


        // setBounds(20,0,1600,1024);
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                controller.sendMessage(MessageType.GOODBYE, null);
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

    public void serverStatus() {
        UIManager.put("OptionPane.yesButtonText", "Подключиться");
        UIManager.put("OptionPane.noButtonText", "Выход");
        UIManager.put("OptionPane.cancelButtonText", "Настройки");
        int input = JOptionPane.showConfirmDialog(this, "Сервер не доступен!", "Подключение", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        switch (input) {
            case 1:
                controller.exit();
                break;
            case 2:
                JPanel panel = new JPanel();
                JLabel labelIP = new JLabel("IP адрес сервера :");
                JTextField ip = new JTextField(16);
                ip.setText(controller.getServerAddress());
                JLabel labelPort = new JLabel("   Port сервера :");
                JTextField port = new JTextField(8);
                port.setText(String.valueOf(controller.getServerPort()));
                panel.add(labelIP);
                panel.add(ip);
                panel.add(labelPort);
                panel.add(port);
                String[] options = new String[]{"OK", "Отмена"};
                int option = JOptionPane.showOptionDialog(null, panel, "Настройка подключения",
                        JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, ip);
                String newIP = "";
                String newPort = "";
                if (option == 0) {
                    newIP = String.valueOf(ip.getText());
                    newPort = String.valueOf(port.getText());
                    controller.updateProperties("server.ip", newIP);
                    controller.updateProperties("server.port", newPort);
                }
                break;
            default:
                break;

        }
    }

    public boolean loginView() throws CloseWindow {
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
        Set<User> users = controller.getModel().getUsers();
        int itemCount = cmbLogin.getItemCount();
        String[] logins = new String[itemCount];
        for (int i = 0; i < itemCount; i++) {
            logins[i] = cmbLogin.getItemAt(i);
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
        String name = null;
        for (User o : users) {
            if (o.getLogin().equals(result)) {
                name = o.getFirstName() + " " + o.getSecondName();
            }
        }

        JPanel panel = new JPanel();
        JLabel label = new JLabel(name + " введите ваш пароль :");
        JPasswordField pass = new JPasswordField(15);
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK", "Отмена"};
        int option = JOptionPane.showOptionDialog(null, panel, "Ввод пароля",
                YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, pass);
        String password = "";
        if (option == 0) {
            password = String.valueOf(pass.getPassword());
        }

        if (validation(password, result.toString())) {
            JOptionPane.showMessageDialog(this, "Добро пожаловать!");
            activeUser = result.toString();
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Не верный пароль, попробуйте еще раз.");
            return false;
        }
    }

    public void refreshUsers() {
        cmbLogin.removeAllItems();
        ClientGuiModel model = controller.getModel();
        Set<User> users = model.getUsers();
        for (User o : users) {
            cmbLogin.addItem(o.getLogin());
        }
    }

    private boolean validation(String password, String userName) {
        User tmp = null;
        for (User u : controller.getModel().getUsers()) {
            if (u.getLogin().equals(userName)) {
                tmp = u;
            }
        }
        if (String.valueOf(password).equals(tmp.getPassword())) return true;

        return false;
    }

    private void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void mainView() {
        setContentPane(pnlMain);
        cmbRackName.setMaximumRowCount(10);
        cmbReference.setMaximumRowCount(15);
        User tmpUser = null;
        for (User u : controller.getModel().getUsers()) {
            if (u.getLogin().equals(controller.getCurrentUser())) {
                tmpUser = u;
            }
        }
        lblCurrentUser.setText("User: " + tmpUser.getFirstName() + " " + tmpUser.getSecondName());

        switch (tmpUser.getRole()) {
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
                        //          TreeMap<Integer, String> listOfManagersCommands = controller.getListOfManagersCommands();
                        for (Integer k : listOfManagersCommands.keySet()) {
                            if (listOfManagersCommands.get(k).equals(command)) {
                                switch (k) {
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
                                        ImportDataCells importDataCells = new ImportDataCells();
                                        importDataCells.initView(controller);
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
        // pack();
    }


    private void setupGUI(int i) {
        btnForcePickUp.setEnabled(false);
        pnlManager.setVisible(false);
        pnlStoreKeeper.setVisible(false);
        pnlLogisticDriver.setVisible(true);
        switch (i) {
            case 1:
                btnForcePickUp.setEnabled(true);
            case 2:
                pnlManager.setVisible(true);
                btnForcePickUp.setEnabled(true);
            case 3:
                pnlStoreKeeper.setVisible(true);
                btnForcePickUp.setEnabled(true);
            case 4:
                pnlLogisticDriver.setVisible(true);
                break;
        }
    }

    private void printTable(String tableName) {
        selectReferenceList(tableName);
        //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        Rack rack = null;
        for (Rack r : controller.getModel().getRacks()) {
            if (tableName.equals(r.getName())) {
                rack = r;
            }
        }

        int currentRackRowCount = rack.getRow();
        int currentRackColumnCount = rack.getCol();
        Cell[][] cells = rack.getCells();

        lblTableName.setText(rack.getName());

        String[] header = new String[currentRackColumnCount];
        for (int j = 0; j < currentRackColumnCount; j++) {
            header[j] = String.valueOf((char) (65 + j));
        }
        TableModel tblModel = new TableModel(currentRackRowCount, currentRackColumnCount, header);
        TableCellRenderer tableCellRenderer = new MyCellRender(scrTable.getWidth(), currentRackColumnCount);
        TableCellEditor tableCellEditor = new MyCellEditor(new JCheckBox(), scrTable.getWidth(), currentRackColumnCount);
        mainTable.setModel(tblModel);
        mainTable.setGridColor(Color.DARK_GRAY);
        //mainTable.setGridColor();
        JTable rowTable = new RowNumberTable(mainTable);
        scrTable.setRowHeaderView(rowTable);
        for (int i = 0; i < currentRackRowCount; i++) {
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
                                // data.setValue(position, pallet.getMaterial() + "<br>" + pallet.getLoadingDate().format(dateTimeFormatter));
                                data.setValue(position, "P");
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
                                    // data.setValue(position, pallet.getMaterial() + "<br>" + pallet.getLoadingDate().format(dateTimeFormatter));
                                    data.setValue(position, pallet.getMaterial());
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
                mainTable.setValueAt(value, i, j);
                mainTable.getColumnModel().getColumn(j).setCellEditor(tableCellEditor);
                mainTable.getColumnModel().getColumn(j).setCellRenderer(tableCellRenderer);
            }
            mainTable.setRowHeight(i, ROW_HEIGHT);
        }

        mainTable.setRowHeight(0, ROW_HEIGHT);
    }

    private void selectReferenceList(String tableName) {
        ArrayList<String> listOfReferences = new ArrayList<>();
        cmbReference.removeAllItems();
        for (SAPReference s : controller.getModel().getReferences()) {
            String[] allowedRacks = s.getAllowedRacks();
            for (String rack : allowedRacks) {
                if (rack.equals(tableName)) {
                    listOfReferences.add(s.getReference());
                    break;
                }
            }
        }
        Collections.sort(listOfReferences);
        for (String s : listOfReferences) {
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
        for (Rack r : controller.getModel().getRacks()) {
            listOfRacks.add(r.getName());
        }

        cmbRackName.removeAllItems();
        Collections.sort(listOfRacks);
        for (String s : listOfRacks) {
            cmbRackName.addItem(s);
        }
    }

//    public void refreshLog() {
//        txtaLog.setText("");
//        TreeMap<LocalDateTime, String> log = controller.getLog();
//        for (LocalDateTime dateTime : log.keySet()) {
//            if (dateTime.isAfter(LocalDateTime.now().minusDays(7))) {
//                DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM HH:mm");
//                dateTime.format(f);
//                String mes = dateTime.format(f) + " - " + log.get(dateTime) + "\n";
//                try {
//                    txtaLog.getDocument().insertString(0, mes, null);
//                } catch (BadLocationException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    public void refreshView() {
        refreshRackList();
        refreshRack();
        //refreshLog();
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
        pnlMain.setAlignmentX(0.0f);
        pnlMain.setAlignmentY(0.0f);
        pnlMain.setAutoscrolls(false);
        pnlMain.setMaximumSize(new Dimension(1280, 780));
        pnlMain.setMinimumSize(new Dimension(1280, 780));
        pnlMain.setPreferredSize(new Dimension(1280, 780));
        scrTable = new JScrollPane();
        scrTable.setHorizontalScrollBarPolicy(31);
        scrTable.setVerticalScrollBarPolicy(21);
        pnlMain.add(scrTable, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(980, 780), new Dimension(980, 780), new Dimension(980, 780), 0, false));
        mainTable = new JTable();
        mainTable.setMaximumSize(new Dimension(950, 780));
        mainTable.setMinimumSize(new Dimension(950, 780));
        mainTable.setName(ResourceBundle.getBundle("properties/labelNames").getString("mainTable"));
        mainTable.setPreferredScrollableViewportSize(new Dimension(950, 780));
        mainTable.setPreferredSize(new Dimension(950, 780));
        scrTable.setViewportView(mainTable);
        pnlRight = new JPanel();
        pnlRight.setLayout(new GridLayoutManager(12, 2, new Insets(0, 0, 0, 0), -1, -1));
        pnlRight.setName("pnlRight");
        pnlMain.add(pnlRight, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(300, -1), new Dimension(300, -1), new Dimension(300, -1), 0, false));
        pnlRight.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        pnlTopInfo = new JPanel();
        pnlTopInfo.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        pnlTopInfo.setName("pnlTopInfo");
        pnlRight.add(pnlTopInfo, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(280, -1), new Dimension(280, -1), new Dimension(280, -1), 0, false));
        lblTableName = new JLabel();
        Font lblTableNameFont = this.$$$getFont$$$(null, Font.BOLD, 22, lblTableName.getFont());
        if (lblTableNameFont != null) lblTableName.setFont(lblTableNameFont);
        lblTableName.setText("");
        pnlTopInfo.add(lblTableName, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(200, 40), new Dimension(200, 40), new Dimension(200, 40), 0, false));
        lblSelectedCell = new JLabel();
        Font lblSelectedCellFont = this.$$$getFont$$$(null, Font.BOLD, 18, lblSelectedCell.getFont());
        if (lblSelectedCellFont != null) lblSelectedCell.setFont(lblSelectedCellFont);
        lblSelectedCell.setName(ResourceBundle.getBundle("properties/labelNames").getString("lblSelectedCell"));
        lblSelectedCell.setText("");
        pnlTopInfo.add(lblSelectedCell, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(60, 40), new Dimension(60, 40), new Dimension(60, 40), 0, false));
        cmbRackName = new JComboBox();
        cmbRackName.setAlignmentX(0.0f);
        Font cmbRackNameFont = this.$$$getFont$$$(null, -1, 28, cmbRackName.getFont());
        if (cmbRackNameFont != null) cmbRackName.setFont(cmbRackNameFont);
        cmbRackName.setName(ResourceBundle.getBundle("properties/labelNames").getString("cmbRackName"));
        pnlRight.add(cmbRackName, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(280, 40), new Dimension(280, 40), new Dimension(280, 40), 0, false));
        scrDataPane = new JScrollPane();
        scrDataPane.setHorizontalScrollBarPolicy(31);
        scrDataPane.setName("scrDataPane");
        scrDataPane.setVerticalScrollBarPolicy(21);
        pnlRight.add(scrDataPane, new GridConstraints(10, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(250, -1), new Dimension(250, -1), new Dimension(250, -1), 0, false));
        txtCellInfo = new JTextArea();
        txtCellInfo.setEditable(false);
        Font txtCellInfoFont = this.$$$getFont$$$(null, Font.BOLD, 26, txtCellInfo.getFont());
        if (txtCellInfoFont != null) txtCellInfo.setFont(txtCellInfoFont);
        txtCellInfo.setMaximumSize(new Dimension(250, 120));
        txtCellInfo.setMinimumSize(new Dimension(250, 120));
        txtCellInfo.setName(ResourceBundle.getBundle("properties/labelNames").getString("txtCellInfo"));
        txtCellInfo.setPreferredSize(new Dimension(250, 120));
        txtCellInfo.setWrapStyleWord(true);
        scrDataPane.setViewportView(txtCellInfo);
        pnlLogisticDriver = new JPanel();
        pnlLogisticDriver.setLayout(new GridLayoutManager(3, 1, new Insets(0, 5, 0, 5), -1, -1));
        pnlRight.add(pnlLogisticDriver, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(200, -1), new Dimension(200, -1), new Dimension(200, -1), 0, false));
        pnlLogisticDriver.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null));
        rbShowRef = new JRadioButton();
        Font rbShowRefFont = this.$$$getFont$$$(null, -1, 14, rbShowRef.getFont());
        if (rbShowRefFont != null) rbShowRef.setFont(rbShowRefFont);
        this.$$$loadButtonText$$$(rbShowRef, ResourceBundle.getBundle("strings").getString("txt_Show"));
        pnlLogisticDriver.add(rbShowRef, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rbAvailableCells = new JRadioButton();
        Font rbAvailableCellsFont = this.$$$getFont$$$(null, -1, 14, rbAvailableCells.getFont());
        if (rbAvailableCellsFont != null) rbAvailableCells.setFont(rbAvailableCellsFont);
        this.$$$loadButtonText$$$(rbAvailableCells, ResourceBundle.getBundle("strings").getString("txt_AvailableCell"));
        pnlLogisticDriver.add(rbAvailableCells, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        pnlLogisticDriver.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 30), new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        pnlStoreKeeper = new JPanel();
        pnlStoreKeeper.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        pnlRight.add(pnlStoreKeeper, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(250, -1), new Dimension(250, -1), new Dimension(250, -1), 0, false));
        pnlStoreKeeper.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), ResourceBundle.getBundle("strings").getString("btn_KeeperFunctions")));
        btnForcePickUp = new JButton();
        Font btnForcePickUpFont = this.$$$getFont$$$(null, -1, 12, btnForcePickUp.getFont());
        if (btnForcePickUpFont != null) btnForcePickUp.setFont(btnForcePickUpFont);
        this.$$$loadButtonText$$$(btnForcePickUp, ResourceBundle.getBundle("strings").getString("btn_PickUpForce"));
        pnlStoreKeeper.add(btnForcePickUp, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(200, 30), new Dimension(200, 30), new Dimension(200, 30), 0, false));
        historyButton = new JButton();
        Font historyButtonFont = this.$$$getFont$$$(null, -1, 12, historyButton.getFont());
        if (historyButtonFont != null) historyButton.setFont(historyButtonFont);
        this.$$$loadButtonText$$$(historyButton, ResourceBundle.getBundle("strings").getString("rb_History"));
        pnlStoreKeeper.add(historyButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(200, 30), new Dimension(200, 30), new Dimension(200, 30), 0, false));
        pnlManager = new JPanel();
        pnlManager.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        pnlRight.add(pnlManager, new GridConstraints(8, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(270, -1), new Dimension(270, -1), new Dimension(270, -1), 0, false));
        pnlManager.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), ResourceBundle.getBundle("strings").getString("btn_ManagerMenu")));
        btnRunManagerCommand = new JButton();
        Font btnRunManagerCommandFont = this.$$$getFont$$$(null, -1, 14, btnRunManagerCommand.getFont());
        if (btnRunManagerCommandFont != null) btnRunManagerCommand.setFont(btnRunManagerCommandFont);
        this.$$$loadButtonText$$$(btnRunManagerCommand, ResourceBundle.getBundle("strings").getString("btn_Ok"));
        pnlManager.add(btnRunManagerCommand, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(120, 30), new Dimension(120, 30), new Dimension(120, 30), 0, false));
        cmbManagerFunctions = new JComboBox();
        Font cmbManagerFunctionsFont = this.$$$getFont$$$(null, -1, 12, cmbManagerFunctions.getFont());
        if (cmbManagerFunctionsFont != null) cmbManagerFunctions.setFont(cmbManagerFunctionsFont);
        pnlManager.add(cmbManagerFunctions, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, 50), new Dimension(250, 50), new Dimension(250, 50), 0, false));
        cmbReference = new JComboBox();
        Font cmbReferenceFont = this.$$$getFont$$$(null, -1, 20, cmbReference.getFont());
        if (cmbReferenceFont != null) cmbReference.setFont(cmbReferenceFont);
        cmbReference.setName(ResourceBundle.getBundle("properties/labelNames").getString("cmbReference"));
        pnlRight.add(cmbReference, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(200, 40), new Dimension(200, 40), new Dimension(200, 40), 0, false));
        btnLoad = new JButton();
        btnLoad.setAlignmentX(0.0f);
        Font btnLoadFont = this.$$$getFont$$$(null, -1, 14, btnLoad.getFont());
        if (btnLoadFont != null) btnLoad.setFont(btnLoadFont);
        btnLoad.setMargin(new Insets(0, 0, 0, 0));
        btnLoad.setName(ResourceBundle.getBundle("properties/labelNames").getString("btnLoad"));
        this.$$$loadButtonText$$$(btnLoad, ResourceBundle.getBundle("strings").getString("btn_Load"));
        pnlRight.add(btnLoad, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(120, 60), new Dimension(120, 60), new Dimension(120, 60), 0, false));
        btnPickUp = new JButton();
        Font btnPickUpFont = this.$$$getFont$$$(null, -1, 14, btnPickUp.getFont());
        if (btnPickUpFont != null) btnPickUp.setFont(btnPickUpFont);
        btnPickUp.setHorizontalAlignment(0);
        btnPickUp.setHorizontalTextPosition(11);
        btnPickUp.setName(ResourceBundle.getBundle("properties/labelNames").getString("btnPickUp"));
        this.$$$loadButtonText$$$(btnPickUp, ResourceBundle.getBundle("strings").getString("btn_PickUp"));
        pnlRight.add(btnPickUp, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(120, 60), new Dimension(120, 60), new Dimension(120, 60), 0, false));
        rbSelectDate = new JRadioButton();
        Font rbSelectDateFont = this.$$$getFont$$$(null, -1, 12, rbSelectDate.getFont());
        if (rbSelectDateFont != null) rbSelectDate.setFont(rbSelectDateFont);
        this.$$$loadButtonText$$$(rbSelectDate, ResourceBundle.getBundle("strings").getString("btn_SelectDate"));
        pnlRight.add(rbSelectDate, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 25), new Dimension(100, 25), new Dimension(100, 25), 0, false));
        lblCurrentUser = new JLabel();
        lblCurrentUser.setText("");
        pnlRight.add(lblCurrentUser, new GridConstraints(11, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, -1), new Dimension(200, -1), new Dimension(200, -1), 0, false));
        final Spacer spacer2 = new Spacer();
        pnlRight.add(spacer2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 30), new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        final Spacer spacer3 = new Spacer();
        pnlRight.add(spacer3, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
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
