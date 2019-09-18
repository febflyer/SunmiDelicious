package com.sunmi.sunmiK1Ext.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.sunmiK1Ext.R;

public class CustomDialog extends Dialog implements View.OnTouchListener {
    private ImageView dialogIcon;
    private TextView time;
    private TextView dialogBtn;
    private Handler handler;
    int count = 30;

    private TextView dialogBtn1;
    private TextView dialogBtn2;

    DialogOver dialogOver;

    public CustomDialog(@NonNull Context context) {
        super(context, R.style.DialogStyle);
        setContentView(R.layout.dialog_custom);
    }

    public CustomDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.DialogStyle);
    }

    protected CustomDialog(@NonNull Context context, boolean cancelable, @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        dialogIcon = (ImageView) findViewById(R.id.dialog_icon);
        time = (TextView) findViewById(R.id.time);
        dialogBtn = (TextView) findViewById(R.id.dialog_btn);


        dialogBtn.setClipToOutline(true);
        dialogBtn.setOutlineProvider(getProvider());

        dialogBtn1 = (TextView) findViewById(R.id.dialog_btn1);
        dialogBtn2 = (TextView) findViewById(R.id.dialog_btn2);
        dialogBtn1.setClipToOutline(true);
        dialogBtn1.setOutlineProvider(getProvider());
        dialogBtn2.setClipToOutline(true);
        dialogBtn2.setOutlineProvider(getProvider());

        dialogBtn2.setOnTouchListener(this);
        dialogBtn1.setOnTouchListener(this);
        dialogBtn.setOnTouchListener(this);

    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressed(v);
                break;
            case MotionEvent.ACTION_UP:
                unPressed(v);
                break;
            case MotionEvent.ACTION_CANCEL:
                unPressed(v);
                break;
        }

        return false;
    }

    private void pressed(View v){
        switch (v.getId()) {
            case R.id.dialog_btn:
                v.setBackgroundColor(getContext().getResources().getColor(R.color.btn_press));
                break;
            case R.id.dialog_btn1:
                v.setBackgroundColor(Color.parseColor("#88333C4F"));
                break;
            case R.id.dialog_btn2:
                v.setBackgroundColor(getContext().getResources().getColor(R.color.btn_press));
                break;
        }
    }

    private void unPressed(View v){
        switch (v.getId()) {
            case R.id.dialog_btn:
                v.setBackgroundColor(getContext().getResources().getColor(R.color.orange));
                break;
            case R.id.dialog_btn1:
                v.setBackgroundColor(Color.parseColor("#66333C4F"));
                break;
            case R.id.dialog_btn2:
                v.setBackgroundColor(getContext().getResources().getColor(R.color.orange));
                break;
        }
    }


    @NonNull
    private ViewOutlineProvider getProvider() {
        return new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), view.getHeight() / 2);
            }
        };
    }

    @Override
    public void show() {
        super.show();
        count = 30;
        time.setText(count + "");
        timer();
    }

    @Override
    public void dismiss() {
        handler.removeCallbacksAndMessages(null);
        handler.removeMessages(0);
        handler = null;
        super.dismiss();
    }

    public void setDialogOver(DialogOver dialogOver) {
        this.dialogOver = dialogOver;
    }

    private void timer() {
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                count--;
                if (count == 0 && isShowing()) {
                    dismiss();
                    if (dialogOver != null) {
                        dialogOver.over();
                    }
                    return;
                }
                timer();
                time.setText(count + "");
            }
        }, 1000);
    }



    public interface DialogOver {
        void over();
    }
}
