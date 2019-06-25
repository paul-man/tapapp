package com.sackstack.tapapp;

import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class SeekBar {
    private IndicatorSeekBar seekBar;
    private MainActivity app;
    private AlphaAnimation indicatorFadeAnim;
    private boolean seekBarMovement;

    public SeekBar(IndicatorSeekBar seekBar, MainActivity app) {
        this.seekBar = seekBar;
        this.app = app;
        initBPMPreviewAnimation();
        initSeekBar();
    }

    public void setEnabled(boolean enabled) {
        this.seekBar.setEnabled(enabled);
    }

    private void initSeekBar() {
        seekBar.setIndicatorTextFormat("${PROGRESS} BPM");
        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                Log.i("SEEKBAR", "onSeeking");
                manageBlinkEffect();
            }
            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
                Log.i("SEEKBAR", "onStartTrackingTouch");
                seekBarMovement = true;
                pauseIndicatorAnim();
            }
            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                Log.i("SEEKBAR", "onStopTrackingTouch");
                seekBarMovement = false;
                app.gracePeriod = app.getGracePeriod(seekBar.getProgress());
                seekBar.getIndicator().getContentView().getAnimation().reset();
                manageBlinkEffect();
            }
        });
    }

    public int getProgress() {
        return seekBar.getProgress();
    }
    public int getMax() {
        return (int) seekBar.getMax();
    }

    public int getMin() {
        return (int) seekBar.getMin();
    }

    public void pauseIndicatorAnim() {
        seekBar.getIndicator().getContentView().getAnimation().reset();
        seekBar.getIndicator().getContentView().getAnimation().setDuration(Integer.MAX_VALUE);
    }
    public void restartIndicatorAnim() {
        seekBar.getIndicator().getContentView().getAnimation().reset();
        manageBlinkEffect();
    }


    public int getBPMInMilli() {
        System.out.println(seekBar.getProgress());
        return (int) ((60.0 / seekBar.getProgress()) * 1000);
    }


    private void initBPMPreviewAnimation() {
        indicatorFadeAnim = new AlphaAnimation(1.0f, 0.0f);
        indicatorFadeAnim.setDuration(getBPMInMilli());
        indicatorFadeAnim.setRepeatCount(Animation.INFINITE);
        indicatorFadeAnim.setRepeatMode(Animation.REVERSE);
    }


    public void manageBlinkEffect() {
        if (seekBarMovement) {
            return;
        }
        int progress = getBPMInMilli();
        long duration = progress / 2;
        if (!app.appStart){
            app.appStart = true;
            indicatorFadeAnim.setDuration(duration);
            seekBar.getIndicator().getContentView().startAnimation(indicatorFadeAnim);
        } else {
            seekBar.getIndicator().getContentView().getAnimation().setDuration(duration);
        }
    }
}
