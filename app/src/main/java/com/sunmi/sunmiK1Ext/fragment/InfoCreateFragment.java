package com.sunmi.sunmiK1Ext.fragment;

import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.sunmi.sunmiK1Ext.BaseFragment;
import com.sunmi.sunmiK1Ext.R;
import com.sunmi.sunmiK1Ext.utils.TimeUtils;
import com.sunmi.sunmiK1Ext.bean.HotelUser;
import com.sunmi.sunmiK1Ext.model.RoomModel;
import com.sunmi.sunmiK1Ext.present.CountDownPresent;

public class InfoCreateFragment extends BaseFragment implements CountDownPresent.CountDownCallback {
    private TextView tvCountDown;
    private TextView timeMsg;
    private TextView orderName;
    private TextView orderDate;
    private TextView orderType;
    private TextView orderMoney;
    private TextView orderNumber;
    private TextView goNext;
    private TextView recharge;      //充值
    private HotelUser hotelUser;
    public void setHotelUser(HotelUser hotelUser) {
        this.hotelUser = hotelUser;
    }
    @Override
    protected int getViewId() {
        return R.layout.fragment_info_create;
    }

    @Override
    protected void initView(View view) {

        tvCountDown = (TextView) view.findViewById(R.id.tv_count_down);
        timeMsg = (TextView) view.findViewById(R.id.time_msg);
        orderName = (TextView) view.findViewById(R.id.order_name);
        orderDate = (TextView) view.findViewById(R.id.order_date);
        orderType = (TextView) view.findViewById(R.id.order_type);
        orderMoney = (TextView) view.findViewById(R.id.order_money);
        orderNumber = (TextView) view.findViewById(R.id.order_number);
        goNext = (TextView) view.findViewById(R.id.goNext);
        recharge = (TextView) view.findViewById(R.id.recharge);
    }



    @Override
    protected void initData() {
        hotelUser.setMoneySum(hotelUser.getMoneySum());

        orderName.setText(hotelUser.getName());
        orderDate.setText(TimeUtils.getNowDateShort());
        orderType.setText(RoomModel.getLevel());
        orderMoney.setText(hotelUser.getMoneySum()+"");
        orderNumber.setText(hotelUser.getMiCardNo());
    }

    @Override
    protected void initAction() {
        goNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRecharge();   //给钱包充点电吧
            }
        });
        CountDownPresent.getInstance().setCountDownCallback(this);
        CountDownPresent.getInstance().setStartTime(30);
    }

    private void goNext() {
        replaceContent((InfoFragment) InfoFragment.newInstance(new InfoFragment(), orderType.getText().toString(), null));
    }
    private void goRecharge(){
        replaceContent(RechargeFragment.newInstance(new RechargeFragment(), null, null));
    }

    @Override
    public void onTick(long millisUntilFinished) {
        tvCountDown.setText(""+millisUntilFinished);
    }

    @Override
    public void onSuccess() {
        backTopFragment();
    }
}
