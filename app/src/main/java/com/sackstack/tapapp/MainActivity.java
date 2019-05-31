package com.sackstack.tapapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mohammedalaa.seekbar.RangeSeekBarView;

import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.animation.ObjectAnimator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private boolean timerStarted = false;
    private int interval = 1;
    private float deviation = 0;
    private int taps = 0;
    private long lastTapTime = 0;
    private float allowedDeviation = 0.25f * 1000;
    private TextView bpm_label;
    private ObjectAnimator anim;
    private boolean bpm_visible = true;
    private Timer task;
    private RangeSeekBarView rangeSeekbar;

    int minteger = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rangeSeekbar = (RangeSeekBarView) findViewById(R.id.range_seekbar);
        rangeSeekbar.setAnimated(true,3000L);
        TextView bpm_value = (TextView) findViewById(
                R.id.bpm_value);
        bpm_value.setText(rangeSeekbar.getValue() + " ");

        bpm_label = (TextView) findViewById(
                R.id.bpm_label);
        MobileAds.initialize(this,"ca-app-pub-5858630370976483~8800934464");

        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
        adView.loadAd(adRequest);
        manageBlinkEffect();
    }

    private void manageBlinkEffect() {
        long duration = (long) ((60.0 / rangeSeekbar.getValue()) * 1000) / 2;
        if (task != null) {
            task.cancel();
        }
        task = new Timer();
        task.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                int alpha = 0;
                if (!bpm_visible) {
                    alpha = 1;
                }
                bpm_visible = !bpm_visible;
                bpm_label.setAlpha(alpha);
            }
        },0,duration);
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
            double progress = (60.0 / rangeSeekbar.getValue()) * 1000;
            float delay = (float)(lastTapTime - now)/-1000*1000;
            System.out.println("Interval: " + (float)progress);
            System.out.println("delay: " + delay);
            System.out.println("min: " + (delay - allowedDeviation));
            System.out.println("max: " + (delay + allowedDeviation));
            System.out.println("allowedDeviation: " + allowedDeviation);
            if ( (float)progress < (delay - allowedDeviation) || (float)progress > (delay + allowedDeviation)) {
                displayMessage("Off time!");
            } else {
                displayMessage("Good!");
            }
        }

        lastTapTime = now;
    }

    private void displayMessage(String msg) {
        TextView displayInteger = (TextView) findViewById(
                R.id.delay_message);
        displayInteger.setText(msg);
    }
}



