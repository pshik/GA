package view;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.ClientGuiController;
import model.SAPReference;
import server.MessageType;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class ImportData extends JFrame {
    private JButton btnBrowse;
    private JTable tblData ;
    private JScrollPane scrTable;
    private JPanel pnlTable;
    private JPanel pnlPath;
    private JPanel pnlMain;
    private JButton btnImport;
    private JButton btnExit;
    private JTextArea txtaStatus;
    final JFileChooser fc = new JFileChooser();

    public ImportData() {
        setTitle("Настройка стеллажей");
        setVisible(false);
      //  setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setLocation(200,200);
        // setAlwaysOnTop(true);
        setContentPane(pnlMain);
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".csv");
                }
            }

            @Override
            public String getDescription() {
                return "CSV Documents (*.csv)";
            }
        });

    }

    public void initView(ClientGuiController controller){

        btnBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(pnlMain);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    ArrayList<String[]> records = new ArrayList<>();
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (!line.isEmpty()) {
                                String[] values = line.split(",");
                                records.add(values);
                            }
                        }
                        Object[] headers = new Object[]{"#","SAP Reference","Size","Description","Allowed Racks"};
                        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                        tblData.setModel(new DefaultTableModel(headers,0));
                        DefaultTableModel model = (DefaultTableModel) tblData.getModel();
                        tblData.getColumn("#").setPreferredWidth(10);
                        tblData.getColumn("SAP Reference").setPreferredWidth(120);
                        tblData.getColumn("Size").setPreferredWidth(30);
                        tblData.getColumn("Description").setPreferredWidth(200);
                        tblData.getColumn("Allowed Racks").setPreferredWidth(120);
                        tblData.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
                        tblData.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
                        for (int i=0; i < records.size(); i++){
                            if (records.get(i).length == 4){
                                model.addRow(new Object[]{i+1,records.get(i)[0], records.get(i)[1], records.get(i)[2],records.get(i)[3]});
                               // System.out.println(records.get(i)[0] + " : " +records.get(i)[1]+ " : " +records.get(i)[2]+ " : " + records.get(i)[3]);
                            } else if (records.get(i).length > 4){
                                model.addRow(new Object[]{i+1,records.get(i)[0], records.get(i)[1], records.get(i)[2],records.get(i)[3]});
                                txtaStatus.append("Error in line:" + i + "\n");
                            } else {
                                switch (records.get(i).length){
                                    case 1:
                                        model.addRow(new Object[]{i+1,records.get(i)[0],"", "",""});
                                        break;
                                    case 2:
                                        model.addRow(new Object[]{i+1,records.get(i)[0],records.get(i)[1], "",""});
                                        break;
                                    case 3:
                                        model.addRow(new Object[]{i+1,records.get(i)[0],records.get(i)[1], records.get(i)[2],""});
                                        break;
                                }
                                txtaStatus.append("Ошибка в процессе обработки файла. Строка : " + (i+1) + "\n");
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        btnImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isDataCorrect = true;
                DefaultTableModel model = (DefaultTableModel) tblData.getModel();
                ArrayList<Object> list = new ArrayList<>();
                for (int i = 0; i < model.getRowCount(); i++){
                    try {
                        String name = (String) model.getValueAt(i, 1);
                        int size = Integer.parseInt((String) model.getValueAt(i, 2));
                        String description = (String) model.getValueAt(i, 3);
                        String listOfRacks = (String) model.getValueAt(i, 4);
                        if (!name.trim().isEmpty() && !description.trim().isEmpty()) {
                            SAPReference newRef = new SAPReference(name, description, size, listOfRacks.split(";"));
                            list.add(newRef);
                        } else {
                            isDataCorrect = false;
                            JOptionPane.showMessageDialog(pnlMain,"Проверьте корректность данных в строке " + (i+1));
                        }
                    } catch (NumberFormatException eSize){
                        JOptionPane.showMessageDialog(pnlMain,"Проверьте корректность данных в строке " + (i+1));
                        isDataCorrect = false;
                    }
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectMapper mapper = new ObjectMapper();
                try {
                    mapper.writeValue(out, list);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    isDataCorrect = false;
                }
                if (isDataCorrect) {
                    controller.sendMessage(MessageType.IMPORT_EXPORT, 0 + out.toString());
                }
            }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        pack();
        setVisible(true);
    }
    public static void main(String[] args) {
        ImportData importData = new ImportData();
        importData.initView(null);
    }
}
