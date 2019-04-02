package com.example.tapapp;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.tapapp.TapSeekbar;

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
        } else {
            float delay = (float)(lastTapTime - now)/-1000;
            displayDelay(delay);

            // New average = old average * (n-1)/n + new value /n
            deviation = deviation * (taps - 1)/taps + delay/taps;
            displayDeviation(deviation);
        }

        lastTapTime = now;
    }

    private void displayDelay(float number) {
        TextView displayInteger = (TextView) findViewById(
                R.id.last_delay_number);
        displayInteger.setText("Time since last tap:\n" + number + " seconds");
    }
    private void displayDeviation(float number) {
        System.out.println("Deviation: " + number);
        TextView displayInteger = (TextView) findViewById(
                R.id.deviation_number);
        displayInteger.setText("Deviation:\n" + number + " seconds");
    }
}



