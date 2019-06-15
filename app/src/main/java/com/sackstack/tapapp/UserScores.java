package com.sackstack.tapapp;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class UserScores {
    private int maxTaps = 0;
    private int maxBPM = 0;
    private int maxTapsCBPM = 0;
    private int maxBPMCTaps = 0;
    private boolean newHighScoreBPM = false;
    private boolean newHighScoreTaps = false;
    private MainActivity app;

    public UserScores( MainActivity app) {
        this.app = app;
    }

    public int getBPMScore(int bpm) {
        String bpmKey = Integer.toString(bpm);
        SharedPreferences prefs = app.getSharedPreferences("userScores", Context.MODE_PRIVATE);
        return prefs.getInt(bpmKey,0);
    }

    public void saveScore(int bpm, int score) {
        String bpmKey = Integer.toString(bpm);
        SharedPreferences prefs = app.getSharedPreferences("userScores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(bpmKey, score);
        editor.commit();
    }

    public void setClassHighScores() {
        maxTaps = 0;
        maxBPM = 0;
        maxTapsCBPM = 0;
        maxBPMCTaps = 0;
        SharedPreferences prefs = app.getSharedPreferences("userScores", Context.MODE_PRIVATE);
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

    public void clearScores() {
        SharedPreferences prefs = app.getSharedPreferences("userScores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public int getMaxTaps() {
        return maxTaps;
    }

    public void setMaxTaps(int maxTaps) {
        this.maxTaps = maxTaps;
    }

    public int getMaxBPM() {
        return maxBPM;
    }

    public void setMaxBPM(int maxBPM) {
        this.maxBPM = maxBPM;
    }

    public int getMaxTapsCBPM() {
        return maxTapsCBPM;
    }

    public void setMaxTapsCBPM(int maxTapsCBPM) {
        this.maxTapsCBPM = maxTapsCBPM;
    }

    public int getMaxBPMCTaps() {
        return maxBPMCTaps;
    }

    public void setMaxBPMCTaps(int maxBPMCTaps) {
        this.maxBPMCTaps = maxBPMCTaps;
    }

    public boolean isNewHighScoreBPM() {
        return newHighScoreBPM;
    }

    public void setNewHighScoreBPM(boolean newHighScoreBPM) {
        this.newHighScoreBPM = newHighScoreBPM;
    }

    public boolean isNewHighScoreTaps() {
        return newHighScoreTaps;
    }

    public void setNewHighScoreTaps(boolean newHighScoreTaps) {
        this.newHighScoreTaps = newHighScoreTaps;
    }
}
