package com.sackstack.tapapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
//import com.pranavpandey.android.dynamic.dialogs.DynamicDialog;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import android.graphics.Color;
import java.util.Map;

import spencerstudios.com.ezdialoglib.EZDialog;
import spencerstudios.com.ezdialoglib.EZDialogListener;

public class MainActivity extends AppCompatActivity {

    private int tapCount = 0;
    private long lastTapTime = 0;
    private long originalTapTime = 0;
    private float allowedDeviation = 0.25f * 1000;
    private IndicatorSeekBar seekBar;
    private AlphaAnimation indicatorFadeAnim;
    private boolean gameOver = false;
    private boolean appStart = false;
    private TextView tapCountView;
    private int maxTaps = 0;
    private int maxBPM = 0;
    private int maxTapsCBPM = 0;
    private int maxBPMCTaps = 0;
    private boolean seekBarMovement = false;
    private TextView tapBtn;
    private boolean newHighScoreBPM = false;
    private boolean newHighScoreTaps = false;
    private boolean stopHelping = false;
    private int handHoldingTime = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initSeekBar();
        initBPMPreviewAnimation();
        initAds();

        startupDialog();
    }

    public void showScores(View view) {
        restartGame(null);
        startupDialog();
    }

    private void initViews() {
        tapBtn = (TextView) findViewById(R.id.tap_btn);
        tapCountView = (TextView) findViewById(R.id.tap_count);
    }

    public void restartGame(View view) {
        gameOver = false;
        tapCount = 0;
        lastTapTime = 0;
        originalTapTime = 0;
        maxTaps = 0;
        maxBPM = 0;
        maxTapsCBPM = 0;
        maxBPMCTaps = 0;
        seekBarMovement = false;
        newHighScoreBPM = false;
        newHighScoreTaps = false;
        stopHelping = false;
        handHoldingTime = 5;
        seekBar.setEnabled(true);
        setClassHighScores();
        tapCountView.setText("0");
        restartIndicatorAnim();
    }

    private void initSeekBar() {
        seekBar = (IndicatorSeekBar) findViewById(R.id.seekBar);
        seekBar.setIndicatorTextFormat("${PROGRESS} BPM");
        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                System.out.println(seekParams.progress);
                manageBlinkEffect(getBPMInMilli());
            }
            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
                seekBarMovement = true;
                pauseIndicatorAnim();
            }
            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                seekBarMovement = false;
                seekBar.getIndicator().getContentView().getAnimation().reset();
                manageBlinkEffect(getBPMInMilli());
            }
        });
    }

    private void pauseIndicatorAnim() {
        seekBar.getIndicator().getContentView().getAnimation().reset();
        seekBar.getIndicator().getContentView().getAnimation().setDuration(Integer.MAX_VALUE);
    }
    private void restartIndicatorAnim() {
        seekBar.getIndicator().getContentView().getAnimation().reset();
        manageBlinkEffect(getBPMInMilli());
    }

    private void initBPMPreviewAnimation() {
        indicatorFadeAnim = new AlphaAnimation(1.0f, 0.0f);
        indicatorFadeAnim.setDuration(getBPMInMilli());
        indicatorFadeAnim.setRepeatCount(Animation.INFINITE);
        indicatorFadeAnim.setRepeatMode(Animation.REVERSE);
    }

    private void initAds() {
        MobileAds.initialize(this,"ca-app-pub-5858630370976483~8800934464");
        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
    }

    private void startupDialog() {
        setClassHighScores();
        String scoreMsg = "";
        if (maxBPM == 0 && maxTaps == 0) {
            scoreMsg = "No Scores yet!";
        } else {
            scoreMsg = "Best BPM:\n" + maxBPM + " with " + maxTaps + " taps\n";
            scoreMsg += "Best Taps:\n" + maxTaps + " at " + maxBPM + " BPM\n";
        }

        new EZDialog.Builder(this)
            .setTitle("How well can you keep time?")
            .setMessage(scoreMsg)
            .setPositiveBtnText("Start Tapping!")
            .setCancelableOnTouchOutside(false)
            .OnPositiveClicked(new EZDialogListener() {
                @Override
                public void OnClick() {
                    manageBlinkEffect(getBPMInMilli());
                }
            })
                .setBackgroundColor(getResources().getColor(R.color.splash_blue))
            .build();
    }

    private void manageBlinkEffect(int progress) {
        if (seekBarMovement) {
            return;
        }
        long duration = progress / 2;
        if (!appStart){
            appStart = true;
            indicatorFadeAnim.setDuration(duration);
            seekBar.getIndicator().getContentView().startAnimation(indicatorFadeAnim);
        } else {
            seekBar.getIndicator().getContentView().getAnimation().setDuration(duration);
        }
    }

    public void sendTap(View view) {
        if (gameOver) {
            return;
        }
        long now = System.currentTimeMillis();
        tapCount++;

        if (lastTapTime == 0) {
            originalTapTime = now;
            seekBar.setEnabled(false);
            tapBtn.setText("Tap!");
        } else {
            int milliBPM = getBPMInMilli();
            float delay = (float)(now - lastTapTime);
            if ( (float)milliBPM < (delay - allowedDeviation) || (float)milliBPM > (delay + allowedDeviation)) {
                gameOver();
            }
            logTapData(milliBPM, delay);
        }
        if (!stopHelping && (now - originalTapTime) / 1000 > handHoldingTime) {
            stopHelping = true;
            pauseIndicatorAnim();
        }
        tapCountView.setText(Integer.toString(tapCount));
        lastTapTime = now;
    }

    private void logTapData(int progress, float delay) {
        System.out.println("Interval: " + (float)progress);
        System.out.println("delay: " + delay);
        System.out.println("min: " + (delay - allowedDeviation));
        System.out.println("max: " + (delay + allowedDeviation));
        System.out.println("allowedDeviation: " + allowedDeviation);
    }

    private void displayMessage(String msg) {
        TextView displayInteger = (TextView) findViewById(
                R.id.delay_message);
        displayInteger.setText(msg);
    }

    private int getBPMInMilli() {
        System.out.println(seekBar.getProgress());
        return (int) ((60.0 / seekBar.getProgress()) * 1000);
    }

    private void initSeekBarChangeListener() {

    }

    private void gameOver() {
        gameOver = true;
        int bpm = seekBar.getProgress();
        int score = tapCount;
        String gameOverMsg = "";
        int prevBPMScore = getBPMScore(bpm);
        if (score > prevBPMScore) {
            saveScore(bpm, score);
            newHighScoreBPM = true;
        }

        EZDialog.Builder dialogBuilder = new EZDialog.Builder(this)
            .setTitle("Off time!")
            .setPositiveBtnText("Try again?")
            .setCancelableOnTouchOutside(false)
            .OnPositiveClicked(new EZDialogListener() {
                @Override
                public void OnClick() {
                    restartGame(null);
                }
            })
            .setBackgroundColor(getResources().getColor(R.color.splash_blue));
        if (newHighScoreBPM) {
            gameOverMsg += "New high score for this BPM!\n";
        }
        if (newHighScoreTaps) {
            gameOverMsg += "New high score for this amount of taps!\n";
        }
        dialogBuilder.setMessage(gameOverMsg);
        ((EZDialog.Builder) dialogBuilder).build();
    }

    private int getBPMScore(int bpm) {
        String bpmKey = Integer.toString(bpm);
        SharedPreferences prefs = this.getSharedPreferences("userScores", Context.MODE_PRIVATE);
        return prefs.getInt(bpmKey,0);
    }

    private void saveScore(int bpm, int score) {
        String bpmKey = Integer.toString(bpm);
        SharedPreferences prefs = this.getSharedPreferences("userScores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(bpmKey, score);
        editor.commit();
    }

    private void setClassHighScores() {
        maxTaps = 0;
        maxBPM = 0;
        maxTapsCBPM = 0;
        maxBPMCTaps = 0;
        SharedPreferences prefs = this.getSharedPreferences("userScores", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            int bpm = Integer.parseInt(entry.getKey());
            int taps = Integer.parseInt(entry.getValue().toString());
            if (bpm > maxBPM) {
                maxBPM = bpm;
                maxBPMCTaps = taps;
            }
            if (taps > maxTaps) {
                maxTaps = taps;
                maxTapsCBPM = bpm;
            }
        }
    }
}



