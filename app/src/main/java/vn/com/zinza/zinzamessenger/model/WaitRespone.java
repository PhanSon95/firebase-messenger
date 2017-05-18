package vn.com.zinza.zinzamessenger.model;

import java.io.Serializable;

/**
 * Created by ASUS on 4/5/2017.
 */

public class WaitRespone implements Serializable {
    private String mId;
    private String mToken;
    private String mName;
    private String mUrl;

    public WaitRespone() {
    }

    public WaitRespone(String mId, String mToken, String mName, String mUrl) {
        this.mId = mId;
        this.mToken = mToken;
        this.mName = mName;
        this.mUrl = mUrl;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmToken() {
        return mToken;
    }

    public void setmToken(String mToken) {
        this.mToken = mToken;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
