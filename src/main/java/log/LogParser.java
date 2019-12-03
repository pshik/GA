package log;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LogParser{
    private static LogParser ourInstance = new LogParser();
    private ArrayList<Event> events = new ArrayList<>();
    private String event;

    public static LogParser getInstance() {
        return ourInstance;
    }

    private LogParser() {
        loadData();
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void updateLog(){
        events.clear();
        loadData();
    }

    private void loadData(){
        try {
            FileReader fileReader = new FileReader("logs/output.log");
            BufferedReader reader = new BufferedReader(fileReader);
            while (reader.ready()){
                String line = reader.readLine();
                String[] s = line.split(" ");
                String[] split = line.split(" - ");
                LocalDateTime tmpDate = LocalDateTime.parse(s[0]+ " " + s[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS"));

                Event tmp = new Event(tmpDate,split[1], s[2]);
                events.add(0,tmp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public static void main(String[] args) {
//        System.out.println(System.getProperty("user.dir"));
//    }
    public String getEvent() {
        return event;
    }

    public void setEvent(final String event) {
        this.event = event;
    }
}
