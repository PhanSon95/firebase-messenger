package vn.com.zinza.zinzamessenger.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.utils.Utils;

public class VideoViewActivity extends Activity {

    // Declare variables
    ProgressDialog pDialog;
    VideoView videoview;

    // Insert your Video URL
//    String VideoURL = "https://firebasestorage.googleapis.com/v0/b/zinza-4acc9.appspot.com/o/yaWteb938JOG7rJ3g4EpGIuwNCn2-zKfKfezloxgCdlKm7XmExCEzTup2%2Ffiles%2FMOV_0124.mp4?alt=media&token=a7ec5fa5-5ae2-4cd1-bf33-9a951defaa9b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the layout from video_main.xml
        setContentView(R.layout.videoview_main);
        // Find your VideoView in your video_main.xml layout
        videoview = (VideoView) findViewById(R.id.VideoView);
        // Execute StreamVideo AsyncTask

        // Create a progressbar
        pDialog = new ProgressDialog(VideoViewActivity.this);
        // Set progressbar title
        pDialog.setTitle("Play video");
        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();

        String getIntentUrl=getIntent().getStringExtra(Utils.URL_STREAMING);

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    VideoViewActivity.this);
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(getIntentUrl);
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

            videoview.requestFocus();
            videoview.setOnPreparedListener(new OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    pDialog.dismiss();
                    videoview.start();
                }
            });

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }


    }

}