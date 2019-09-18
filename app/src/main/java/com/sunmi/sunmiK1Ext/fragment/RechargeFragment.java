package com.sunmi.sunmiK1Ext.fragment;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.idcardservice.IDCardServiceAidl;
import com.sunmi.sunmiK1Ext.BaseFragment;
import com.sunmi.sunmiK1Ext.R;
import com.sunmi.sunmiK1Ext.bean.HotelUser;
import com.sunmi.sunmiK1Ext.present.CountDownPresent;
import com.sunmi.sunmiK1Ext.present.IDCardPresent;
import com.sunmi.sunmiK1Ext.present.ScannerPresent;

public class RechargeFragment extends BaseFragment implements ScannerPresent.OnDataReceiveListener, CountDownPresent.CountDownCallback,View.OnClickListener{
    ScannerPresent scannerPresent;
    private HotelUser hotelUser;

    private TextView rechargeNum;   //默认258
    private TextView tvCountDown;

    private TextView one;
    private TextView two;
    private TextView three;
    private TextView four;
    private TextView five;
    private TextView six;
    private TextView seven;
    private TextView eight;
    private TextView nine;
    private TextView clear;
    private TextView zero;
    private TextView delete;

    private TextView confirm;
    private TextView cancel;

    @Override
    protected int getViewId(){ return R.layout.fragment_recharge; }

    @Override
    protected void initView(View view){
        rechargeNum = (TextView) view.findViewById(R.id.rec_num);
        tvCountDown = (TextView) view.findViewById(R.id.tv_count_down);

        one = (TextView) view.findViewById(R.id.one);
        two = (TextView) view.findViewById(R.id.two);
        three = (TextView) view.findViewById(R.id.three);
        four = (TextView) view.findViewById(R.id.four);
        five = (TextView) view.findViewById(R.id.five);
        six = (TextView) view.findViewById(R.id.six);
        seven = (TextView) view.findViewById(R.id.seven);
        eight = (TextView) view.findViewById(R.id.eight);
        nine = (TextView) view.findViewById(R.id.nine);
        clear = (TextView) view.findViewById(R.id.clear);
        zero = (TextView) view.findViewById(R.id.zero);
        delete = (TextView) view.findViewById(R.id.delete);

        confirm = (TextView) view.findViewById(R.id.confirm);
        cancel = (TextView) view.findViewById(R.id.cancel);
    }

    @Override
    protected void initData(){}

    @Override
    protected void initAction(){
        CountDownPresent.getInstance().setCountDownCallback(this);
        CountDownPresent.getInstance().setStartTime(60);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        clear.setOnClickListener(this);
        zero.setOnClickListener(this);
        delete.setOnClickListener(this);

        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        tvCountDown.setText("" + millisUntilFinished);
    }

    @Override
    public void onSuccess() {
        backTopFragment();
    }

    private void goNext() {
        replaceContent(InfoCreateFragment.newInstance(new InfoCreateFragment(), rechargeNum.getText().toString(), null));
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.confirm:
                confirm.setVisibility(View.GONE);
                //开始扫码

                break;
            case R.id.cancel:
                //哼，小气
                goNext();
                break;
            case R.id.clear:
                rechargeNum.setText("");
                break;
            case R.id.delete:
                String str = rechargeNum.getText().toString();
                if (!"".equals(str)) {//判断输入框不为空，执行删除
                    str = str.substring(0, str.length() - 1);
                    rechargeNum.setText(str);
                }
                break;
            default:
                String input = (String) v.getTag();
                rechargeNum.setText(rechargeNum.getText().toString() + input);
                break;
        }
    }

    @Override
    public void onDataReceive(String data){
        scannerPresent.stop();
        String str = rechargeNum.getText().toString();
        if (!"".equals(str))
            hotelUser.setMoneySum(hotelUser.getMoneySum() + Integer.parseInt(rechargeNum.getText().toString()));
        else
            hotelUser.setMoneySum(hotelUser.getMoneySum() + 258);
        goNext();
    }

    @Override
    public void onCmdFail(){
        Log.e("RechargeFragment","connnect scanner fail.");
    }

    public void setHotelUser(HotelUser hotelUser) {
        this.hotelUser = hotelUser;
    }

    public void setScannerServiceAidl(Context context) {
        if (context != null) {
            scannerPresent =  new ScannerPresent(context,this);
            scannerPresent.start();
        }
    }
}
