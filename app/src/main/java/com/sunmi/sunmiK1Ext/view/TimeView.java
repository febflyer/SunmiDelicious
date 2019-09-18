package com.sunmi.sunmiK1Ext.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.sunmiK1Ext.R;

import java.util.Calendar;
import java.util.Locale;

public class TimeView extends LinearLayout {
    private TimeHandler mTimehandler = new TimeHandler();
    private TextView mHour;
    private TextView mMaohao;
    private TextView mMinute;

    public TimeView(Context context) {
        super(context);
        Init(context);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    //初始化方法
    private void Init(Context context) {
        inflate(getContext(),R.layout.layout_time_view,this);
        mHour = ((TextView) findViewById(R.id.hour));
        mMaohao = ((TextView) findViewById(R.id.maohao));
        mMinute = ((TextView) findViewById(R.id.minute));
        try {
            //初始化textview显示时间
            updateClock(true);
            //更新进程开始
            new TimeThread().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //更新子线程
    private class TimeThread extends Thread {
        @Override
        public void run() {
            mTimehandler.startScheduleUpdate();
        }
    }

    //重要的更新Handler
    private class TimeHandler extends Handler {
        private boolean mStopped;

        private void post() {
            sendMessageDelayed(obtainMessage(0), 1000 * (60 - Calendar.getInstance().get(Calendar.SECOND)));
            sendMessageDelayed(obtainMessage(1), 1000);
        }

        private boolean show = true;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!mStopped) {
                if (msg.what == 0) {
                    updateClock(true);
                    sendMessageDelayed(obtainMessage(0), 1000 * (60 - Calendar.getInstance().get(Calendar.SECOND)));
                } else if (msg.what == 1) {
                    updateClock(show);
                    show = !show;
                    sendMessageDelayed(obtainMessage(1), 1000);
                }
            }
        }

        //开始更新
        public void startScheduleUpdate() {
            mStopped = false;
            post();
        }

        //停止更新
        public void stopScheduleUpdate() {
            mStopped = true;
            removeMessages(0);
        }
    }

    private void updateClock(boolean isShow) {
        //更新时间
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        mHour.setText(String.format(Locale.getDefault(), "%02d", hour));
        mMaohao.setVisibility(isShow? VISIBLE : INVISIBLE);
        mMinute.setText(String.format(Locale.getDefault(), "%02d", minute));
    }


}