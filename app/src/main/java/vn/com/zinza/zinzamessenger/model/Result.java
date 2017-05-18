package vn.com.zinza.zinzamessenger.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ASUS on 4/14/2017.
 */

public class Result implements Serializable {
    @SerializedName("message_id")
    private String message_id;

    public Result(){

    }
    public Result(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
}
