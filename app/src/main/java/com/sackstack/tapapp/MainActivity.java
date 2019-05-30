package com.sackstack.tapapp;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    private boolean timerStarted = false;
    private int interval = 1;
    private float deviation = 0;
    private int taps = 0;
    private long lastTapTime = 0;
    private TapSeekbar seekBar;
    private float allowedDeviation = 0.25f;

    int minteger = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = new TapSeekbar((SeekBar)findViewById(R.id.seekBar), findViewById(R.id.seekBar_value));;

        MobileAds.initialize(this,"ca-app-pub-5858630370976483~8800934464");

        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
        adView.loadAd(adRequest);
    }

    public void sendTap(View view) {
        taps++;
        long now = System.currentTimeMillis();

        if (lastTapTime == 0) {
            lastTapTime = now;
            TextView startMessage = (TextView) findViewById(
                    R.id.start_message);
            startMessage.setText("GO!");
        } else {
            float delay = (float)(lastTapTime - now)/-1000;
            System.out.println("Interval: " + (float)seekBar.getSeekbarProgress());
            System.out.println("Selay: " + delay);
            if (
                    (float)seekBar.getSeekbarProgress() < (delay - allowedDeviation)
                    || (float)seekBar.getSeekbarProgress() > (delay + allowedDeviation)) {
                displayMessage("Off time!");
            } else {
                displayMessage("Good!");
            }
        }

        lastTapTime = now;
    }

//    private void displayDelay(float number) {
//        TextView displayInteger = (TextView) findViewById(
//                R.id.last_delay_number);
//        displayInteger.setText("Time since last tap:\n" + number + " seconds");
//    }
    private void displayMessage(String msg) {
        TextView displayInteger = (TextView) findViewById(
                R.id.delay_message);
        displayInteger.setText(msg);
    }
//    private void displayDeviation(float number) {
//        System.out.println("Deviation: " + number);
//        TextView displayInteger = (TextView) findViewById(
//                R.id.deviation_number);
//        displayInteger.setText("Deviation:\n" + number + " seconds");
//    }
}



