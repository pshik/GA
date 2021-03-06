package controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import exceptions.CloseWindow;
import model.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import server.Connection;
import server.Message;
import server.MessageType;
import view.ClientGUI;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;


public class ClientGuiController {
    public int BLOCKED_DAYS = 0;
    private final String MESSAGE_DELIMITER = "-_-";
    private static int logDays = 3;
    private static int serverPort;
    private static String serverAddress;
    private static Properties properties = new Properties();
    private static String pathToProperties = "";
    public int getBLOCKED_DAYS() {
        return BLOCKED_DAYS;
    }

    public void setBLOCKED_DAYS(int BLOCKED_DAYS) {
        this.BLOCKED_DAYS = BLOCKED_DAYS;
    }

    public  int getServerPort() {
        return serverPort;
    }

    public  void setServerPort(int serverPort) {
        ClientGuiController.serverPort = serverPort;
    }

    public  String getServerAddress() {
        return serverAddress;
    }

    public  void setServerAddress(String serverAddress) {
        ClientGuiController.serverAddress = serverAddress;
    }


    private Connection connection;
    private volatile boolean clientConnected = false;
    private String currentUser;
    private ClientGuiModel model = new ClientGuiModel();
    private ClientGUI view = new ClientGUI(this);
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Rack> racks = new ArrayList<>();
    private ArrayList<SAPReference> references = new ArrayList<>();
    private TreeMap<LocalDateTime,String> log = new TreeMap<>(Collections.reverseOrder());
    private boolean access = false;
    private boolean isBusy = false;
    public String getMESSAGE_DELIMITER() {
        return MESSAGE_DELIMITER;
    }
    public Map<String,String> events = new HashMap<String, String>() {{
        put("SECURITY", "SECURITY");
        put("DEBUG", "DEBUG");
        put("INFO", "INFO");
        put("WARN", "WARN");
        put("ERROR", "ERROR");
    }};


    public String getCurrentUser() {
        return currentUser;
    }
    public Connection getConnection() {
        return connection;
    }


