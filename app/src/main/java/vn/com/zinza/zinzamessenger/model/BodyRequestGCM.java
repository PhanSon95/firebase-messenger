package vn.com.zinza.zinzamessenger.model;

import java.util.List;

/**
 * Created by ASUS on 4/13/2017.
 */

public class BodyRequestGCM {
    private List<String> registration_ids;
    private Notification notification;
    private Data data;

    public BodyRequestGCM(List<String> registration_ids, Notification notification, Data data) {
        this.registration_ids = registration_ids;
        this.notification = notification;
        this.data = data;
    }

    public List<String> getRegistration_ids() {
        return registration_ids;
    }

    public void setRegistration_ids(List<String> registration_ids) {
        this.registration_ids = registration_ids;
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
