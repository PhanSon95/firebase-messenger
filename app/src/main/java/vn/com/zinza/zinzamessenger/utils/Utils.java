package vn.com.zinza.zinzamessenger.utils;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Date;

import vn.com.zinza.zinzamessenger.R;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ASUS on 02/20/2017.
 */

public class Utils {
    public static int NOTIFICATION_ID = 0;

    public static final String FCM_KEY = "AAAAxJKOUg8:APA91bFCRClxYF_8Ro_sEvS2gMQmfXzA4eNcSFKFVNwM8TkCJOZTpVGTXecgrJFDKCRqAHDoWpJm1_c5r8Jxt2PmQ5coamI5_um8V0dXfOhNarGGZ20Mi9IqtMNYHof5FVsbKmLBSTnm";
    public static final String FCM_SEND_URL = "https://fcm.googleapis.com";
    public static final String GCM_SEND_URL = "https://android.googleapis.com";

    public static final String FIREBASE_DOWNLOAD = "https://firebasestorage.googleapis.com";
    public static String IMAGE_NAME = "";
    public static String FIREBASE_END_URL = "";
    public static String REJECT_ACTION = "REJECT";
    public static String ACCEPT_ACTION = "ACCEPT";
    public static final String INTERNET = "Turn on internet connection";
    public static final String SIGN_IN_FAIL = "Google Sign In failed.";
    public static final String FB_NAME = "FB_NAME";
    public static final String FB_URL = "FB_URL";
    //Table
    public static final String TBL_USERS = "users";
    public static final String TBL_FRIENDS = "tblFriend";
    public static final String TBL_CHATS = "tblChat";
    public static final String TBL_COLOR = "tblColor";
    public static final String TBL_WAIT = "tblWaitRespone";
    public static final String TBL_GROUP = "tblGroups";
    public static final String TBL_GROUP_CHAT = "tblGroupChat";
    //Folder
    public static final String FOLDER_BG = "background-chat";
    //Login
    public static boolean GOT_USER = false;
    //Current USER
    public static String USER_ID = "";
    public static String AVATAR_URL = "";
    public static String USER_NAME = "";
    public static String USER_PASS = "";
    public static String USER_DOB = "";
    public static String USER_EMAIL = "";
    public static String USER_CREATED = "";
    public static String USER_TOKEN = getToken();
    //Type
    public static String TYPE_ADD = "ADD";
    public static String TYPE_ANSWER = "ANSWER";
    public static String TYPE_SEND = "SEND";
    public static String TYPE_ADD_GROUP = "ADD_GROUP";
    //Type chat
    public static String TEXT = "TEXT";
    public static String IMAGE = "IMAGE";
    public static String FILE = "FILE";
    public static String VIDEO = "VIDEO";

    public static String NAME_FILE = "";
    public static String ROOT_FOLDER = Environment.getExternalStorageDirectory() + "/ZinZaMessenger";
    public static String INTRO_ACCEPT = "Hai bạn đã là bạn của nhau. Hãy bắt đầu trò chuyện";
    //Chating
    public static String TYPE_CHAT = "TYPE_CHAT";
    public static String PRIVATE = "PRIVATE";
    public static String GROUP = "GROUP";
    public static String FR_USER = "FR_USER";
    public static String FORMAT_TIME = "dd-MM-yyyy hh:mm:ss";
    public static String SENDER_ID = "SENDER_ID";
    public static String RECIPIENT_ID = "RECIPIENT_ID";
    public static String URL_STREAMING = "URL_STREAMING";
    public static String READ = "READ";
    public static String UNREAD = "UNREAD";
    //Group chat
    public static final String GROUP_FOLDER = "GROUP";
    public static final String GROUP_OBJECT = "GROUP_OBJECT";
    public static final String FOLDER_AVATAR = "FOLDER_AVATAR";
    // Color
    public static String COLOR = "FFFFFF";

    public static String URL_PATH_1 = "";
    public static String URL_PATH_2 = "";
    public static String URL_PATH_3 = "";
    public static String URL_PATH_4 = "";
    public static String URL_PATH_5 = "";

    public static String URL_PART_1 = "";
    public static String URL_PART_2 = "";
    public static String URL_PART_3 = "";
    public static String URL_PART_4 = "";
    public static String URL_PART_5 = "";

    public static String URL_DOWNLOAD = "";
    //Upload multi
    public static String URL_PART = "";
    public static String URL_CONTENT = "";
    //Connection
    public static int CONNECTION_STATUS;


    public static AlertDialog buildAlertDialog(String title, String message, boolean isCancelable, Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title);

        if (isCancelable) {
            builder.setPositiveButton(android.R.string.ok, null);
        } else {
            builder.setCancelable(false);
        }
        return builder.create();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static void showToast(String alert, Context mContext) {
        Toast.makeText(mContext, alert, Toast.LENGTH_SHORT).show();
    }

    public final static void dissmiss(ProgressDialog progressDialog) {
        progressDialog.dismiss();
    }

    public static boolean verifyConnection(Context context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        conectado = conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected();
        return conectado;
    }

    public static void cancelNotification(Context ctx) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public static String getToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    public static String createAt() {
        return new SimpleDateFormat(Utils.FORMAT_TIME).format(new Date());
    }

    public static String createNameFile() {
        Date date = new Date();
        String name = "ZinZa" + date.getYear() + date.getMonth() + date.getDay() + date.getTime();
        return name;

    }
}
