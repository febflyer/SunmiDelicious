package com.sunmi.sunmiK1Ext.fragment;

import android.view.View;
import android.widget.TextView;

import com.sunmi.sunmiK1Ext.BaseFragment;
import com.sunmi.sunmiK1Ext.R;
import com.sunmi.sunmiK1Ext.view.ParticleSmasher;
import com.sunmi.sunmiK1Ext.view.SmashAnimator;

public class MainFragment extends BaseFragment implements View.OnClickListener {

    private TextView hotelStayIn;
    private ParticleSmasher mSmasher;   //按钮爆炸效果

    @Override
    protected int getViewId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(View view) {
        hotelStayIn = (TextView) view.findViewById(R.id.hotel_stay_in);

    }

    @Override
    protected void initData() {
        mSmasher = new ParticleSmasher(getActivity());
    }

    @Override
    protected void initAction() {
        hotelStayIn.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.hotel_stay_in:
                mSmasher.with(v)
                        .setStartDelay(10)
                        .setVerticalMultiple(8)
                        .addAnimatorListener(new SmashAnimator.OnAnimatorListener() {
                            @Override
                            public void onAnimatorEnd(){
                                super.onAnimatorEnd();
                                IDCardFragment idCardFragment = (IDCardFragment) IDCardFragment.newInstance(new IDCardFragment(), null, null);
                                replaceContent(idCardFragment, false);

                                postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSmasher.reShowView(v);
                                    }
                                },1000);
                            }
                        }).start();

//                IDCardFragment idCardFragment = (IDCardFragment) IDCardFragment.newInstance(new IDCardFragment(), null, null);
//                replaceContent(idCardFragment, false);
                break;
        }
    }
}
