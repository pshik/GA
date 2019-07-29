package log;

import java.time.LocalDateTime;

public class Event {
    private LocalDateTime dateTime;
    private String message;
    private String level;

    public Event(LocalDateTime dateTime, String message, String level) {
        this.dateTime = dateTime;
        this.message = message;
        this.level = level;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getMessage() {
        return message;
    }

    public String getLevel() {
        return level;
    }
}
