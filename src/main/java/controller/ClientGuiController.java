package controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import exceptions.CloseWindow;
import model.*;
import server.Connection;
import server.Message;
import server.MessageType;
import view.ClientGUI;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.util.*;


public class ClientGuiController {
    public int BLOCKED_DAYS = 0;
    public String MESSAGE_DELIMITER = "-_-";
    private static int serverPort;
    private static String serverAddress;

    private static TreeMap<Integer,String> listOfManagersCommands = new TreeMap<>();

    private Connection connection;
    private volatile boolean clientConnected = false;
    private String currentUser;
    private ClientGuiModel model = new ClientGuiModel();
    private ClientGUI view = new ClientGUI(this);
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Rack> racks = new ArrayList<>();
    private ArrayList<SAPReference> references = new ArrayList<>();
    private ArrayList<Cell> cells = new ArrayList<>();
    private boolean access = false;
    private boolean isBusy = false;
    static {
      //  listOfManagersCommands.put(1,"Создать стеллаж");
       // listOfManagersCommands.put(2,"Удалить стеллаж");
        listOfManagersCommands.put(3,"Управление материалами");
        listOfManagersCommands.put(4,"Управление стеллажами");
        listOfManagersCommands.put(5,"Управление пользователями");
       // listOfManagersCommands.put(6,"Удалить пользователя");
      //  listOfManagersCommands.put(7,"Выгрузить в ...");
        listOfManagersCommands.put(8,"Загрузить из ...");
      //  listOfManagersCommands.put(9,"Привязать материалы к стеллажу");
    }

    public String getCurrentUser() {
        return currentUser;
    }
    public Connection getConnection() {
        return connection;
    }
    public TreeMap<Integer, String> getListOfManagersCommands() {
        return listOfManagersCommands;
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


    public void loadPallet(String cellFulPath, String refName, String lblTableName, Object b) {
        sendMessage(MessageType.LOAD_PALLET,cellFulPath + "-_-" + refName + "-_-" + lblTableName + "-_-" + b);
    }

    public void reload() {
        ClientGuiController.this.run();
    }

    public void setCurrentUser(String activeUser) {
        currentUser = activeUser;
    }

    public boolean getCellStatus(String cell) {
        for (Cell c: getModel().getCells()){

            if (c.getCol().equals(cell.substring(0,1)) && c.getRow().equals(cell.substring(1,2))){
                return c.isBlocked();
            }
        }
        return false;
    }



    class GuiSocketThread extends Thread{


        public void run(){

            try {
                boolean isConnected = false;

                Socket socket = new Socket(serverAddress, serverPort);
                connection = new Connection(socket);
                clientHandshake();
                clientStartWorkplace();
                clientLoop();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Server not available!");
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
                            case CELL_UPDATE:
                                connection.send(new Message(MessageType.CELL_UPDATE));
                                message = connection.receive();
                                if (message.getType() == MessageType.CELL_UPDATE) {
                                    getBase(message);
                                    model.updateCells(cells);
                                }
                                if (!isBusy) {
                                    view.refreshRack();
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

        private void getBase(Message message) throws IOException {
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
                case RACK_UPDATE:
                    Rack[] r = mapper.readValue(reader, Rack[].class);
                    if(!racks.isEmpty())
                        racks.clear();
                    racks.addAll(Arrays.asList(r));
                    break;
                case CELL_UPDATE:
                    Cell[] c = mapper.readValue(reader, Cell[].class);
                    if(!cells.isEmpty())
                        cells.clear();
                    cells.addAll(Arrays.asList(c));
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
            connection.send(new Message(MessageType.CELL_UPDATE));
            message = connection.receive();
            if (message.getType() == MessageType.CELL_UPDATE){
                getBase(message);
                model.updateCells(cells);
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
            view.mainView();
        }
    }


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

    public static void main (String[] args){
        Properties properties = new Properties();
        FileInputStream fileServerProperties ;

        try {
            fileServerProperties = new FileInputStream("src/main/resources/client.properties");
            properties.load(fileServerProperties);
            serverAddress = properties.getProperty("server.ip");
            serverPort = Integer.parseInt(properties.getProperty("server.port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClientGuiController controller = new ClientGuiController();
        controller.run();
    }
}
