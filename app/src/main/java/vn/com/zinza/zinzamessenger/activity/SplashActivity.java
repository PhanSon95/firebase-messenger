package vn.com.zinza.zinzamessenger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import vn.com.zinza.zinzamessenger.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    gotoLoginScreen();
                    finish();
                }
            }
        };
        timerThread.start();
    }
    private void gotoMainScreen(){
        Intent intent = new Intent(SplashActivity.this,MessageFriendActivity.class);
        startActivity(intent);
    }
    private void gotoLoginScreen(){
        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
