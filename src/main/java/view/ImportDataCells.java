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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ImportDataCells extends JFrame {
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

    public ImportDataCells() {
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
                                String[] values = line.split(";");
                                records.add(values);
                            }
                        }
                        Object[] headers = new Object[]{"#","Rack Name","Reference","Date","Cell","Volume"};
                        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                        tblData.setModel(new DefaultTableModel(headers,0));
                        DefaultTableModel model = (DefaultTableModel) tblData.getModel();
                        tblData.getColumn("#").setPreferredWidth(50);
                        tblData.getColumn("Rack Name").setPreferredWidth(100);
                        tblData.getColumn("Reference").setPreferredWidth(200);
                        tblData.getColumn("Date").setPreferredWidth(160);
                        tblData.getColumn("Cell").setPreferredWidth(80);
                        tblData.getColumn("Volume").setPreferredWidth(80);
                        tblData.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
                        tblData.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
                        for (int i=0; i < records.size(); i++){
                            if (records.get(i).length == 5){
                                model.addRow(new Object[]{i+1,records.get(i)[0], records.get(i)[1], records.get(i)[2],records.get(i)[3],records.get(i)[4]});
                               // System.out.println(records.get(i)[0] + " : " +records.get(i)[1]+ " : " +records.get(i)[2]+ " : " + records.get(i)[3]);
                            } else if (records.get(i).length > 5){
                                model.addRow(new Object[]{i+1,records.get(i)[0], records.get(i)[1], records.get(i)[2],records.get(i)[3],records.get(i)[4]});
                                txtaStatus.append("Error in line:" + i + "\n");
                            } else {
                                switch (records.get(i).length){
                                    case 1:
                                        model.addRow(new Object[]{i+1,records.get(i)[0],"", "","",""});
                                        break;
                                    case 2:
                                        model.addRow(new Object[]{i+1,records.get(i)[0],records.get(i)[1], "","",""});
                                        break;
                                    case 3:
                                        model.addRow(new Object[]{i+1,records.get(i)[0],records.get(i)[1], records.get(i)[2],"",""});
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
                        String rackName = (String) model.getValueAt(i, 1);
                        String reference = (String) model.getValueAt(i, 2);
                        String dateString = (String) model.getValueAt(i, 3);
                        String cellAddress = (String) model.getValueAt(i, 4);
                        int volume = Integer.parseInt((String) model.getValueAt(i, 5));
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                        String yyyy;
                        String MMM;
                        String dd;
                        String time = dateString.substring(dateString.indexOf(" "));
                        if (dateString.contains(".")){
                            dd = dateString.split("\\.")[1];
                            MMM = dateString.split("\\.")[0];
                            yyyy = dateString.split("\\.")[2].split(" ")[0];
                        } else {
                            dd = dateString.split("/")[0];
                            MMM = dateString.split("/")[1];
                            yyyy = dateString.split("/")[2].split(" ")[0];
                        }
                        if (time.split(":").length <= 2){
                            time = time + ":00";
                        }
                        String HH = time.split(":")[0].trim();
                        String mm = time.split(":")[1];
                        String ss = time.split(":")[2];
                        if (HH.length()<2){
                            HH=0+HH;
                        }
                        if (mm.length()<2){
                            mm=0+HH;
                        }
                        time = " " + HH + ":" + mm + ":" + ss;
                        String resultDate = dd + "/" + MMM + "/" + yyyy + time;

                       // LocalDateTime dateForLoading ;
//                        DateTimeFormatter form = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//                        dateForLoading = LocalDateTime.parse(resultDate,form);
                        String cell = cellAddress.split("-")[0];
                        String position = "0";
                        String[] tmp = cellAddress.split("-");
                        if (tmp.length == 3){
                            switch (tmp[1]){
                                case "2":
                                    position = tmp[2];
                                    break;
                                case "3":
                                    switch (tmp[2]){
                                        case "1":
                                            position = "3";
                                            break;
                                        case "2":
                                            position = "4";
                                            break;
                                        case "3":
                                            position = "5";
                                            break;
                                    }
                                    break;
                            }
                        } else if(tmp.length == 2){
                            switch (volume){
                                case 1:
                                    switch (tmp[1]){
                                        case "1":
                                            position = "3";
                                            break;
                                        case "2":
                                            position = "4";
                                            break;
                                        case "3":
                                            position = "5";
                                            break;
                                    }
                                    break;
                                case 2:
                                    position = tmp[2];
                                    break;
                                case 3:
                                    position = "0";
                                    break;
                            }
                        }
                        String str = rackName + " | " + reference + " | " + resultDate + " | " + cell + "[" + position + "]";
                        if (!rackName.trim().isEmpty() && !reference.isEmpty() && !dateString.trim().isEmpty() && !cellAddress.isEmpty()) {
                            String newLine = rackName + controller.getMESSAGE_DELIMITER() + reference + controller.getMESSAGE_DELIMITER() + resultDate + controller.getMESSAGE_DELIMITER() + cell + "[" + position + "]";
                            list.add(newLine);
                        } else {
                            isDataCorrect = false;
                            JOptionPane.showMessageDialog(pnlMain,"Проверьте корректность данных в строке " + (i+1));
                        }
                        System.out.println(str);
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
                    controller.sendMessage(MessageType.IMPORT_EXPORT, 1 + out.toString());
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
        ImportDataCells importData = new ImportDataCells();
        importData.initView(null);
    }
}
