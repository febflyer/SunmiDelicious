package com.sunmi.sunmiK1Ext.fragment;

;

import android.animation.Animator;
import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sunmi.idcardservice.MiFareCardAidl;
import com.sunmi.sunmiK1Ext.BaseFragment;

import com.sunmi.sunmiK1Ext.MainActivity;
import com.sunmi.sunmiK1Ext.R;
import com.sunmi.sunmiK1Ext.bean.HotelUser;
import com.sunmi.sunmiK1Ext.dialog.Create;
import com.sunmi.sunmiK1Ext.dialog.CustomDialog;
import com.sunmi.sunmiK1Ext.present.CountDownPresent;
import com.sunmi.sunmiK1Ext.present.IDCardPresent;
import com.sunmi.sunmiK1Ext.view.CircularProgressView;
import com.sunmi.idcardservice.IDCardInfo;
import com.sunmi.idcardservice.IDCardServiceAidl;


public class IDCardFragment extends BaseFragment implements IDCardPresent.IDCardCallBack, CountDownPresent.CountDownCallback {

    private IDCardPresent idCardPresent;

    private HotelUser hotelUser;
    private TextView tvCountDown;
    private CircularProgressView circularProgressView;
    private FrameLayout animSuccess;
    private FrameLayout circularRoot;
    private TextView TitleText;
    private TextView timeMsg;
    private TextView tips;

    @Override
    protected int getViewId() {
        return R.layout.fragment_idcard;
    }


    @Override
    protected void initView(View view) {
        tvCountDown = view.findViewById(R.id.tv_count_down);

        TitleText = (TextView) view.findViewById(R.id.TitleText);
        timeMsg = (TextView) view.findViewById(R.id.time_msg);
        tips = (TextView) view.findViewById(R.id.tips);


        circularProgressView = (CircularProgressView) view.findViewById(R.id.circularProgressView);

        circularRoot = (FrameLayout) view.findViewById(R.id.circular_root);


        animSuccess = (FrameLayout) view.findViewById(R.id.anim_success);
        animSuccess.setClipToOutline(true);
        animSuccess.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0
                        , view.getMeasuredWidth(),
                        view.getMeasuredWidth(),
                        view.getMeasuredWidth() / 2);
            }
        });
    }

    @Override
    protected void initData() {

    }

    public void setStop(){
        super.setStop();
        if(idCardPresent != null)
            idCardPresent.cancelAutoReading();
    }
    public void setResume(){
        super.setResume();
        if(idCardPresent != null)
            idCardPresent.startReadCard();
    }


    public void setIdCardServiceAidl(IDCardServiceAidl idCardServiceAidl) {
        if (idCardServiceAidl != null) {
            idCardPresent = new IDCardPresent(idCardServiceAidl,null, this);
        }

    }

    @Override
    protected void initAction() {
        if (idCardPresent != null && !MainActivity.isDebug) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    idCardPresent.startReadCard();
                }
            },3000);
//            idCardPresent.startReadCard();    //上面延时加载，防止跟音效冲突，导致音效卡顿
        }
        if (MainActivity.isDebug) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAnim();
                }
            },2000);
        }

        CountDownPresent.getInstance().setCountDownCallback(this);
        CountDownPresent.getInstance().setStartTime(120);
    }

    void goNext() {
       postDelayed(new Runnable() {
            @Override
            public void run() {
                replaceContent(FaceFragment.newInstance(new FaceFragment(), null, null));
            }
        }, 3000);
    }

    void showAnim() {
        circularProgressView.startAnim(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                TitleText.setText(R.string.idcard_scan_success);
                timeMsg.setText(R.string.idcard_scan_next);
                tvCountDown.setVisibility(View.GONE);
                animSuccess.setVisibility(View.VISIBLE);
                ((MainActivity)getActivity()).soundPresent.startIdcardSuccess();
                goNext();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onFail(String msg) {

    }

    @Override
    public void getCardData(IDCardInfo info, int code) {
        Log("身份证 ==" + info.toString());
        hotelUser.setIDCard(info.getIdCard());
        hotelUser.setName(info.getName());
        hotelUser.setIdcard_face(info.getImageAddress());
        timeMsg.post(new Runnable() {
            @Override
            public void run() {
                TitleText.setText(R.string.idcard_scan);
                tips.setVisibility(View.GONE);
                showAnim();
            }
        });

    }

    //add by mayflower on 19/8/6---------------------------
    @Override
    public void getMifareCardData(byte[] data, int code){

    }
    //-----------------------------------------------------

    public void setHotelUser(HotelUser hotelUser) {
        this.hotelUser = hotelUser;
    }



    @Override
    public void onTick(long millisUntilFinished) {
        tvCountDown.setText("" + millisUntilFinished);
    }

    @Override
    public void onSuccess() {
        setStop();
        showDialog();
    }

    CustomDialog dialog;

    void showDialog() {
        if (dialog == null) {
            dialog = Create.createDialog(getContext(), R.drawable.ic_card, getString(R.string.dialog_no_idcard), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    backTopFragment();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    setResume();
                    CountDownPresent.getInstance().setStartTime(60);
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

    @Override
    public void onDestroy() {
        if(idCardPresent != null)
            idCardPresent.cancelAutoReading();
        super.onDestroy();
    }
}
