package vn.com.zinza.zinzamessenger.model;

import java.io.Serializable;

/**
 * Created by ASUS on 02/10/2017.
 */

    public class User implements Serializable{
    private String mId;
    private String mUsername;
    private String mEmail;
    private String mPassword;
    private String mAvatar;
    private String mDateOfBirth;
    private String mStatus;
    private String mToken;
    private String mCreatedAt;
    private boolean mIsFriend;

    public User(String mId, String mUsername, String mEmail, String mPassword, String mAvatar, String mDateOfBirth, String mStatus, String mToken, String mCreatedAt) {
        this.mId = mId;
        this.mUsername = mUsername;
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mAvatar = mAvatar;
        this.mDateOfBirth = mDateOfBirth;
        this.mStatus = mStatus;
        this.mToken = mToken;
        this.mCreatedAt = mCreatedAt;
    }

    public User() {

    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmAvatar() {
        return mAvatar;
    }

    public void setmAvatar(String mAvatar) {
        this.mAvatar = mAvatar;
    }

    public String getmDateOfBirth() {
        return mDateOfBirth;
    }

    public void setmDateOfBirth(String mDateOfBirth) {
        this.mDateOfBirth = mDateOfBirth;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmToken() {
        return mToken;
    }

    public void setmToken(String mToken) {
        this.mToken = mToken;
    }

    public String getmCreatedAt() {
        return mCreatedAt;
    }

    public void setmCreatedAt(String mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    public boolean getmIsFriend() {
        return mIsFriend;
    }

    public void setmIsFriend(boolean mIsFriend) {
        this.mIsFriend = mIsFriend;
    }
}
