package vn.com.zinza.zinzamessenger.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.webkit.MimeTypeMap;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ASUS on 03/06/2017.
 */

public class Helper {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public static void setUserOnline(DatabaseReference mReference) {
        mReference = FirebaseDatabase.getInstance().getReference();
        mReference.child(Utils.TBL_USERS).child(Utils.USER_ID).child("mStatus").setValue("on");//Set user online
    }

    public static void setUserOffline(DatabaseReference mReference) {
        mReference = FirebaseDatabase.getInstance().getReference();
        mReference.child(Utils.TBL_USERS).child(Utils.USER_ID).child("mStatus").setValue("off");//Set user offline
        mReference.child(Utils.TBL_USERS).child(Utils.USER_ID).child("mToken").setValue("");//Set user offline
    }

    public static String convertTime(String time) {
        SimpleDateFormat output = new SimpleDateFormat("hh:mm");
        SimpleDateFormat formatter = new SimpleDateFormat(Utils.FORMAT_TIME);
        try {
            Date parsed = formatter.parse(time);
            return output.format(parsed);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;

    }
    public static void setOffline(final DatabaseReference mReference){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                setUserOffline(mReference);
            }
        }, 5000);
    }
    public static String convertDay(String time) {
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat(Utils.FORMAT_TIME);
        try {
            Date parsed = formatter.parse(time);
            return output.format(parsed);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
    //Camera & galeery
    public static String getUrlDownload(String url) {
        return url.substring(url.lastIndexOf("apis.com") + 8);
    }
    //Video & File
    public static String getUrlStorageDownload(String url){
        return url.substring(0,url.lastIndexOf("---"));
    }

    public static String getUrlFileDownload(String url){
        String mURl = url.substring(url.lastIndexOf("apis.com")+8);
        return mURl;
    }
    public static String getNameFile(String url){
        String mURl = url.substring(url.indexOf("---")+3,url.indexOf("+++"));
        return mURl;
    }

    public static String getFullPart(String url){
        String fullPart= url.substring(url.lastIndexOf("+++")+3);
        return fullPart;

    }


    public static String getName(String url) {
        return url.substring(url.lastIndexOf("---") + 3,url.lastIndexOf("+++"));
    }

    public static void createDirectory() {
        File root = new File(Utils.ROOT_FOLDER);
        root.mkdir();
    }
    public static String checkType(String url) {
        if (url.contains(".mp4")) {
            return ".mp4";
        } else if (url.contains(".jpg")) {
            return ".jpg";
        } else if (url.contains(".mp3")) {
            return ".mp3";
        }
        else if (url.contains(".jpeg")) {
            return ".jpeg";
        }
        else if (url.contains(".apk")){
            return ".apk";
        }
        else if (url.contains(".txt")){
            return ".txt";
        }
        else if (url.contains(".doc")){
            return ".doc";
        }
        else if (url.contains(".bin")){
            return ".doc";
        }
        return null;
    }

    public static String getTypeFromUri(Context context, Uri uri) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(cR.getType(uri));
        return type;
    }

    public static boolean splitFile(String source, String dest, int nuberFile, String typeFile) throws FileNotFoundException, IOException {
        File sourceFile = new File(source);
        if (sourceFile.exists() && sourceFile.isFile()) {
            long sizeFile = sourceFile.length();
            long sizeSplitFile = (sizeFile / nuberFile);
            InputStream is = new FileInputStream(sourceFile);
            byte[] arr = new byte[1024];
            for (int i = 1; i <= nuberFile; i++) {
                int j = 0;
                long a = 0;
                OutputStream os = new FileOutputStream(dest+ i + "." + typeFile);
                System.out.println("file cắt được " + i + "." + typeFile);
                while ((j = is.read(arr)) != -1) {
                    os.write(arr, 0, j);
                    a += j;
                    if (a >= sizeSplitFile) {
                        break;
                    }
                }
                Log.e("Cut file", "Success");
                os.flush();
                os.close();
            }
            is.close();
            return true;
        } else {
            System.out.println("file không tồn tại");
            return false;
        }
    }
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public static Bitmap getBitmapFromUrl(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap mBitmap = BitmapFactory.decodeStream(inputStream);
            return mBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static ScaleAnimation generateAnimation(){
        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        scale.setDuration(300);
        scale.setInterpolator(new OvershootInterpolator());
        return scale;
    }
}
