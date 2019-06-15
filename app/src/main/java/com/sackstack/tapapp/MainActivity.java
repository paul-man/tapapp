package com.sackstack.tapapp;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.warkiz.widget.IndicatorSeekBar;
import java.util.Timer;
import java.util.TimerTask;
import spencerstudios.com.ezdialoglib.EZDialog;
import spencerstudios.com.ezdialoglib.EZDialogListener;

public class MainActivity extends AppCompatActivity {

    private int tapCount = 0;
    private long lastTapTime = 0;
    private long originalTapTime = 0;
    public double gracePeriod;
    private SeekBar seekBar;
    private boolean gameOver = false;
    public boolean appStart = false;
    private TextView tapCountView;
    private boolean seekBarMovement = false;
    private TextView tapBtn;
    private boolean stopHelping = false;
    private int handHoldingTime = 5;
    private UserScores scores;
    private Timer tapTimer;
    public double aCurve = 0;
    public double bCurve = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Difficulty difficulty = Difficulty.EASY;
        scores = new UserScores(MainActivity.this);
        seekBar = new SeekBar((IndicatorSeekBar) findViewById(R.id.seekBar), MainActivity.this);
        initCurveAlgo(difficulty, seekBar.getMax(), seekBar.getMin());
        gracePeriod = getGracePeriod(seekBar.getProgress());
//        scores.clearScores();
        initViews();
        initAds();
        startupDialog();
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
        scores.setMaxTaps(0);
        scores.setMaxBPM(0);
        scores.setMaxTapsCBPM(0);
        scores.setMaxBPMCTaps(0);
        scores.setNewHighScoreBPM(false);
        scores.setNewHighScoreTaps(false);
        seekBarMovement = false;
        stopHelping = false;
        handHoldingTime = 5;
        seekBar.setEnabled(true);
        scores.setClassHighScores();
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

    private void startupDialog() {
        scores.setClassHighScores();
        String scoreMsg = "";
        if (scores.getMaxBPM() == 0 && scores.getMaxTaps() == 0) {
            scoreMsg = "No Scores yet!";
        } else {
            scoreMsg = "Best BPM:\n" + scores.getMaxBPM() + " with " + scores.getMaxTaps() + " taps\n";
            scoreMsg += "Best Taps:\n" + scores.getMaxTaps() + " at " + scores.getMaxBPM() + " BPM\n";
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

    private void displayMessage(String msg) {
        TextView displayInteger = (TextView) findViewById(
                R.id.delay_message);
        displayInteger.setText(msg);
    }

    private void gameOver(String reason) {
        gameOver = true;
        tapTimer = null;
        int bpmScore = seekBar.getProgress();
        int tapScore = tapCount;
        String gameOverMsg = "";
        int prevBPMScore = scores.getBPMScore(bpmScore);
        int prevTapScore = scores.getTapScore(tapScore);
        if (score > prevBPMScore) {
            scores.saveScore(bpm, score);
            scores.setNewHighScoreBPM(true);
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
        if (scores.isNewHighScoreBPM()) {
            gameOverMsg += "New high score for this BPM!\n";
        }
        if (scores.isNewHighScoreTaps()) {
            gameOverMsg += "New high score for this amount of taps!\n";
        }
        dialogBuilder.setMessage(gameOverMsg);
        ((EZDialog.Builder) dialogBuilder).build();
    }
}



