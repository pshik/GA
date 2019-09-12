package server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import controller.ServerController;
import dao.Base;
import log.LoggerFiFo;
import model.Rack;
import model.SAPReference;
import model.User;
import org.apache.logging.log4j.Level;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server {
    private static Map<Connection, String> connectionMap = new ConcurrentHashMap<>();
    private static int port;
    private static Base base;
    private static ServerController serverController;
    private static int BLOCKED_DAYS = 0;
    public static String pathToBase;


    public static void main(String[] args) {
        Properties properties = new Properties();
        FileInputStream fileServerProperties ;

        try {
            fileServerProperties = new FileInputStream("src/main/resources/server.properties");
            properties.load(fileServerProperties);
            pathToBase = properties.getProperty("db.path");
            port = Integer.parseInt(properties.getProperty("server.port"));
            BLOCKED_DAYS = Integer.parseInt(properties.getProperty("blocked.days"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        base = Base.getInstance();
        if (base.getBase("Users").isEmpty()){
            base.initDefaultBase("Users");
        }
        if (base.getBase("References").isEmpty()){
            base.initDefaultBase("References");
        }
        if (base.getBase("Racks").isEmpty()){
            base.initDefaultBase("Racks");
        }
        //base.getBase("References").clear();
        serverController = ServerController.getServerController();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LoggerFiFo.getInstance().getRootLogger().log(Level.DEBUG,"Server is running!");
            System.out.println("Server is running!");
            while (true) {
                Socket socket = serverSocket.accept();
                Thread t = new Handler(socket);
                t.start();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private static class Handler extends Thread{
        private Socket socket;

        Handler(Socket socket){
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            //  while (true) {
            Message message;
            message = connection.receive();
            if (message.getType() == MessageType.USER_REQUEST) {
                ConcurrentMap users = base.getBase("Users");
                ArrayList<User> userList = new ArrayList<>();
                for (Object o : users.values()) {
                    User u = (User) o;
                    userList.add(u);
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectMapper mapper = new ObjectMapper();

                mapper.writeValue(out, userList);
                String data = out.toString();
                connection.send(new Message(MessageType.USERS_UPDATE, data));
                Message answer = connection.receive();
                if (answer.getType() == MessageType.ACCESS_GRANTED) {
                    LoggerFiFo.getInstance().getRootLogger().log(Level.forName("SECURITY", 350), "User " + answer.getData() + " login");
                    User u = (User) users.get(answer.getData());
                    serverController.addConnectedUser(u);
                    connectionMap.put(connection,u.getLogin());
                    return u.getLogin();
                } else if (answer.getType() == MessageType.ACCESS_DENIED) {
                    LoggerFiFo.getInstance().getRootLogger().log(Level.forName("SECURITY", 350), answer.getData());

                } else if (answer.getType() == MessageType.GOODBYE){
                    connection.close();
                }
            }
            return null;
        }

        @Override
        public void run() {
            System.out.println("Установлено соединение с удаленным адресом: " + socket.getRemoteSocketAddress());
            String userName = null;
            try (Connection connection = new Connection(socket)){

                userName = serverHandshake(connection);
                if (userName != null) {
                    //sendBroadcastMessage(new Message(MessageType.USER_ADDED,userName));
                    serverMainLoop(connection, userName);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Произошла ошибка при обмене данными с удаленным адресом. ClassNotFoundException");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Произошла ошибка при обмене данными с удаленным адресом. IOException");
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            boolean connectionActive = true;
            System.out.println("Успешный вход польхователя: " + userName + ". Используется соединение с удаленным адресом: " + socket.getRemoteSocketAddress());
            while (connectionActive){
                Message m = connection.receive();
                String data;
                int action;
                StringReader reader;
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                boolean isCorrect;
                switch (m.getType()){
                    case RACK_UPDATE:
                        ConcurrentMap racks = base.getBase("Racks");
                        data = messageData(racks);
                        connection.send(new Message(MessageType.RACK_UPDATE, data));
                        break;
                    case REFERENCE_REQUEST:
                        ConcurrentMap references = base.getBase("References");
                        data = messageData(references);
                        connection.send(new Message(MessageType.REFERENCE_REQUEST, data));
                        break;
                    case USER_REQUEST:
                        ConcurrentMap users = base.getBase("Users");
                        data = messageData(users);
                        connection.send(new Message(MessageType.USERS_UPDATE, data));
                        break;
                    case LOG_REQUEST:
                     //   @JsonSerialize(keyUsing = MyPairSerializer.class)
                        Map<LocalDateTime, String> infoLog = serverController.getInfoLog();
                   //     ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectMapper om = new ObjectMapper();

                        om.registerModule(new JavaTimeModule());
                        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                        String jsonResult = om.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(infoLog);
                //        om.writeValueAsString(infoLog);

                        connection.send(new Message(MessageType.LOG_REQUEST, jsonResult));
                        break;
                    case LOAD_PALLET:
                        data = m.getData();
                        isCorrect = serverController.loadPalletToRack(userName,data.split("-_-")[0],data.split("-_-")[1],data.split("-_-")[2],data.split("-_-")[3]);
                        if (isCorrect){
                            broadcastMessage(MessageType.RACK_UPDATE);
                        }
                        break;
                    case PICKUP_PALLET:
                        data = m.getData();
                        isCorrect = serverController.pickupPallet(userName,data.split("-_-")[0],data.split("-_-")[1],data.split("-_-")[2]);
                        if (isCorrect){
                            broadcastMessage(MessageType.RACK_UPDATE);
                        }
                        break;
                    case SETTINGS:
                        data = m.getData();
                        switch (data){
                            case "Blocked_Days":
                                connection.send(new Message(MessageType.SETTINGS,"BLOCKED_DAYS:=" + BLOCKED_DAYS));
                                break;
                        }
                        break;
                    case GOODBYE:
                        System.out.println("Польхователь: " + userName + " закончил работу. Используется соединение с удаленным адресом: " + socket.getRemoteSocketAddress());
                        connectionMap.remove(connection);
                        connection.close();
                        connectionActive = false;
                        break;
                    case FORCED_PICKUP:
                        data = m.getData();
                        isCorrect = serverController.forcedPickUp(userName,data.split("-_-")[0],data.split("-_-")[1],data.split("-_-")[2]);
                        if (isCorrect){
                            broadcastMessage(MessageType.RACK_UPDATE);
                        }
                        break;
                    case CHANGE_RACK:
                        data = m.getData();
                        action = Integer.parseInt(data.substring(0,1));
                        String refList = data.split("-_-")[1];
                        reader = new StringReader(data.substring(1).split("-_-")[0]);
                        Rack r = mapper.readValue(reader,Rack.class);
                        isCorrect = serverController.changeRack(userName,r,action,refList);
                        if (isCorrect){
                            broadcastMessage(MessageType.RACK_UPDATE);
                        }
                        break;
                    case IMPORT_EXPORT:
                        data = m.getData();
                        action = Integer.parseInt(data.substring(0,1));
                        reader = new StringReader(data.substring(1));
                        Object[] list = mapper.readValue(reader,Object[].class);
                        isCorrect = serverController.importExport(userName,action,list);
                        if (isCorrect){
                            broadcastMessage(MessageType.REFERENCE_UPDATE);
                        }
                        break;
                    case CHANGE_REFERENCE:
                        data = m.getData();
                        action = Integer.parseInt(data.substring(0,1));
                        reader = new StringReader(data.substring(1));
                        SAPReference s = mapper.readValue(reader, SAPReference.class);
                        isCorrect = serverController.changeReference(userName,s,action);
                        if (isCorrect){
                            broadcastMessage(MessageType.REFERENCE_UPDATE);
                        }
                        break;
                    case EVENT:
                        data = m.getData();
                        String level = data.split("-_-")[0];
                        String message = data.split("-_-")[1];
                        switch (level){
                            case "SECURITY":
                                LoggerFiFo.getInstance().getRootLogger().log(Level.forName("SECURITY", 350), message);
                                break;
                            case "DEBUG":
                                LoggerFiFo.getInstance().getRootLogger().log(Level.DEBUG, message);
                                break;
                            case "INFO":
                                LoggerFiFo.getInstance().getRootLogger().log(Level.INFO, message);
                                break;
                            case "WARN":
                                LoggerFiFo.getInstance().getRootLogger().log(Level.WARN, message);
                                break;
                            case "ERROR":
                                LoggerFiFo.getInstance().getRootLogger().log(Level.ERROR, message);
                                break;
                        }

                        break;
                    case CHANGE_USER:
                        data = m.getData();
                        action = Integer.parseInt(data.substring(0,1));
                        reader = new StringReader(data.substring(1));
                        User u = mapper.readValue(reader, User.class);
                        isCorrect = serverController.changeUser(userName,u,action);
                        if (isCorrect){
                            broadcastMessage(MessageType.USERS_UPDATE);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        private String messageData(ConcurrentMap map) throws IOException {
            ArrayList<Object> list = new ArrayList<>();
            for (Object o : map.values()) {
                list.add(o);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            mapper.writeValue(out, list);
            return out.toString();
        }

        private void broadcastMessage(MessageType type) throws IOException {
            for (Connection connection : connectionMap.keySet()){
                connection.send(new Message(type));
            }
        }
    }
}
