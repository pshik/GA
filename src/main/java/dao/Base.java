package dao;


import model.Cell;
import model.Rack;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import server.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;


public class Base {
    private static String DB_PATH;
    private DB db;
    private HashMap<String, ConcurrentMap> baseMap = new HashMap<>();
    private static Base ourInstance = new Base();
    private final String[] colNames = new String[]{"A","B","C","D","E","F","G","H","I"};
    private final String[] rowNames = new String[]{"1","2","3","4","5","6","7","8","9"};

    public static Base getInstance() {
        return ourInstance;
    }

    private Base() {
        DB_PATH = Server.pathToBase;
        db = DBMaker.fileDB(DB_PATH).checksumHeaderBypass().closeOnJvmShutdown().make();
        baseMap.put("Users",db.hashMap("UsersMap").createOrOpen());
        baseMap.put("References",db.hashMap("ReferencesMap").createOrOpen());
        baseMap.put("Racks",db.hashMap("RacksMap").createOrOpen());
        baseMap.put("Cells",db.hashMap("CellsMap").createOrOpen());
    }

    public ConcurrentMap getBase(String mapName){
        return baseMap.get(mapName);
    }

    public void closeDB(){
        db.close();
    }

    public void printAllEntity(String mapName){
        ConcurrentMap tmp = getBase(mapName);
        for (Object o: tmp.keySet()){
            System.out.println(tmp.get(o).toString());
        }
    }
    public void fillNewRack(Rack rack){
            int col = rack.getCol();
            int row = rack.getRow();

            for (int i = 0; i < row ; i++){
                for (int j = 0; j < col; j++){
                    getBase("Cells").put(rack.getName() + ":" + colNames[j]+rowNames[row-i-1],new Cell(rack.getName(),colNames[j],rowNames[row-i-1],null));
                }
            }
    }
    public ArrayList<Object> getDataList (String mapName){
        ArrayList<Object> objects = new ArrayList<>();
        ConcurrentMap tmp = getBase(mapName);
        for (Object o: tmp.keySet()){
            objects.add(tmp.get(o));
        }
        return objects;
    }
}