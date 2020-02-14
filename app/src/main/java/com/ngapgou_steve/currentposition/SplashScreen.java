package com.ngapgou_steve.currentposition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread splash = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                }catch (Exception e){

                }finally {
                    startActivity(new Intent(getApplicationContext() ,MapsActivity.class));
                }
            }
        };
        splash.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
