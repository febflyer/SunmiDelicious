package com.sunmi.sunmiK1Ext.fragment;

/**
 * created by mayflower on 19/8/6
 * get me at jiangli@sunmi.com
 * */
import android.animation.Animator;
import android.graphics.Outline;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sunmi.idcardservice.IDCardInfo;
import com.sunmi.idcardservice.IDCardServiceAidl;
import com.sunmi.idcardservice.MiFareCardAidl;
import com.sunmi.sunmiK1Ext.BaseFragment;
import com.sunmi.sunmiK1Ext.MainActivity;
import com.sunmi.sunmiK1Ext.R;
import com.sunmi.sunmiK1Ext.bean.HotelUser;
import com.sunmi.sunmiK1Ext.dialog.Create;
import com.sunmi.sunmiK1Ext.dialog.CustomDialog;
import com.sunmi.sunmiK1Ext.present.CountDownPresent;
import com.sunmi.sunmiK1Ext.present.IDCardPresent;
import com.sunmi.sunmiK1Ext.utils.ByteUtils;
import com.sunmi.sunmiK1Ext.view.CircularProgressView;

public class MifareCardFragment extends BaseFragment implements IDCardPresent.IDCardCallBack, CountDownPresent.CountDownCallback,View.OnClickListener{
    IDCardPresent mifareCardPresenter;      //非接跟身份证是同一个服务

    private HotelUser hotelUser;
    private TextView tvCountDown;
    private CircularProgressView circularProgressView;
    private FrameLayout animSuccess;
    private FrameLayout circularRoot;
    private TextView TitleText;
    private TextView timeMsg;
    private TextView tips;
    private TextView memberNoCard;

    @Override
    protected int getViewId() {
        return R.layout.fragment_mifarecard;
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
        memberNoCard = (TextView)view.findViewById(R.id.member_no_card);
    }

    @Override
    protected void initData() {

    }

    public void setStop(){
        super.setStop();
        if(mifareCardPresenter != null)
            mifareCardPresenter.stopReadMiCard();
    }
    public void setResume(){
        super.setResume();
        if(mifareCardPresenter != null)
            mifareCardPresenter.startReadMiCard();
    }

    public void setMifareCardServiceAidl(MiFareCardAidl miFareCardAidl) {
        if (miFareCardAidl != null) {
            mifareCardPresenter = new IDCardPresent(null,miFareCardAidl, this);
        }

    }

    @Override
    protected void initAction() {
        if (mifareCardPresenter != null && !MainActivity.isDebug) {
            mifareCardPresenter.startReadMiCard();
        }
        if (MainActivity.isDebug) {
            memberNoCard.setVisibility(View.GONE);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAnim();
                }
            },2000);
        }

        CountDownPresent.getInstance().setCountDownCallback(this);
        CountDownPresent.getInstance().setStartTime(120);

        memberNoCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.member_no_card:
                //电子会员，无会员卡刷
                String cardNo = SystemClock.uptimeMillis()+"";
                hotelUser.setMiCardNo(cardNo);
                showAnim();
        }
    }

    void goNext() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
//                replaceContent(PhoneFragment.newInstance(new PhoneFragment(), null, null));
                replaceContent(InfoCreateFragment.newInstance(new InfoCreateFragment(), null, null));
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
    public void onFail(String msg){ }
    @Override
    public void getCardData(IDCardInfo info, int code){ }
    @Override
    public void getMifareCardData(byte[] data, int code){
        Log("非接卡 ==" + data);
        //因为硬件底层协议：低字节走在前 高字节在后。所以要将字节反转回来
        String strData = ByteUtils.bytes2HexStr(data,0,data.length);

        strData = ByteUtils.reverseHexStr(strData);

        strData = "" + ByteUtils.hexStr2decimal(strData);

        hotelUser.setMiCardNo(strData);     //到这里，就是真实的卡号了

        timeMsg.post(new Runnable() {
            @Override
            public void run() {
                TitleText.setText(R.string.idcard_scan);
                tips.setVisibility(View.GONE);
                showAnim();
            }
        });
    }

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
        if(mifareCardPresenter != null)
            mifareCardPresenter.stopReadMiCard();
        super.onDestroy();
    }
}
