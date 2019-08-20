package view;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.ClientGuiController;
import model.CheckListItem;
import model.Rack;
import model.SAPReference;
import server.MessageType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReferenceSettings extends JFrame{
    private JComboBox cmbReferences;
    private JButton btnUpdate;
    private JButton btnCreate;
    private JButton btnExit;
    private JLabel lblSize;
    private JLabel lblNewRef;
    private JLabel lblDescription;
    private JComboBox cmbSize;
    private JTextField txtNewRef;
    private JTextField txtDescription;
    private JList lstRacks;
    private JPanel pnlReferences;
    private JButton btnDelete;
    private JButton btnRefresh;
    private ArrayList<Rack> racks = new ArrayList<>();
    private ArrayList<SAPReference> references = new ArrayList<>();

    public ReferenceSettings() {
        setTitle("Настройка материалов");
        setVisible(false);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setLocation(200,200);
        // setAlwaysOnTop(true);
        setContentPane(pnlReferences);
        cmbSize.addItem("Small");
        cmbSize.addItem("Medium");
        cmbSize.addItem("Large");



    }

    public void initView(ClientGuiController controller){

        if (controller != null)
        {
            controller.setBusy(true);
        }

        Collections.sort(references);
        racks.addAll(controller.getModel().getRacks());
        references.addAll(controller.getModel().getReferences());
        CheckListItem[] checkBoxes = new CheckListItem[racks.size()];

        cmbReferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cmbReferences.getItemCount() != 0) {
                    SAPReference currentRef = null;
                    for (SAPReference r : references) {
                        if (r.getReference().equals(cmbReferences.getSelectedItem())) {
                            currentRef = r;
                        }
                    }

                    for (int i = 0; i < checkBoxes.length; i++) {
                        checkBoxes[i] = null;
                    }
                    for (int i = 0; i < racks.size(); i++) {
                        CheckListItem tmpCheckItem = new CheckListItem(racks.get(i).getName());
                        if (currentRef != null && currentRef.isAllowedRack(racks.get(i).getName())) {
                            tmpCheckItem.setSelected(true);
                        }
                        checkBoxes[i] = tmpCheckItem;
                    }
                    Arrays.sort(checkBoxes);
                    lstRacks.setListData(checkBoxes);
                    lstRacks.setCellRenderer(new CheckListRenderer());
                    lstRacks.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    cmbSize.setSelectedIndex(currentRef.getSize() - 1);
                    txtDescription.setText(currentRef.getDescription());
                }
            }
        });
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setBusy(false);
                references.clear();
                references.addAll(controller.getModel().getReferences());
                addRefToCmb(references);
                controller.setBusy(true);
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] rackList = allowedRacks();
                if (txtDescription.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Заполните описание!");
                } else {
                    SAPReference tmp = new SAPReference(cmbReferences.getSelectedItem().toString(), txtDescription.getText(), cmbSize.getSelectedIndex() + 1, rackList);
                    if (controller != null) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            mapper.writeValue(out, tmp);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        String data = "1" + out.toString();
                        controller.sendMessage(MessageType.CHANGE_REFERENCE, data);
                    }
                }
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] rackList = allowedRacks();
                SAPReference tmp = new SAPReference(cmbReferences.getSelectedItem().toString(), txtDescription.getText(), cmbSize.getSelectedIndex() + 1, rackList);
                if (controller != null) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        mapper.writeValue(out, tmp);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    String data = "0" + out.toString();
                    controller.sendMessage(MessageType.CHANGE_REFERENCE, data);
                }
            }
        });
        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] rackList = allowedRacks();
                if (txtNewRef.getText().isEmpty() || txtDescription.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"Введите название материала и описание!");
                } else {
                    SAPReference tmp = new SAPReference(txtNewRef.getText(), txtDescription.getText(), cmbSize.getSelectedIndex() + 1, rackList);
                    if (controller != null) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            mapper.writeValue(out, tmp);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        String data = "1" + out.toString();
                        controller.sendMessage(MessageType.CHANGE_REFERENCE, data);
                    }
                }
            }
        });
        lstRacks.addMouseListener(new MouseAdapter() {
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
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller !=null) {
                    controller.setBusy(false);
                    controller.getView().refreshView();
                }
                dispose();
            }
        });

        addRefToCmb(references);

        pack();
        setVisible(true);
    }
    private void addRefToCmb(ArrayList<SAPReference> list) {
        Collections.sort(list);
        cmbReferences.removeAllItems();
        for (SAPReference r: list){
            cmbReferences.addItem(r.getReference());
        }
    }
    private String[] allowedRacks (){

        ListModel model = lstRacks.getModel();
        List<CheckListItem> selectedValuesList = new ArrayList();
        for (int i = 0; i < model.getSize(); i++){
            CheckListItem element = (CheckListItem) model.getElementAt(i);
            if (element.isSelected()){
                selectedValuesList.add(element);
            }
        }
        String[] rackList = new String[selectedValuesList.size()];
        int next = 0;
        for (CheckListItem o: selectedValuesList){
            rackList[next++] = o.toString();
        }
        return rackList;
    }
    public static void main(String[] args) {
//        String d = "D4";
//        System.out.println(d.substring(0,1));
//
//        System.out.println(d.substring(1,2));
        char c = 65;
        System.out.println(c);
    }
}
