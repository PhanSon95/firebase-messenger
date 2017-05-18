package vn.com.zinza.zinzamessenger.model;

/**
 * Created by ASUS on 02/28/2017.
 */

public class BodyRequestFCM {
    private String to;
    private Notification notification;
    private Data data;

    public BodyRequestFCM(String to, Notification notification, Data data) {
        this.to = to;
        this.notification = notification;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
