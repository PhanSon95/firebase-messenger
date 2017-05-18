package vn.com.zinza.zinzamessenger.model;

import android.content.Intent;

/**
 * Created by ASUS on 3/28/2017.
 */

public class ResultData {
    private Intent mData;
    private String mKey;
    private String mType;
    private int mStatus;

    public ResultData(Intent mData, String mKey, String mType, int mStatus) {
        this.mData = mData;
        this.mKey = mKey;
        this.mType = mType;
        this.mStatus = mStatus;
    }

    public Intent getmData() {
        return mData;
    }

    public void setmData(Intent mData) {
        this.mData = mData;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }
}
