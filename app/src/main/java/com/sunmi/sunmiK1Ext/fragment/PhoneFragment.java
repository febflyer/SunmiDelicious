package com.sunmi.sunmiK1Ext.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.sunmiK1Ext.BaseFragment;
import com.sunmi.sunmiK1Ext.R;
import com.sunmi.sunmiK1Ext.present.CountDownPresent;

public class PhoneFragment extends BaseFragment implements TextWatcher, CountDownPresent.CountDownCallback, View.OnClickListener {

    private TextView phone;
    private TextView tvCountDown;

    private LinearLayout phoneKey;
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

    @Override
    protected int getViewId() {
        return R.layout.fragment_phone;
    }

    @Override
    protected void initView(View view) {
        phone = (TextView) view.findViewById(R.id.phone);
        phone.addTextChangedListener(this);

        tvCountDown = (TextView) view.findViewById(R.id.tv_count_down);


        phoneKey = (LinearLayout) view.findViewById(R.id.phone_key);
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

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {
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

    }


    @Override
    public void onTick(long millisUntilFinished) {
        tvCountDown.setText("" + millisUntilFinished);
    }

    @Override
    public void onSuccess() {
        backTopFragment();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 11) {
            confirm.setEnabled(true);
        } else {
            confirm.setEnabled(false);
        }
    }


    private void goNext() {
        replaceContent(InfoCreateFragment.newInstance(new InfoCreateFragment(), phone.getText().toString(), null));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                goNext();
                break;
            case R.id.clear:
                phone.setText("");
                break;
            case R.id.delete:
                String str = phone.getText().toString();
                if (!"".equals(str)) {//判断输入框不为空，执行删除
                    str = str.substring(0, str.length() - 1);
                    phone.setText(str);
                }
                break;
            default:
                String input = (String) v.getTag();
                phone.setText(phone.getText().toString() + input);
                break;
        }

    }
}
