package com.sackstack.tapapp;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.warkiz.widget.IndicatorSeekBar;

import java.util.Timer;
import java.util.TimerTask;

public class TapGame {
//    private IndicatorSeekBar seekBar;
//    private int tapCount = 0;
//    private long lastTapTime = 0;
//    private long originalTapTime = 0;
//    private float gracePeriod = 0.25f * 1000;
//    private boolean stopHelping;
//    private int handHoldingTime;
//    private UserScores scores;
//    private Timer tapTimer;
//    private TimerTask tooSlowTimerTask;
//    private boolean gameOver;
//    private MainActivity app;
//
//    public TapGame(IndicatorSeekBar seekBar, UserScores scores) {
//        this.seekBar = seekBar;
//        this.scores = scores;
//        this.app = MainActivity.getInstance();
//        scores.clearScores();
//        gameOver = false;
//        tapTimer = null;
//        handHoldingTime = 5;
//        stopHelping = false;
//        gracePeriod = 0.25f * getBPMInMilli();
//    }
//
//    public void restartGame(View view) {
//        gameOver = false;
//        tapCount = 0;
//        lastTapTime = 0;
//        originalTapTime = 0;
//        scores.setMaxTaps(0);
//        scores.setMaxBPM(0);
//        scores.setMaxTapsCBPM(0);
//        scores.setMaxBPMCTaps(0);
//        scores.setNewHighScoreBPM(false);
//        scores.setNewHighScoreTaps(false);
//        seekBarMovement = false;
//        stopHelping = false;
//        handHoldingTime = 5;
//        seekBar.setEnabled(true);
//        scores.setClassHighScores();
//        app.setTapCountText("0");
//        app.restartIndicatorAnim();
//    }
//
//    public void tap() {
//
//    }
//
//
//    private int getBPMInMilli() {
//        Log.i("SEEKBAR", Integer.toString(seekBar.getProgress()));
//        return (int) ((60.0 / seekBar.getProgress()) * 1000);
//    }
}
