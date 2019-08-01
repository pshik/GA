package view;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.ClientGuiController;
import model.CheckListItem;
import model.Pallet;
import model.Rack;
import model.SAPReference;
import server.Message;
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

import static server.MessageType.USERS_UPDATE;

public class ReferenceSettings extends JFrame{
    private JComboBox cmbReferences;
    private JButton btnUpdate;
    private JButton btnCreate;
    private JButton btnExit;
    private JLabel lblSize;
    private JLabel lblNewRef;
    private JLabel lblDescription;
    private JComboBox cmbSize;
    private JTextField txtfNewRef;
    private JTextField txtfDescription;
    private JList lstRacks;
    private JPanel pnlReferences;
    private JButton btnDelete;

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

    public void initView(ClientGuiController controller, ArrayList<Rack> racks, ArrayList<SAPReference> references){
        if (controller != null)
        {
            controller.setBusy(true);
        }

        Collections.sort(references);

        CheckListItem[] checkBoxes = new CheckListItem[racks.size()];
        cmbReferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                cmbSize.setSelectedIndex(currentRef.getSize()-1);
                txtfDescription.setText(currentRef.getDescription());
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] rackList = allowedRacks();
                if (txtfDescription.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Заполните описание!");
                } else {
                    SAPReference tmp = new SAPReference(cmbReferences.getSelectedItem().toString(), txtfDescription.getText(), cmbSize.getSelectedIndex() + 1, rackList);
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
                    SAPReference tmp = new SAPReference(cmbReferences.getSelectedItem().toString(), txtfDescription.getText(), cmbSize.getSelectedIndex() + 1, rackList);
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
                if (txtfNewRef.getText().isEmpty() || txtfDescription.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"Введите название материала и описание!");
                } else {
                    SAPReference tmp = new SAPReference(txtfNewRef.getText(), txtfDescription.getText(), cmbSize.getSelectedIndex() + 1, rackList);
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
                    controller.getView().refreshRack();
                }
                dispose();
            }
        });

        for (SAPReference s: references){
            cmbReferences.addItem(s.getReference());
        }
        pack();
        setVisible(true);
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
        Rack test1 = new Rack("Rack1",6,7);
        Rack test2 = new Rack("Rack2",7,5);
        Rack test3 = new Rack("Rack3",5,4);
        Rack test4 = new Rack("Rack4",5,7);
        Rack test5 = new Rack("Rack5",4,6);
        ArrayList<Rack> racks = new ArrayList<>();
        racks.add(test1);
        racks.add(test2);
        racks.add(test3);
        racks.add(test4);
        racks.add(test5);
        SAPReference reference = new SAPReference("61021230234-03","HL",1,"Rack1","Rack2","Rack3","Rack4","Rack5");
        SAPReference reference1 = new SAPReference("61021230234-01","WR",1,"Rack1","Rack2","Rack3","Rack4","Rack5");
        SAPReference reference2 = new SAPReference("14535322452-01","LR",2,"Rack1","Rack2","Rack5");
        SAPReference reference3 = new SAPReference("14535322452","LR",3,"Rack1","Rack4","Rack5");
        ArrayList<SAPReference> references = new ArrayList<>();
        references.add(reference3);
        references.add(reference);
        references.add(reference1);
        references.add(reference2);
        references.add(reference3);
        references.add(reference);
        references.add(reference1);
        references.add(reference2);
        references.add(reference3);
        new ReferenceSettings().initView(null,racks,references);
    }
}
