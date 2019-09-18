package com.sunmi.sunmiK1Ext.present;

import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.Map;

public class DelayedPresent {
    private Map<Integer, Runnable> map = new HashMap<>();

    private int what = -1;

    private boolean isClose;

    private boolean isStop;

    public DelayedPresent() {

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isClose) {
                return;
            }
            if (map.containsKey(msg.what)) {
                if (isStop) {
                    handler.sendEmptyMessageDelayed(msg.what, 500);
                    return;
                }
                map.get(msg.what).run();
            }
        }
    };


    public int postDelayed(Runnable r, long delayMillis) {
        if (what > 100) {
            what = -1;
        }
        if (isClose) {
            return -1;
        }
        what++;
        handler.sendEmptyMessageDelayed(what, delayMillis);
        map.put(what, r);
        return what;
    }

    public void clear() {
        isClose = true;
        map.clear();
    }

    public void setStop() {
        isStop = true;
    }

    public void setResume() {
        isStop = false;
    }
}
