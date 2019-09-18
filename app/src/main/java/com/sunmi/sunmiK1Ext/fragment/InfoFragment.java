package com.sunmi.sunmiK1Ext.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.extprinterservice.ExtPrinterService;
import com.sunmi.sunmiK1Ext.BaseFragment;
import com.sunmi.sunmiK1Ext.MainActivity;
import com.sunmi.sunmiK1Ext.R;
import com.sunmi.sunmiK1Ext.bean.HotelUser;
import com.sunmi.sunmiK1Ext.dialog.Create;
import com.sunmi.sunmiK1Ext.dialog.CustomDialog;
import com.sunmi.sunmiK1Ext.model.RoomModel;
import com.sunmi.sunmiK1Ext.present.CountDownPresent;
import com.sunmi.sunmiK1Ext.present.MiCardPresent;
import com.sunmi.mifarecard.IMifareCard;
import com.sunmi.sunmiK1Ext.present.PrinterPresent;

import java.util.Random;

public class InfoFragment extends BaseFragment implements View.OnClickListener, CountDownPresent.CountDownCallback {

    private TextView confirm;
    private TextView tvCountDown;
    private TextView timeMsg;
    private ImageView ivInfoType;
    private TextView goNext;
    private TextView msg;
    private TextView tips;

//    private MiCardPresent miCardPresent;    //K1版不发卡，直接打印
    private PrinterPresent printerPresent;

    private HotelUser hotelUser;
    boolean isSuccess;
    ObjectAnimator showBg = new ObjectAnimator();
    ObjectAnimator animator = new ObjectAnimator();


    byte[] cardNo = new byte[32];//卡号
    byte[] bytes = new byte[128];//卡数据

    public void setHotelUser(HotelUser hotelUser) {
        this.hotelUser = hotelUser;
    }

    public void setPrinterServiceAidl(ExtPrinterService mPrinter) {
        if (mPrinter != null) {
            printerPresent = new PrinterPresent(getContext(),mPrinter);    //打印机没有异步回调
        }
    }


    @Override
    protected int getViewId() {
        return R.layout.fragment_info;
    }

    @Override
    protected void initView(View view) {
        confirm = (TextView) view.findViewById(R.id.confirm);

        tvCountDown = (TextView) view.findViewById(R.id.tv_count_down);
        timeMsg = (TextView) view.findViewById(R.id.time_msg);
        ivInfoType = (ImageView) view.findViewById(R.id.iv_info_type);


        goNext = (TextView) view.findViewById(R.id.goNext);

        msg = (TextView) view.findViewById(R.id.msg);

        tips = (TextView) view.findViewById(R.id.tips);

    }

    @Override
    protected void initData() {
        switch (RoomModel.getRoom(getContext(), mParam1)) {
            case RoomModel.one:
                ivInfoType.setImageResource(R.drawable.ic_onebed);
                break;
            case RoomModel.two:
                ivInfoType.setImageResource(R.drawable.ic_twobed);
                break;
            default:
                ivInfoType.setImageResource(R.drawable.ic_bigbed);
                break;
        }

//        SpannableString spannableString = new SpannableString(confirm.getText());
//        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6000")), 5, confirm.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        confirm.setText(spannableString);
        confirm.setText(setNumColor(getString(R.string.info_num_hotel) + new Random().nextInt(1000)));

//        SpannableString spannableString1 = new SpannableString(tips.getText());
//        spannableString1.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6000")), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString1.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6000")), 9, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tips.setText(setNumColor(tips.getText().toString()));

        showBg.setDuration(300);
        showBg.setFloatValues(1.0f, 0.0f, 1.0f);
        showBg.setTarget(null);
        showBg.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                if (Math.abs(animatedValue) < 0.02f) {
                    confirm.setText(getString(R.string.info_hotel_success1));
                    msg.setText(getString(R.string.info_hotel_success2));
                }
                confirm.setAlpha(animatedValue);
                msg.setAlpha(animatedValue);
                ivInfoType.setAlpha(animatedValue);

            }
        });

        animator.setDuration(300).setFloatValues(0.0f, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                goNext.setAlpha(animatedValue);
            }
        });
    }

    public static SpannableStringBuilder setNumColor(String str) {
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            if (a >= '0' && a <= '9') {
                style.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6000")), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                style.setSpan(new RelativeSizeSpan(3.0f), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
        }
        return style;
    }


    @Override
    protected void initAction() {
        goNext.setOnClickListener(this);
        if (printerPresent != null) {
            printerPresent.print(hotelUser);
            //这俩放到一个按钮点击事件里去，点击确认结束
            isSuccess = true;
        }

        CountDownPresent.getInstance().setCountDownCallback(this);
        CountDownPresent.getInstance().setStartTime(30);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        tvCountDown.setText("" + millisUntilFinished);
    }

    @Override
    public void onSuccess() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    backTopFragment();
                } else {
                    showDialog();
                }
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goNext:
                backTopFragment();
                break;
        }
    }

    CustomDialog dialog;

    void showDialog() {
        if (dialog == null) {
            dialog = Create.createDialog(getContext(), R.drawable.ic_room, getString(R.string.dialog_no_micard), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    backTopFragment();
                }
            }, new CustomDialog.DialogOver() {
                @Override
                public void over() {
                    backTopFragment();
                }
            });
            dialog.show();
        } else {
            dialog.show();
        }
    }
}
