package vn.com.zinza.zinzamessenger.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ASUS on 02/28/2017.
 */

public class ResultRequestFCM implements Serializable {
    @SerializedName("multicast_id")
    private String multicast_id;
    @SerializedName("message_id")
    private String message_id;
    @SerializedName("success")
    private String failure;
    @SerializedName("failure")
    private String success;
    @SerializedName("canonical_ids")
    private String canonical_ids;


    public ResultRequestFCM(String multicast_id, String message_id, String failure, String success, String canonical_ids) {
        this.multicast_id = multicast_id;
        this.message_id = message_id;
        this.failure = failure;
        this.success = success;
        this.canonical_ids = canonical_ids;
    }

    public String getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(String multicast_id) {
        this.multicast_id = multicast_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getFailure() {
        return failure;
    }

    public void setFailure(String failure) {
        this.failure = failure;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(String canonical_ids) {
        this.canonical_ids = canonical_ids;
    }
}
