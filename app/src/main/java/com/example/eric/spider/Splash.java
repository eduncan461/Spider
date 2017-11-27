package com.example.eric.spider;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Splash extends Activity{

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    private ImageButton buttonPlay;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        /*
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // Create an Intent that will start the Menu-Activity.
                Intent mainIntent = new Intent(Splash.this,SpiderGame.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH); */
        ImageView splash = (ImageView) findViewById(R.id.splash);
        // set a onclick listener for when the button gets clicked
        splash.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                Intent mainIntent = new Intent(Splash.this,SpiderGame.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        });
    }
}
