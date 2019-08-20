package view;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.ClientGuiController;
import model.Cell;
import model.CheckListItem;
import model.Rack;
import model.SAPReference;
import server.MessageType;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public class RackSettings extends JFrame{
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
        setLocation(200,200);
        // setAlwaysOnTop(true);
        setContentPane(pnlRacks);
        NumberFormat integerFieldFormatter = NumberFormat.getIntegerInstance();
        integerFieldFormatter.setGroupingUsed(false);
        DefaultFormatterFactory formatterFactory =
                new DefaultFormatterFactory(new NumberFormatter());
        fTxtRowsNum.setFormatterFactory(formatterFactory);
        fTxtColumnNum.setFormatterFactory(formatterFactory);
    }

    public void initView(ClientGuiController controller){
        if (controller != null)
        {
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
                    sendData(controller,0);
                } else {
                    JOptionPane.showMessageDialog(null, "Не рекомендуется удалять последний стеллаж!");
                }
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fTxtRackName.getText().length() <= 10){
                    if (!fTxtRackName.getText().isEmpty() && !fTxtColumnNum.getText().isEmpty() && !fTxtRowsNum.getText().isEmpty()) {
                        boolean isRackExist = false;
                        for (Rack r : racks) {
                            if (r.getName().equals(fTxtRackName.getText())) {
                                isRackExist = true;
                                break;
                            }
                        }
                        if (isRackExist) {
                            sendData(controller,1);
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
                if(fTxtRackName.getText().length() <= 10){
                    if (!fTxtRackName.getText().isEmpty() && !fTxtColumnNum.getText().isEmpty() && !fTxtRowsNum.getText().isEmpty()) {
                        boolean isRackExist = false;
                        for (Rack r : racks) {
                            if (r.getName().equals(fTxtRackName.getText())) {
                                isRackExist = true;
                                break;
                            }
                        }
                        if (!isRackExist) {
                           sendData(controller,1);
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
        for (Rack r: list){
            cmbRacks.addItem(r.getName());
        }
    }

    private void sendData(ClientGuiController controller,int action){
        Rack tmpRack = new Rack(fTxtRackName.getText(), Integer.parseInt(fTxtRowsNum.getText()), Integer.parseInt(fTxtColumnNum.getText()),null);


        ListModel referencesModel = lstReferences.getModel();
        List<CheckListItem> selectedReferencesList = new ArrayList();
        for (int i = 0; i < referencesModel.getSize(); i++){
            CheckListItem element = (CheckListItem) referencesModel.getElementAt(i);
            if (element.isSelected()){
                selectedReferencesList.add(element);
            }
        }
        String refString = "";
        for (CheckListItem o: selectedReferencesList){
            refString = refString + o.toString() + ",";
        }

        ListModel cellsModel = lstCells.getModel();
        for (int i = 0; i < cellsModel.getSize(); i++){
            CheckListItem element = (CheckListItem) cellsModel.getElementAt(i);
            if (element.isSelected()){
                Cell tmpCell = tmpRack.getCellByName(element.toString());
                tmpCell.setBlocked(true);
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
}
