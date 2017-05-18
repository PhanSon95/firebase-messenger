package vn.com.zinza.zinzamessenger.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.MainActivity;
import vn.com.zinza.zinzamessenger.activity.ChattingActivity;
import vn.com.zinza.zinzamessenger.activity.MessageFriendActivity;
import vn.com.zinza.zinzamessenger.activity.WaitResponeActivity;
import vn.com.zinza.zinzamessenger.model.Friend;
import vn.com.zinza.zinzamessenger.model.WaitRespone;
import vn.com.zinza.zinzamessenger.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ASUS on 02/28/2017.
 */

public class FireMsgService extends FirebaseMessagingService {
    private String senDerID;
    private String senderToken;
    public String senderName;
    public static String SENDER_ID = "SENDER_ID";
    public static String SENDER_TOKEN = "SENDER_TOKEN";
    public static String SENDER_NAME = "SENDER_NAME";
    private String type;
    private DatabaseReference mReference;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("Msg", "Message received [" + remoteMessage + "]");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        senDerID = remoteMessage.getData().get("id");
        senderToken = remoteMessage.getData().get("token");
        type = remoteMessage.getData().get("type");
        String urlAvatar = remoteMessage.getData().get("avatarURL");

        if (type.equals(Utils.TYPE_ADD)) {
            showNotification(title, body, urlAvatar);
            addWaitRespone(Utils.USER_ID, senDerID, body, senderToken, urlAvatar);
        } else if (type.equals(Utils.TYPE_ANSWER)) {
//            showNotification(title, body, urlAvatar);
            showAnswerNotification(title, urlAvatar, body, Utils.TYPE_ANSWER);
        } else if (type.equals(Utils.TYPE_ADD_GROUP)) {
            showAnswerNotification(title, urlAvatar, body, Utils.TYPE_ADD_GROUP);
        }
    }

    private void addWaitRespone(String idCurrentUser, String idFriend, String name, String token, String url) {
        final String tblContact = idCurrentUser + "-" + idFriend;
        WaitRespone mWaitRespone = new WaitRespone(tblContact, token, name, url);

        mReference = FirebaseDatabase.getInstance().getReference(Utils.TBL_WAIT);
        mReference.child(tblContact).setValue(mWaitRespone);
    }

    private void showAnswerNotification(String title, String url, String message, String typeNoti) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_answer_friend);
        Intent t = new Intent(this, MessageFriendActivity.class);
        Utils.cancelNotification(this);

        PendingIntent pdIntent = PendingIntent.getActivity(this, 0, t, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mbBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.zinza_icon)
                .setContentTitle(title)
                .setAutoCancel(false)
                .setContentIntent(pdIntent)
                .setContentText("Thông báo");

        android.app.Notification notification = mbBuilder.build();
        remoteViews.setImageViewBitmap(R.id.avatarAnswer, getBitmapFromUrl(url));
        if (typeNoti.equals(Utils.TYPE_ANSWER)) {
            remoteViews.setTextViewText(R.id.txtnameAnswer, message);
        } else if (typeNoti.equals(Utils.TYPE_ADD_GROUP)) {
            remoteViews.setTextViewText(R.id.txtnameAnswer, message + " đã mời bạn tham gia nhóm!");
        }
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification.bigContentView = remoteViews;
        }
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mbBuilder.setSound(alarmSound);
        mbBuilder.setVibrate(new long[]{500, 500, 500, 500, 500});

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(Utils.NOTIFICATION_ID, notification);
    }

    private void showNotification(String title, String message, String url) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification_add_friend);
        Intent t = new Intent(this, WaitResponeActivity.class);

        PendingIntent pdIntent = PendingIntent.getActivity(this, 0, t, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mbBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.zinza_icon)
                .setContentTitle(title)
                .setAutoCancel(false)
                .setContentIntent(pdIntent)
                .setContentText("Lời mời kết bạn :)");

        android.app.Notification notification = mbBuilder.build();
        remoteViews.setImageViewBitmap(R.id.imgAvatarNoti, getBitmapFromUrl(url));
        remoteViews.setTextViewText(R.id.txtMessageNoti, message + " muốn gửi lời mời kết bạn đến bạn.Bạn có đồng ý không ?");

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification.bigContentView = remoteViews;
        }
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mbBuilder.setSound(alarmSound);
        mbBuilder.setVibrate(new long[]{500, 500, 500, 500, 500});

        Intent rejectIntent = new Intent(this, SwitchButtonListener.class);
        rejectIntent.setAction(Utils.REJECT_ACTION);
        rejectIntent.putExtra(SENDER_ID, senDerID);
        rejectIntent.putExtra(SENDER_TOKEN, senderToken);
        rejectIntent.putExtra(SENDER_NAME, senderName);
        rejectIntent.setAction(Utils.REJECT_ACTION);

        PendingIntent pendingRejectClick = PendingIntent.getBroadcast(this, 0, rejectIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.btnReject, pendingRejectClick);
        Intent acceptIntent = new Intent(this, SwitchButtonListener.class);
        acceptIntent.putExtra(SENDER_ID, senDerID);
        acceptIntent.putExtra(SENDER_TOKEN, senderToken);
        acceptIntent.putExtra(SENDER_NAME, senderName);
        acceptIntent.setAction(Utils.ACCEPT_ACTION);

        PendingIntent pendingAcceptClick = PendingIntent.getBroadcast(this, 1234, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnAccept, pendingAcceptClick);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(Utils.NOTIFICATION_ID, notification);
    }

    private Bitmap getBitmapFromUrl(String strUrl) {
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
}
