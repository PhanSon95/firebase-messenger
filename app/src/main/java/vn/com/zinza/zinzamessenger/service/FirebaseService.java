package vn.com.zinza.zinzamessenger.service;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Streaming;
import retrofit.http.Url;

/**
 * Created by ASUS on 03/13/2017.
 */

public interface FirebaseService {
    @GET()
    @Streaming
    Call<ResponseBody> downloadAttachment(@Url String fileUrl);
}
