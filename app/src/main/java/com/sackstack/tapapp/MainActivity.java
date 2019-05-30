package com.sackstack.tapapp;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.animation.Animation;
import android.animation.ValueAnimator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private boolean timerStarted = false;
    private int interval = 1;
    private float deviation = 0;
    private int taps = 0;
    private long lastTapTime = 0;
    private SeekBar seekBar;
    private float allowedDeviation = 0.25f * 1000;
    private TextView bpm_label;
    private ObjectAnimator anim;
    private boolean bpm_visible = true;
    private Timer task;

    int minteger = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        bpm_label = (TextView) findViewById(
                R.id.bpm_label);
        MobileAds.initialize(this,"ca-app-pub-5858630370976483~8800934464");

        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
        adView.loadAd(adRequest);
        manageBlinkEffect();
        initSeekbar();
    }

    private void manageBlinkEffect() {
//        anim = ObjectAnimator.ofInt(bpm_label, "backgroundColor", Color.WHITE, Color.RED);
//        anim.setDuration((long)(60 / seekBar.getProgress()) * 1000);
//        anim.setEvaluator(new ArgbEvaluator());
//        anim.setRepeatMode(ValueAnimator.RESTART);
//        anim.setRepeatCount(Animation.INFINITE);
//        anim.start();
        long duration = (long) ((60.0 / seekBar.getProgress()) * 1000) / 2;
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
        TextView bpm_value = (TextView) findViewById(
                R.id.bpm_value);
        bpm_value.setText(seekBar.getProgress() + " ");
    }
    private void updateBlinkSpeed(int progress) {
        long duration = (long) ((60.0 / progress) * 1000) / 2;
        System.out.println(duration);
        anim.setDuration(duration).start();
        TextView bpm_value = (TextView) findViewById(
                R.id.bpm_value);
        bpm_value.setText(progress + "");
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
            double progress = (60.0 / seekBar.getProgress()) * 1000;
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
    private void initSeekbar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                manageBlinkEffect();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}



