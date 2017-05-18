package vn.com.zinza.zinzamessenger.model;

import java.util.ArrayList;

/**
 * Created by dell on 17/02/2017.
 */

public class FileHistory{
    private int mImg;
    private String mName,mDate;
    private String mSize;
    private String pathFileInStorage;

    public FileHistory(int mImg, String mName, String mDate) {
        this.mImg = mImg;
        this.mName = mName;
        this.mDate = mDate;
    }

    public FileHistory() {
    }

    public String getSize() {
        return mSize;
    }

    public void setSize(String mSize) {
        this.mSize = mSize;
    }

    public int getImg() {
        return mImg;
    }

    public void setImg(int mImg) {
        this.mImg = mImg;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getPathFileInStorage() {
        return pathFileInStorage;
    }

    public void setPathFileInStorage(String pathFileInStorage) {
        this.pathFileInStorage = pathFileInStorage;
    }
}
