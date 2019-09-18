package com.sunmi.sunmiK1Ext.present;

import android.os.CountDownTimer;

public class CountDownPresent {
    private int tickTime = 1000;
    CountDownTimer timer;
    CountDownCallback countDownCallback;

    private int startTime = -1;

    private boolean isStop = false;

    private CountDownPresent() {
        start();
    }

    private static class Singleton {
        private static final CountDownPresent present = new CountDownPresent();
    }

    public static CountDownPresent getInstance() {
        return Singleton.present;
    }

    public interface CountDownCallback {
        void onTick(long millisUntilFinished);

        void onSuccess();
    }

    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setCountDownCallback(CountDownCallback countDownCallback) {
        this.countDownCallback = countDownCallback;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public void start() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(600000, tickTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isStop) {
                    return;
                }
                startTime--;
                if ((startTime == 0)) {
                    if (countDownCallback != null) {
                        countDownCallback.onSuccess();
                    }
                }
                if (countDownCallback != null && startTime >= 0) {
                    countDownCallback.onTick(startTime);
                }
            }

            @Override
            public void onFinish() {
                timer.start();
            }
        };
        timer.start();
    }

    public void cancel() {
        timer.cancel();
    }


}
