package com.example.tapapp;

import android.widget.SeekBar;
import android.widget.TextView;

import android.view.View;

public class TapSeekbar {
    private SeekBar seekBar;
    private View seekBarValue;
    private MainActivity activity;

    public TapSeekbar(SeekBar seekBar, View seekBarValue) {
        this.seekBar = seekBar;
        this.seekBarValue = seekBarValue;
        initSeekbar();
    }

    private void initSeekbar() {
        // perform seek bar change listener event used for getting the progress value
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                TextView displayInteger = (TextView) seekBarValue;
                displayInteger.setText(progressChangedValue + " second intervals");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
    }
}
