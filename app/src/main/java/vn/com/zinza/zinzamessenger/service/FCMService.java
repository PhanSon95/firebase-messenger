package vn.com.zinza.zinzamessenger.service;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;
import vn.com.zinza.zinzamessenger.model.BodyRequestFCM;
import vn.com.zinza.zinzamessenger.model.BodyRequestGCM;
import vn.com.zinza.zinzamessenger.model.ResultRequestFCM;
import vn.com.zinza.zinzamessenger.model.ResultRequestGCM;
import vn.com.zinza.zinzamessenger.utils.Utils;

/**
 * Created by ASUS on 02/28/2017.
 */

public interface FCMService {
    @Headers({
            "Authorization: key="+ Utils.FCM_KEY,
            "Content-Type: application/json"
    })
    @POST("/fcm/send")
    Call<ResultRequestFCM> sendPush(@Body BodyRequestFCM mBodyRequestFCM);
    @Headers({
            "Authorization: key="+ Utils.FCM_KEY,
            "Content-Type: application/json"
    })
    @POST("/gcm/send")
    Call<ResultRequestGCM> sendMultiPush(@Body BodyRequestGCM bodyRequestGCM);

}
