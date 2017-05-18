package vn.com.zinza.zinzamessenger.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vn.com.zinza.zinzamessenger.model.FileHistory;


public class FileUtils {

    public FileUtils() {
    }
    public ArrayList getListFile(){
        ArrayList<FileHistory> fileList = new ArrayList<FileHistory>();
        String root = Utils.ROOT_FOLDER;
        Log.d("Files", "Path: " + root);
        File directory = new File(root);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
            FileHistory fileInfo = new FileHistory();
            String path = files[i].getPath();
            fileInfo.setPathFileInStorage(path);
            fileInfo.setName(files[i].getName());
            fileInfo.setSize(files[i].length()+"");
            fileInfo.setDate(Helper.convertDay(new SimpleDateFormat(Utils.FORMAT_TIME).format(new Date(files[i].lastModified()))));
            fileList.add(fileInfo);
        }
        return fileList;
    }
}
