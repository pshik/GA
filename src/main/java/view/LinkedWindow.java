package view;

import controller.ClientGuiController;
import model.CheckListItem;
import model.Rack;
import model.SAPReference;
import org.jetbrains.annotations.NotNull;
import server.MessageType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class LinkedWindow extends JFrame{
    private JComboBox cmbRack;
    private JList lstReferences;
    private JButton btnUpdate;
    private JButton btnCancel;
    private JPanel refPane;
    private JScrollPane jscrolPane;


    public LinkedWindow() {
        setTitle("Настройка отображения материалов на стеллажах");
        setVisible(false);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setLocation(300,200);
       // setAlwaysOnTop(true);
        setContentPane(refPane);


    }
    public void initView(ClientGuiController controller, ArrayList<Rack> racks, ArrayList<SAPReference> references){

        if (controller != null) {
            controller.setBusy(true);
        }
        ArrayList<String> rackNames = new ArrayList<>();
        for (Rack r: racks){
            rackNames.add(r.getName());
        }
        Collections.sort(rackNames);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.setBusy(false);
                    controller.getView().refreshRack();
                }
                dispose();
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rackName = cmbRack.getSelectedItem().toString();
                ListModel model = lstReferences.getModel();
                List<CheckListItem> selectedValuesList = new ArrayList();
                for (int i = 0; i < model.getSize(); i++){
                    CheckListItem element = (CheckListItem) model.getElementAt(i);
                    if (element.isSelected()){
                        selectedValuesList.add(element);
                    }
                }
                String refList ="";
                for (CheckListItem o: selectedValuesList){
                    refList = refList + "," + o.toString();
                }
                if (controller != null) {
                    controller.sendMessage(MessageType.CHANGE_LINK_RACK_TO_REF, rackName + controller.MESSAGE_DELIMITER + refList);
                }
            }
        });

        CheckListItem[] checkBoxes = new CheckListItem[references.size()];

        cmbRack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i <  checkBoxes.length; i++){
                    checkBoxes[i] = null;
                }
                for (int i = 0; i < references.size(); i++){
                    CheckListItem tmpCheckItem = new CheckListItem(references.get(i).getReference());
                    if (references.get(i).isAllowedRack(cmbRack.getSelectedItem().toString())){
                        tmpCheckItem.setSelected(true);
                    }
                    checkBoxes[i]= tmpCheckItem;
                }
                Arrays.sort(checkBoxes);
                lstReferences.setListData(checkBoxes);
                lstReferences.setCellRenderer(new CheckListRenderer());
                lstReferences.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
        for (String s: rackNames){
            cmbRack.addItem(s);
        }
        pack();
        setVisible(true);
    }


//    public static void main(String[] args) {
//        Rack test1 = new Rack("Rack1",6,7);
//        Rack test2 = new Rack("Rack2",7,5);
//        Rack test3 = new Rack("Rack3",5,4);
//        Rack test4 = new Rack("Rack4",5,7);
//        Rack test5 = new Rack("Rack5",4,6);
//        ArrayList<Rack> racks = new ArrayList<>();
//        racks.add(test1);
//        racks.add(test2);
//        racks.add(test3);
//        racks.add(test4);
//        racks.add(test5);
//        SAPReference reference = new SAPReference("61021230234-03","HL",1,"Rack1","Rack2","Rack3","Rack4","Rack5");
//        SAPReference reference1 = new SAPReference("61021230234-01","WR",1,"Rack1","Rack2","Rack3","Rack4","Rack5");
//        SAPReference reference2 = new SAPReference("14535322452-01","LR",2,"Rack1","Rack2","Rack5");
//        SAPReference reference3 = new SAPReference("14535322452","LR",3,"Rack1","Rack4","Rack5");
//        ArrayList<SAPReference> references = new ArrayList<>();
//        references.add(reference);
//        references.add(reference1);
//        references.add(reference2);
//        references.add(reference3);
//        references.add(reference);
//        references.add(reference1);
//        references.add(reference2);
//        references.add(reference3);
//        references.add(reference);
//        references.add(reference1);
//        references.add(reference2);
//        references.add(reference3);
//        references.add(reference);
//        references.add(reference1);
//        references.add(reference2);
//        references.add(reference3);
//        references.add(reference);
//        references.add(reference1);
//        references.add(reference2);
//        references.add(reference3);
//        references.add(reference);
//        references.add(reference1);
//        references.add(reference2);
//        references.add(reference3);
//        references.add(reference);
//        references.add(reference1);
//        references.add(reference2);
//        references.add(reference3);
//        references.add(reference);
//        references.add(reference1);
//        references.add(reference2);
//        references.add(reference3);
//        references.add(reference);
//        references.add(reference1);
//        references.add(reference2);
//        references.add(reference3);
//
//
//        new LinkedWindow().initView(null,racks,references);
//    }
}
