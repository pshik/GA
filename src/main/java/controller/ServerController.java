package controller;

import dao.Base;
import log.LogParser;
import log.LoggerFiFo;
import model.*;
import view.Logon;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

public class ServerController {
    private static Base base = Base.getInstance();
    private static ServerController serverController = new ServerController();
    private static ConcurrentMap users = base.getBase("Users");
    private static ConcurrentMap references = base.getBase("References");
    private static ConcurrentMap racks = base.getBase("Racks");
    private static ConcurrentMap cells = base.getBase("Cells");
    private ArrayList<User> connectedUser = new ArrayList<>();

    public ArrayList<User> getConnectedUser() {
        return connectedUser;
    }
    public void addConnectedUser(User user){
        connectedUser.add(user);
    }

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
        new Logon(serverController,base).setVisible(true);
       // if (serverController.isAccess()){

       // }
    }

    public static Base getBase() {
        return base;
    }

    public static ConcurrentMap getUsers() {
        return users;
    }

    public static ConcurrentMap getReferences() {
        return references;
    }

    public static ConcurrentMap getRacks() {
        return racks;
    }

    public static ConcurrentMap getCells() {
        return cells;
    }

    public void printLog(){
      //  System.out.println(logger.);
    }
    public synchronized boolean loadPalletToRack(String currentUser, String cellFulPath, String refName, String lblTableName, String manualDate){
        boolean isCorrect = false;
        Rack tmpRack = (Rack) racks.get(lblTableName);
        SAPReference material = (SAPReference) references.get(refName);
        LocalDateTime dateForLoading = LocalDateTime.now();
        if (!manualDate.equals("null")){
            System.out.println(manualDate);
            System.out.println(dateForLoading);
            DateTimeFormatter form = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            dateForLoading = LocalDateTime.parse(manualDate,form);
            System.out.println(dateForLoading);

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
        Cell o;
        if( (o = (Cell) cells.get(lblTableName + ":" + cellName)) == null){
            o = new Cell(lblTableName,String.valueOf(tmpRack.getCol()+1),String.valueOf(tmpRack.getRow()+1), pallet);
        }else {
            if (o.getPallets() != null) {
                for (Pallet p : o.getPallets()) {
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
                o.addPallet(pallet);
            } else {
                if (material.getSize() <= localSize) {
                    o.addPallet(pallet);
                }
                else {
                    System.out.println("Не корректное размещение палета");
                    //JOptionPane.showMessageDialog(pnlMain, "Не корректное размещение палета");
                    isBusy = true;
                }
            }
        }

        if (!isBusy) {
            cells.replace(lblTableName + ":" + cellName, o);

            LoggerFiFo.getInstance().getRootLogger().info(String.format("User %s: Load pallet. %s %s %s",currentUser,tmpRack.getName(),cellName,material.getReference()));
            LogParser.getInstance().updateLog();
          //  loadHistory();
            /// "User: currentUser: Successfully loading pallet. Rack1 cellA6 material
            isCorrect = true;
        }
        return isCorrect;
    }
    public synchronized boolean pickupPallet(String currentUser, String cellFulPath, String refName, String lblTableName){
        boolean isCorrect = false;

        ConcurrentMap tmp = base.getBase("Cells");
        int pos = Integer.parseInt(cellFulPath.substring(cellFulPath.indexOf("[")+1,cellFulPath.indexOf("]")));
        String cellName = cellFulPath.substring(0,cellFulPath.indexOf("["));
        boolean isExist = true;
        try{
            Cell o;
            if( (o = (Cell) cells.get(lblTableName + ":" + cellName)) == null){
                System.out.println("В ячейке пусто.");
            }else {
                for (Pallet pallet: o.getPallets()){
                    if (pallet.getPosition() == pos){
                        if(pallet.getMaterial().equals(refName)){
                            o.pickUpPallet(pos,refName);
                            isExist = false;
                        }
                    }
                }
            }
            if (isExist){
                System.out.println("Ячейка не содержит нужный вам материал.");
            } else {
                cells.replace(lblTableName + ":" + cellName,o);
                LoggerFiFo.getInstance().getRootLogger().info(String.format("User %s: PickUp pallet. %s %s %s",currentUser,lblTableName,cellName,refName));
                LogParser.getInstance().updateLog();
                isCorrect = true;
            }
        } catch (Exception e){
            cells = tmp;
        }

        return isCorrect;
    }
    public synchronized boolean forcedPickUp(String currentUser, String cellFulPath, String refName, String lblTableName){
        boolean isCorrect = false;
        ConcurrentMap tmp = base.getBase("Cells");
        int pos = Integer.parseInt(cellFulPath.substring(cellFulPath.indexOf("[") + 1, cellFulPath.indexOf("]")));
        String cellName = cellFulPath.substring(0, cellFulPath.indexOf("["));
        try {
            Cell o;
            if ((o = (Cell) cells.get(lblTableName + ":" + cellName)) == null) {
                System.out.println("В ячейке пусто.");
            } else {
                for (Pallet pallet : o.getPallets()) {
                    if (pallet.getPosition() == pos) {
                        o.pickUpPallet(pos, refName);
                    }
                }
                cells.replace(lblTableName + ":" + cellName, o);
                LoggerFiFo.getInstance().getRootLogger().info(String.format("User %s: PickUp pallet. %s %s %s", currentUser, lblTableName, cellName, refName));
                LogParser.getInstance().updateLog();
                isCorrect = true;
            }
        } catch (Exception e) {
            cells = tmp;
        }

        return isCorrect;
    }

    public synchronized boolean changeRack(String userName, Rack data, int action, String refList, String cellsList) {
        boolean isCorrect = false;
        ConcurrentMap tmp = base.getBase("Racks");
        try {
            switch (action){
                case 0:
                    racks.remove(data.getName());
                    for (Object r : references.keySet()) {
                        SAPReference o = (SAPReference) references.get(r);
                        for (String t : o.getAllowedRacks()) {
                            if (t.equals(data.getName())) {
                                o.removeAllowedRack(data.getName());
                            }
                        }
                        references.replace(o.getReference(), o);
                    }
                    break;
                case 1:
                    if (racks.containsKey(data.getName())) {
                        racks.replace(data.getName(), data);
                    } else {
                        racks.put(data.getName(), data);
                    }
                    base.fillNewRack(data);
                    changeLinkRackToRef(userName,data.getName(),refList);
                    if (!cellsList.equals("null")) {
                        for (String c: cellsList.split(",")){
                            String colName = c.substring(0,1);
                            String rowNum = c.substring(1,2);
                            Cell o = (Cell) cells.get(data.getName() + ":" + c);
                            o.setBlocked(true);
                            cells.replace(data.getName() + ":" + c,o);
                        }
                    } else {
                        for (Object o : cells.values()){
                            Cell t = (Cell) o;
                            if (t.getRack().equals(data.getName())){
                                if (t.isBlocked()){
                                    t.setBlocked(false);
                                    cells.replace(data.getName() + ":" + t.getCol() + t.getRow(),t);
                                }
                            }
                        }
                    }
                    break;
            }
            isCorrect = true;
        } catch (Exception e) {
            racks = tmp;
        }
        return isCorrect;
    }

    public boolean changeLinkRackToRef(String userName, String rackName, String referencesList) {
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

    public boolean changeReference(String userName, SAPReference data, int action) {
        boolean isCorrect = false;
        ConcurrentMap tmp = base.getBase("References");

        try {
            switch (action){
                case 0:
                    references.remove(data.getReference());
                    break;
                case 1:

                    if (references.containsKey(data.getReference())) {
                        references.replace(data.getReference(), data);
                    } else {
                        references.put(data.getReference(), data);
                    }
                    break;
            }
            isCorrect = true;
        } catch (Exception e) {
            references = tmp;
        }

        return isCorrect;
    }

    public boolean changeUser(String userName, User data, int action) {
        boolean isCorrect = false;
        ConcurrentMap tmp = base.getBase("Users");

        try {
            switch (action){
                case 0:
                    users.remove(data.getLogin());
                    break;
                case 1:
                    if (users.containsKey(data.getLogin())) {
                        users.replace(data.getLogin(), data);
                    } else {
                        users.put(data.getLogin(), data);
                    }
                    break;
            }
            isCorrect = true;
        } catch (Exception e) {
            users = tmp;
        }

        return isCorrect;
    }
}
