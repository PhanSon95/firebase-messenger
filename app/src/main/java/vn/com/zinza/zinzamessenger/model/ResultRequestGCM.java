package vn.com.zinza.zinzamessenger.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ASUS on 4/14/2017.
 */

public class ResultRequestGCM implements Serializable {
    @SerializedName("multicast_id")
    private String multicast_id;
    @SerializedName("results")
    private Result []results;
    @SerializedName("success")
    private String failure;
    @SerializedName("failure")
    private String success;
    @SerializedName("canonical_ids")
    private String canonical_ids;

    public ResultRequestGCM(){

    }
    public ResultRequestGCM(String multicast_id, Result[] results, String failure, String success, String canonical_ids) {
        this.multicast_id = multicast_id;
        this.results = results;
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

    public Result[] getResults() {
        return results;
    }

    public void setResults(Result[] results) {
        this.results = results;
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

