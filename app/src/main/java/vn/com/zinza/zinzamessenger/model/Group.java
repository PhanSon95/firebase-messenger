package vn.com.zinza.zinzamessenger.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ASUS on 4/13/2017.
 */

public class Group implements Serializable {
    private String mId;
    private String mName;
    private String mAvatar;
    private List<String> members;
    private String mDescribe;
    private String mCreated;
    public Group(){

    }

    public Group(String mId, String mName, String mAvatar, List<String> members, String mDescribe, String mCreated) {
        this.mId = mId;
        this.mName = mName;
        this.mAvatar = mAvatar;
        this.members = members;
        this.mDescribe = mDescribe;
        this.mCreated = mCreated;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmAvatar() {
        return mAvatar;
    }

    public void setmAvatar(String mAvatar) {
        this.mAvatar = mAvatar;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getmDescribe() {
        return mDescribe;
    }

    public void setmDescribe(String mDescribe) {
        this.mDescribe = mDescribe;
    }

    public String getmCreated() {
        return mCreated;
    }

    public void setmCreated(String mCreated) {
        this.mCreated = mCreated;
    }
}