    public int getLogDays() {
        return logDays;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public void setAccess(boolean b) {
        access = b;
    }
    public TreeMap<LocalDateTime,String> getLog (){
        return log;
    }
    public boolean isAccess() {
        return access;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        System.exit(0);
    }

    public void reload() {
        ClientGuiController.this.run();
    }

    public void setCurrentUser(String activeUser) {
        currentUser = activeUser;
    }

    public boolean getCellStatus(String rackName, String cell) {
        for (Rack r: racks){
            if (r.getName().equals(rackName)){
                Cell c = r.getCellByName(cell.substring(0,cell.indexOf("[")));
                return c.isBlocked();
            }
        }
        return false;
    }

    public void updateProperties(String key, String value) {

        FileOutputStream fileServerProperties ;
        properties.setProperty(key,value);
        try {
            fileServerProperties = new FileOutputStream(pathToProperties);
            properties.store(fileServerProperties,"Updated by " + currentUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class GuiSocketThread extends Thread{


        public void run(){
            try {
                Socket socket = new Socket(serverAddress, serverPort);
                connection = new Connection(socket);
                clientHandshake();
                clientStartWorkplace();
                clientLoop();
            } catch (IOException | ClassNotFoundException e) {
             //   e.printStackTrace();
            //   System.out.println("Server not available!");
                reloadProperties();
                view.serverStatus();
                reload();
            }  catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void clientLoop() throws IOException, ClassNotFoundException {

            Message message;
            while (true) {
                        message = connection.receive();
                        switch (message.getType()) {
                            case REFERENCE_UPDATE:
                                connection.send(new Message(MessageType.REFERENCE_REQUEST));
                                message = connection.receive();
                                if (message.getType() == MessageType.REFERENCE_REQUEST){
                                    getBase(message);
                                    model.updateReferences(references);
                                }
                                if (!isBusy) {
                                    view.refreshRack();
                                }
                                break;
                            case RACK_UPDATE:
                                connection.send(new Message((MessageType.RACK_UPDATE)));
                                message = connection.receive();
                                if (message.getType() == MessageType.RACK_UPDATE){
                                    getBase(message);
                                    model.updateRack(racks);
                                }
                                if (!isBusy) {
                                    view.refreshRackList();
                                }
                                connection.send(new Message(MessageType.REFERENCE_REQUEST));
                                message = connection.receive();
                                if (message.getType() == MessageType.REFERENCE_REQUEST){
                                    getBase(message);
                                    model.updateReferences(references);
                                }
                                if (!isBusy) {
                                    view.refreshRack();
                                }
                                break;
                            case PALLET_UPDATE:
                                connection.send(new Message((MessageType.PALLET_UPDATE)));
                                message = connection.receive();
                                if (message.getType() == MessageType.PALLET_UPDATE){
                                    getBase(message);
                                    model.updateRack(racks);
                                }
                                if (!isBusy) {
                                    view.refreshRack();
                                    connection.send(new Message(MessageType.LOG_REQUEST));
                                }
                                break;
                            case USERS_UPDATE:
                                connection.send(new Message(MessageType.USER_REQUEST));
                                message = connection.receive();
                                if (message.getType() == MessageType.USERS_UPDATE){
                                    getBase(message);
                                    model.updateUsers(users);
                                }
                                break;
                            case SERVER_IS_STOPPED:
                                break;
                            case LOG_UPDATED:
                                connection.send(new Message(MessageType.LOG_REQUEST));
                                message = connection.receive();
                                if (message.getType() == MessageType.LOG_UPDATED){
                                    updateLog(message);
                                }
                              //  view.refreshLog();
                              //  view.refreshRack();
                                break;
                        }
                }
        }

        private void clientHandshake() throws IOException, ClassNotFoundException {
            Message message;
            connection.send(new Message(MessageType.USER_REQUEST));
            message = connection.receive();
            if ((message.getType() == MessageType.USERS_UPDATE) && users.isEmpty()) {
                try {
                    getBase(message);

                } catch (Exception e) {
                    System.out.println("Wrong Object from server to convert to ConcurrentMap");
                }
            }
                model.updateUsers(users);
                view.refreshUsers();
                boolean access_granted = false;
                while (!access_granted){
                    try {
                        access_granted = view.loginView();
                        if (!access_granted)
                            continue;
                        String name = view.getActiveUser();
                        currentUser = name;
                        connection.send(new Message(MessageType.ACCESS_GRANTED, name));
                    } catch (CloseWindow closeWindow) {
                        System.exit(0);
                    }
                }
        }

        private void getBase(@NotNull Message message) throws IOException {
            String data = message.getData();
            StringReader reader = new StringReader(data);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            switch (message.getType()){
                case REFERENCE_REQUEST:
                    SAPReference[] s = mapper.readValue(reader, SAPReference[].class);
                    if(!references.isEmpty())
                        references.clear();
                    references.addAll(Arrays.asList(s));
                    break;
                case PALLET_UPDATE:
                case RACK_UPDATE:
                    Rack[] r = mapper.readValue(reader, Rack[].class);
                    if(!racks.isEmpty())
                        racks.clear();
                    racks.addAll(Arrays.asList(r));
                    break;
                case USERS_UPDATE:
                    User[] u = mapper.readValue(reader, User[].class);
                    if(!users.isEmpty())
                        users.clear();
                    users.addAll(Arrays.asList(u));
                    break;
                case SERVER_IS_STOPPED:
                    break;
            }
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected){
            synchronized (ClientGuiController.this){
                ClientGuiController.this.clientConnected = clientConnected;
                ClientGuiController.this.notify();
            }
        }
        void clientStartWorkplace() throws IOException, ClassNotFoundException{
            Message message;
            connection.send(new Message(MessageType.RACK_UPDATE));
            message = connection.receive();
            if (message.getType() == MessageType.RACK_UPDATE){
                getBase(message);
                model.updateRack(racks);
            }
            connection.send(new Message(MessageType.REFERENCE_REQUEST));
            message = connection.receive();
            if (message.getType() == MessageType.REFERENCE_REQUEST){
                getBase(message);
                model.updateReferences(references);
            }
            connection.send(new Message(MessageType.SETTINGS,"Blocked_Days"));
            message = connection.receive();
            if (message.getType() == MessageType.SETTINGS){
               if(message.getData().startsWith("BLOCKED_DAYS")){
                    BLOCKED_DAYS = Integer.parseInt(message.getData().split(":=")[1]);
               }
            }
            connection.send(new Message(MessageType.LOG_REQUEST));
            message = connection.receive();
            if (message.getType() == MessageType.LOG_UPDATED){
                updateLog(message);
            }
            view.mainView();
        }
    }

    private void reloadProperties() {
        FileInputStream fileServerProperties ;
        try {
            fileServerProperties = new FileInputStream(pathToProperties);
            properties.load(fileServerProperties);
            serverAddress = properties.getProperty("server.ip");
            serverPort = Integer.parseInt(properties.getProperty("server.port"));
            logDays = Integer.parseInt(properties.getProperty("days.for.log"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @NotNull
    @Contract(" -> new")
    private Thread getSocketThread() {
        return new GuiSocketThread();
    }


    private void run() {
        getSocketThread().run();
    }

    public ClientGuiModel getModel(){
        return model;
    }

    public ClientGUI getView() {
        return view;
    }

    public void sendMessage(MessageType type, String message) {
        try {
            connection.send(new Message(type, message));
        } catch (IOException e) {
            clientConnected = false;
        }
    }

    private void updateLog(@NotNull Message message) {
        String data = message.getData();
        StringReader reader = new StringReader(data);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        TreeMap<LocalDateTime,String> s = new TreeMap<>(Collections.reverseOrder());
        try {
            TypeReference<TreeMap<LocalDateTime, String>> typeRef
                    = new TypeReference<TreeMap<LocalDateTime, String>>() {};
            s = mapper.readValue(reader, typeRef);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.clear();
        log = s;
      //  view.refreshLog();
    }

    public static void main (String[] args){

        FileInputStream fileServerProperties ;
        pathToProperties = args[0];
        try {
            fileServerProperties = new FileInputStream(pathToProperties);
            properties.load(fileServerProperties);
            serverAddress = properties.getProperty("server.ip");
            serverPort = Integer.parseInt(properties.getProperty("server.port"));
            logDays = Integer.parseInt(properties.getProperty("days.for.log"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClientGuiController controller = new ClientGuiController();
        controller.run();
    }
}
