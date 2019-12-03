package controller;

import dao.Base;
import log.Event;
import log.LogParser;
import log.LoggerFiFo;
import model.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class ServerController {
    private static Base base = Base.getInstance();
    private static ServerController serverController = new ServerController();
    private static ConcurrentMap users = base.getBase("Users");
    private static ConcurrentMap references = base.getBase("References");
    private static ConcurrentMap racks = base.getBase("Racks");
    private ArrayList<User> connectedUser = new ArrayList<>();

    public ArrayList<User> getConnectedUser() {
        return connectedUser;
    }
    public void addConnectedUser(User user){
        connectedUser.add(user);
    }

    @Contract(pure = true)
    public static ServerController getServerController() {
        return serverController;
    }

    private boolean access = false;

    public boolean isAccess() {
        return access;
    }


    public  void setAccess(boolean access) {
        this.access = access;
    }

    private ServerController() {
    }

    public static void main(String[] args) {
      //  System.setProperty("log4j.configurationFile","./myData/log4j2.xml");
        LogParser log = LogParser.getInstance();

       // if (serverController.isAccess()){

       // }
    }

    public void printLog(){
      //  System.out.println(logger.);
    }
    public synchronized boolean loadPalletToRack(String currentUser, String cellFulPath, String refName, String lblTableName, @NotNull String manualDate){
        boolean isCorrect = false;
        Rack tmpRack = (Rack) racks.get(lblTableName);
        SAPReference material = (SAPReference) references.get(refName);
        LocalDateTime dateForLoading = LocalDateTime.now();
        if (!manualDate.equals("null")){
          //  System.out.println(manualDate);
           // System.out.println(dateForLoading);
            DateTimeFormatter form = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            dateForLoading = LocalDateTime.parse(manualDate,form);
           // System.out.println(dateForLoading);

        }
        Pallet pallet = new Pallet(material.getReference(),material.getSize(), dateForLoading);
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
        Cell cell = tmpRack.getCellByName(cellName);

            if (cell.getPallets() != null) {
                for (Pallet p : cell.getPallets()) {
                    if (p.getPosition() == pos) {
                        System.out.println("Ячейка занята");
                  //      JOptionPane.showMessageDialog(pnlMain, "Ячейка занята");
                        isBusy = true;
                    } else {
                        if (lockedPositions.contains(p.getPosition())) {
                            System.out.println("Не корректное размещение палета");
                        //    JOptionPane.showMessageDialog(pnlMain, "Не корректное размещение палета");
                            isBusy = true;
                        }
                    }
                }
                cell.addPallet(pallet);
            } else {
                if (material.getSize() <= localSize) {
                    cell.addPallet(pallet);
                }
                else {
                    System.out.println("Не корректное размещение палета");
                    //JOptionPane.showMessageDialog(pnlMain, "Не корректное размещение палета");
                    isBusy = true;
                }
            }

        if (!isBusy) {
            tmpRack.setCellByAddress(cell.getRow(),cell.getCol(),cell);
            //cells.replace(lblTableName + ":" + cellName, o);
            racks.replace(tmpRack.getName(),tmpRack);
            LoggerFiFo.getInstance().getRootLogger().info(String.format("User %s: Load pallet. %s %s[%s] %s",currentUser,tmpRack.getName(),cellName,pos,material.getReference()));
            LogParser.getInstance().updateLog();
          //  loadHistory();
            /// "User: currentUser: Successfully loading pallet. Rack1 cellA6 material
            isCorrect = true;
        }
        return isCorrect;
    }
    public synchronized boolean pickupPallet(String currentUser, @NotNull String cellFulPath, String refName, String lblTableName){
        boolean isCorrect = false;

        ConcurrentMap tmp = base.getBase("Racks");
        Rack tmpRack = (Rack) racks.get(lblTableName);
        int pos = Integer.parseInt(cellFulPath.substring(cellFulPath.indexOf("[")+1,cellFulPath.indexOf("]")));
        String cellName = cellFulPath.substring(0,cellFulPath.indexOf("["));

        boolean isExist = true;
        try{
            Cell cell = tmpRack.getCellByName(cellName);
                for (Pallet pallet: cell.getPallets()){
                    if (pallet.getPosition() == pos){
                        if(pallet.getMaterial().equals(refName)){
                            cell.pickUpPallet(pos,refName);
                            isExist = true;
                            break;
                        } else {
                            isExist = false;
                        }
                    }
                }
            if (!isExist){
                System.out.println("Ячейка не содержит нужный вам материал.");
            } else {
                tmpRack.setCellByAddress(cell.getRow(),cell.getCol(),cell);
                racks.replace(tmpRack.getName(),tmpRack);
                LoggerFiFo.getInstance().getRootLogger().info(String.format("User %s: PickUp pallet. %s %s[%s] %s",currentUser,lblTableName,cellName,pos,refName));
                LogParser.getInstance().updateLog();
                isCorrect = true;
            }
        } catch (Exception e){
            racks = tmp;
        }

        return isCorrect;
    }
    public synchronized boolean forcedPickUp(String currentUser, String cellFulPath, String refName, String lblTableName){
        boolean isCorrect = false;
        ConcurrentMap tmp = base.getBase("Racks");
        Rack rack = (Rack) racks.get(lblTableName);
        int pos = Integer.parseInt(cellFulPath.substring(cellFulPath.indexOf("[") + 1, cellFulPath.indexOf("]")));
        String cellName = cellFulPath.substring(0, cellFulPath.indexOf("["));
        try {
            Cell cell = rack.getCellByName(cellName);
                for (Pallet pallet : cell.getPallets()) {
                    if (pallet.getPosition() == pos) {
                        cell.pickUpPallet(pos, refName);
                    }
                }
                rack.setCellByAddress(cell.getRow(),cell.getCol(),cell);
                racks.replace(rack.getName(), rack);
                LoggerFiFo.getInstance().getRootLogger().info(String.format("User %s: Force pickup pallet. %s %s[%s] %s", currentUser, lblTableName, cellName,pos, refName));
                LogParser.getInstance().updateLog();
                isCorrect = true;
        } catch (Exception e) {
            racks = tmp;
        }
        return isCorrect;
    }

    public synchronized boolean changeRack(String currentUser, Rack rack, int action, String refList) {
        boolean isCorrect = false;
        ConcurrentMap tmp = base.getBase("Racks");
        String actionString = "";
        try {
            switch (action){
                case 0:
                    racks.remove(rack.getName());
                    for (Object r : references.keySet()) {
                        SAPReference o = (SAPReference) references.get(r);
                        for (String t : o.getAllowedRacks()) {
                            if (t.equals(rack.getName())) {
                                o.removeAllowedRack(rack.getName());
                            }
                        }
                        references.replace(o.getReference(), o);
                    }
                    actionString="remove";
                    break;
                case 1:
                    if (racks.containsKey(rack.getName())) {
                        racks.replace(rack.getName(), rack);
                    } else {
                        racks.put(rack.getName(), rack);
                    }
                    base.fillNewRack(rack);
                    changeLinkRackToRef(rack.getName(),refList);
                    actionString="change";
                    break;
            }
            isCorrect = true;
            LoggerFiFo.getInstance().getRootLogger().info(String.format("User %s: %s rack: %s", currentUser,actionString, rack.getName()));
            LogParser.getInstance().updateLog();
        } catch (Exception e) {
            racks = tmp;
        }
        return isCorrect;
    }

    public boolean changeLinkRackToRef(String rackName, String referencesList) {
        boolean isCorrect = false;
        ConcurrentMap tmp = base.getBase("References");
        try {
          for(Object o: references.keySet()){
              String refName = (String) o;
              SAPReference sapReference = (SAPReference) references.get(refName);
              if (referencesList.contains(refName)){
                  if (!sapReference.isAllowedRack(rackName)){
                      sapReference.addAllowedRack(rackName);
                      references.replace(sapReference.getReference(),sapReference);
                  }
              } else {
                  if (sapReference.isAllowedRack(rackName)){
                      sapReference.removeAllowedRack(rackName);
                      references.replace(sapReference.getReference(),sapReference);
                  }
              }
          }
            isCorrect = true;
        } catch (Exception e) {
            references = tmp;
        }
        return isCorrect;
    }

    public boolean changeReference(String currentUser, SAPReference reference, int action) {
        boolean isCorrect = false;
        ConcurrentMap tmp = base.getBase("References");
        String actionString = "";
        try {
            switch (action){
                case 0:
                    references.remove(reference.getReference());
                    actionString = "delete";
                    break;
                case 1:

                    if (references.containsKey(reference.getReference())) {
                        references.replace(reference.getReference(), reference);
                        actionString = "change";
                    } else {
                        references.put(reference.getReference(), reference);
                        actionString = "create";
                    }
                    break;
            }
            isCorrect = true;
            LoggerFiFo.getInstance().getRootLogger().info(String.format("User %s: %s reference: %s", currentUser,actionString, reference.getReference()));
            LogParser.getInstance().updateLog();
        } catch (Exception e) {
            references = tmp;
        }

        return isCorrect;
    }

    public boolean changeUser(String currentUser, User user, int action) {
        boolean isCorrect = false;
        ConcurrentMap tmp = base.getBase("Users");
        String actionString = "";
        try {
            switch (action){
                case 0:
                    users.remove(user.getLogin());
                    actionString = "delete";
                    break;
                case 1:
                    if (users.containsKey(user.getLogin())) {
                        users.replace(user.getLogin(), user);
                        actionString = "change";
                    } else {
                        users.put(user.getLogin(), user);
                        actionString = "create";
                    }
                    break;
            }
            isCorrect = true;
            LoggerFiFo.getInstance().getRootLogger().info(String.format("User %s: %s user: %s", currentUser,actionString, user.getLogin()));
            LogParser.getInstance().updateLog();
        } catch (Exception e) {
            users = tmp;
        }

        return isCorrect;
    }

    public boolean importExport(String userName, int action, Object[] list) {
        boolean isCorrect = false;
        switch (action) {
            case 0:
                for (Object o : list) {
                    SAPReference ref = null;
                    try {
                        LinkedHashMap<String,Object> test = (LinkedHashMap<String, Object>) o;
                     //   ref = (SAPReference) o;
                        String reference = (String) test.get("reference");
                        String description = (String) test.get("description");
                        int size = (int) test.get("size");
                        ArrayList<String> allowedRacks = (ArrayList<String>) test.get("allowedRacks");
                        String[] allowed = new String[allowedRacks.size()];
                        for (int i = 0; i < allowedRacks.size(); i++){
                            allowed[i] = allowedRacks.get(i);
                        }
                        ref = new SAPReference(reference, description , size,  allowed);
                    } catch (Exception e) {
                        System.out.println("Error during cast to SAPReference");
                    }
                    if (ref != null) {
                        if (references.containsKey(ref.getReference())) {
                            references.replace(ref.getReference(), ref);
                        } else {
                            references.put(ref.getReference(), ref);
                        }
                    }
                }
                isCorrect = true;
                break;
            case 1:
               // loadPalletToRack(String currentUser, String cellFulPath, String refName, String lblTableName, String manualDate)
                for (Object o : list) {
                    String line = (String) o;
                    String[] data = line.split("-_-");
                    String cellPath = data[3];
                    String reference = data[1];
                    String rackName = data[0];
                    String dateLoaded = data[2];
                    if(dateLoaded.split(":").length == 2){
                        dateLoaded = dateLoaded + ":00";
                    }
//                    DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//                    dateForLoading = LocalDateTime.parse(dateLoaded,form);
                    loadPalletToRack(userName,cellPath,reference,rackName,dateLoaded);
                }
                isCorrect = true;
                break;
        }
        return isCorrect;
    }

    public TreeMap<LocalDateTime,String> getInfoLog() {
        ArrayList<Event> events = LogParser.getInstance().getEvents();
        TreeMap<LocalDateTime,String> infoMessages = new TreeMap<>(Collections.reverseOrder());
        for (Event e: events){
            if (e.getLevel().equals("INFO")){
                infoMessages.put(e.getDateTime(),e.getMessage());
            }
        }
        return infoMessages;
    }
}
