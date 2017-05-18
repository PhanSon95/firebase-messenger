package vn.com.zinza.zinzamessenger.model;

/**
 * Created by dell on 27/02/2017.
 */

public class Friend {
    private String mId;
    private String mIdCurrentUser;
    private String mIdFriend;
    private String dateCreated;

    public Friend(String mId, String mIdCurrentUser, String mIdFriend, String dateCreated) {
        this.mId = mId;
        this.mIdCurrentUser = mIdCurrentUser;
        this.mIdFriend = mIdFriend;
        this.dateCreated = dateCreated;
    }

    public Friend() {
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmIdCurrentUser() {
        return mIdCurrentUser;
    }

    public void setmIdCurrentUser(String mIdCurrentUser) {
        this.mIdCurrentUser = mIdCurrentUser;
    }

    public String getmIdFriend() {
        return mIdFriend;
    }

    public void setmIdFriend(String mIdFriend) {
        this.mIdFriend = mIdFriend;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
