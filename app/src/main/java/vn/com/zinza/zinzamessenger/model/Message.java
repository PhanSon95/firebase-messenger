package vn.com.zinza.zinzamessenger.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by ASUS on 02/13/2017.
 */

public class Message implements Serializable{
    private String mId;
    private String mIdSender;
    private String mIdRecipient;
    private String mType;
    private String mContent;
    private String mTime;
    private int mRecipientOrSenderStatus;
    public Message(){

    }

    public Message(String mId, String mIdSender, String mIdRecipient, String mType, String mContent, String mTime) {
        this.mId = mId;
        this.mIdSender = mIdSender;
        this.mIdRecipient = mIdRecipient;
        this.mType = mType;
        this.mContent = mContent;
        this.mTime = mTime;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmIdSender() {
        return mIdSender;
    }

    public void setmIdSender(String mIdSender) {
        this.mIdSender = mIdSender;
    }

    public String getmIdRecipient() {
        return mIdRecipient;
    }

    public void setmIdRecipient(String mIdRecipient) {
        this.mIdRecipient = mIdRecipient;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    @Exclude
    public int getRecipientOrSenderStatus() {
        return mRecipientOrSenderStatus;
    }
    public void setRecipientOrSenderStatus(int recipientOrSenderStatus) {
        this.mRecipientOrSenderStatus = recipientOrSenderStatus;
    }
}
