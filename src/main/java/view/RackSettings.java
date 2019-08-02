package view;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.ClientGuiController;
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
import java.text.ParseException;
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

        CheckListItem[] checkBoxes = new CheckListItem[references.size()];

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setBusy(false);
                controller.getView().refreshRack();
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

                    Arrays.fill(checkBoxes, null);

                    for (int i = 0; i < references.size(); i++) {
                        CheckListItem tmpCheckItem = new CheckListItem(references.get(i).getReference());
                        if (references.get(i).isAllowedRack(cmbRacks.getSelectedItem().toString())) {
                            tmpCheckItem.setSelected(true);
                        }
                        checkBoxes[i] = tmpCheckItem;
                    }
                    Arrays.sort(checkBoxes);
                    lstReferences.setListData(checkBoxes);
                    lstReferences.setCellRenderer(new CheckListRenderer());
                    lstReferences.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
        Rack tmpRack = new Rack(fTxtRackName.getText(), Integer.parseInt(fTxtColumnNum.getText()), Integer.parseInt(fTxtRowsNum.getText()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(out, tmpRack);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ListModel model = lstReferences.getModel();
        List<CheckListItem> selectedValuesList = new ArrayList();
        for (int i = 0; i < model.getSize(); i++){
            CheckListItem element = (CheckListItem) model.getElementAt(i);
            if (element.isSelected()){
                selectedValuesList.add(element);
            }
        }
        String refString = "";
        for (CheckListItem o: selectedValuesList){
                refString = refString + o.toString() + ",";
        }
        String data = action + out.toString() + "-_-" + refString;
        controller.sendMessage(MessageType.CHANGE_RACK, data);
    }
}
