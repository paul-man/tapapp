package com.sackstack.tapapp;

// Java imports
import java.util.Timer;
import java.util.TimerTask;
import android.support.v7.app.AppCompatActivity;

// Android imports
import android.os.Bundle;
import android.util.Log;

import android.content.Context;
import android.content.SharedPreferences;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

// 3rd party imports
import com.warkiz.widget.IndicatorSeekBar;

import spencerstudios.com.ezdialoglib.EZDialog;
import spencerstudios.com.ezdialoglib.EZDialogListener;

public class MainActivity extends AppCompatActivity {

    private int tapCount = 0;
    private int handHoldingTime = 5;
    private long lastTapTime = 0;
    private long originalTapTime = 0;
    public double gracePeriod;
    public double aCurve = 0;
    public double bCurve = 0;
    private boolean gameOver = false;
    public boolean appStart = false;
    private boolean seekBarMovement = false;
    private boolean stopHelping = false;
    private TextView tapBtn;
    private TextView tapCountView;
    private Timer tapTimer;
    private UserScores scores;
    private SeekBar seekBar;
    private Difficulty difficulty;
    private Spinner difficultySpinner;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDifficulty();
        scores = new UserScores(MainActivity.this);
        seekBar = new SeekBar((IndicatorSeekBar) findViewById(R.id.seekBar), MainActivity.this);
        initCurveAlgo(difficulty, seekBar.getMax(), seekBar.getMin());
        gracePeriod = getGracePeriod(seekBar.getProgress());
//        scores.clearScores();
        initViews();
        initAds();
        startupDialog();
    }

    public void initDifficulty() {
        SharedPreferences prefs = this.getSharedPreferences("com.sackstack.settings", Context.MODE_PRIVATE);
        String difficultyLabel = prefs.getString("difficulty", "easy");
        int index = 0;
        switch (difficultyLabel) {
            case "easy":
                index = 0;
                this.difficulty = Difficulty.EASY;
                break;
            case "normal":
                index = 1;
                this.difficulty = Difficulty.NORMAL;
                break;
            case "hard":
                index = 2;
                this.difficulty = Difficulty.HARD;
                break;
        }
        difficultySpinner = findViewById(R.id.difficulty_spinner);
        difficultySpinner.setSelection(index);
        initDifficultySpinner();
    }
    public int getGracePeriod(int bpm) {
        if (aCurve == 0 || bCurve == 0) {
            return 0;
        }
        return (int) (aCurve * Math.pow(bCurve, bpm));
    }

    public void initCurveAlgo(Difficulty diff, int maxBPM, int minBPM) {
        Log.i("CURVE","maxBPM: " + maxBPM);
        Log.i("CURVE","minBPM: " + minBPM);
        Log.i("CURVE","diff.getMaxGrace(): " + diff.getMaxGrace());
        Log.i("CURVE","diff.getMinGrace(): " + diff.getMinGrace());
        bCurve = Math.pow(1.0 / (diff.getMaxGrace() / diff.getMinGrace()), 1.0/(maxBPM - minBPM));
        aCurve = diff.getMaxGrace() / Math.pow(bCurve, minBPM);
        Log.i("CURVE","bCurve: " + bCurve);
        Log.i("CURVE","aCurve: " + aCurve);
    }

    private void initDifficultySpinner() {
        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // EASY
                        changeDifficulty(Difficulty.EASY);
                        break;
                    case 1: // NORMAL
                        changeDifficulty(Difficulty.NORMAL);
                        break;
                    case 2: // HARD
                        changeDifficulty(Difficulty.HARD);
                        break;
                }
                Log.i("SPINNER", "Difficulty: " + difficulty.getLabel());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void changeDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        SharedPreferences prefs = this.getSharedPreferences("com.sackstack.settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("difficulty", difficulty.getLabel());
        editor.commit();
        initCurveAlgo(difficulty, seekBar.getMax(), seekBar.getMin());
        gracePeriod = getGracePeriod(seekBar.getProgress());
    }
    public void showScores(View view) {
        restartGame(null);
        seekBar.pauseIndicatorAnim();
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
        seekBarMovement = false;
        stopHelping = false;
        handHoldingTime = 5;
        seekBar.setEnabled(true);
        tapCountView.setText("0");
        seekBar.restartIndicatorAnim();
    }




    private void initAds() {
        MobileAds.initialize(this,"ca-app-pub-5858630370976483~8800934464");
        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
    }
    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private void startupDialog() {
        int[] maxBPMResults = scores.getMaxBPM(difficulty.getLabel());
        int[] maxTapsResults = scores.getMaxTaps(difficulty.getLabel());
        String scoreMsg = capitalize(difficulty.getLabel())+"\n\n";
        if (maxBPMResults[0] == -1 || maxTapsResults[0] == -1) {
            scoreMsg += "No Scores yet!";
        } else {
            scoreMsg += "Best BPM:\n" + maxBPMResults[0] + " with " + maxBPMResults[1] + " taps\n";
            scoreMsg += "Best Taps:\n" + maxTapsResults[1] + " at " + maxTapsResults[0] + " BPM\n";
        }


        new EZDialog.Builder(this)
                .setTitle("How well can you keep time?")
                .setMessage(scoreMsg)
                .setPositiveBtnText("Start Tapping!")
                .setCancelableOnTouchOutside(false)
                .OnPositiveClicked(new EZDialogListener() {
                    @Override
                    public void OnClick() {
                        seekBar.manageBlinkEffect();
                    }
                })
                .setBackgroundColor(getResources().getColor(R.color.splash_blue))
                .build();
    }


    public void sendTap(View view) {
        if (gameOver) {
            return;
        }
        int milliBPM = seekBar.getBPMInMilli();
        gracePeriod = getGracePeriod(seekBar.getProgress());
        long now = System.currentTimeMillis();
        float tapDelay = (float)(now - lastTapTime);
        tapCount++;

        if (lastTapTime == 0) {
            originalTapTime = now;
            seekBar.setEnabled(false);
            tapBtn.setText("Tap!");
        } else {
            if ( tapDelay < ((float)milliBPM - gracePeriod)) {// || (float)milliBPM > (delay + allowedDeviation)) {
                gameOver("fast");
                return;
            }
        }
        if (!stopHelping && (now - originalTapTime) / 1000 > handHoldingTime) {
            stopHelping = true;
            seekBar.pauseIndicatorAnim();
        }
        logTapData(milliBPM, tapDelay);
        tapCountView.setText(Integer.toString(tapCount));
        lastTapTime = now;
        restartTapTimer((int)(milliBPM + gracePeriod));

        Log.i("TIMERDELAY", "milliBPM: " + milliBPM + " graceperiod: " + gracePeriod);
    }

    public void restartTapTimer(int delay) {
        TimerTask tooSlowTimerTask = new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        gameOver("slow");
                    }
                });
            }
        };
        if (tapTimer != null) {
            tapTimer.cancel();
        }
        tapTimer = new Timer();
        tapTimer.schedule(tooSlowTimerTask, delay);
    }

    private void logTapData(int progress, float delay) {
        Log.i("TAPDATA", "Interval: " + (float)progress);
        Log.i("TAPDATA", "delay: " + delay);
        Log.i("TAPDATA", "min: " + (delay - gracePeriod));
        Log.i("TAPDATA", "max: " + (delay + gracePeriod));
        Log.i("TAPDATA", "allowedDeviation: " + gracePeriod);
        Log.i("TAPDATA", "grace: " + gracePeriod);
    }

    private void gameOver(String reason) {
        gameOver = true;
        try {
            tapTimer.cancel();
        } catch (Exception e) {
            Log.e("GAMEOVER", "Error cancelling tapTimer");
        }
        tapTimer = null;
        seekBar.pauseIndicatorAnim();
        boolean isHighScore;
        String gameOverMsg = "";
        int result = scores.addScore(seekBar.getProgress(), tapCount, difficulty.getLabel());
        isHighScore = (result == 1);

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
        if (isHighScore) {
            gameOverMsg += "New high score for "+seekBar.getProgress()+" bpm!\n";
        }
        dialogBuilder.setMessage(gameOverMsg);
        ((EZDialog.Builder) dialogBuilder).build();
    }
}



