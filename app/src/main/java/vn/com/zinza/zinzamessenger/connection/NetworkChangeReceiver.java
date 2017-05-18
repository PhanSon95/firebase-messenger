package vn.com.zinza.zinzamessenger.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import vn.com.zinza.zinzamessenger.utils.Utils;

/**
 * Created by ASUS on 4/4/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);
        Utils.showToast(status,context);
        Utils.CONNECTION_STATUS = NetworkUtil.getConnectivityStatus(context);


    }
}
