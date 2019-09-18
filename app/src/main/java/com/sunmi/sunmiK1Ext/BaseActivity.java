package com.sunmi.sunmiK1Ext;

import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener ,BaseFragment.OnFragmentInteractionListener {
    private final int container = R.id.container;

    private ArrayList<BaseFragment> fragments;// back fragment list.
    private BaseFragment fragment;// current fragment.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewId());
        initView();
        initData();
        initAction();

    }


    /**
     * replace the current fragment.
     *
     * @param fragment       the new fragment to shown.
     * @param addToBackStack if it can back.
     */
    public void addContent(BaseFragment fragment, boolean addToBackStack) {
        initFragments();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(container, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        } else {
            removePrevious();
        }

        ft.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();

        fragments.add(fragment);
        setFragment();
    }

    // use replace method to show fragment.
    public void replaceContent(BaseFragment fragment, boolean addToBackStack) {
        initFragments();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.setCustomAnimations( R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out );

        ft.replace(container, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        } else {
            removePrevious();
        }
        ft.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();

        fragments.add(fragment);
        setFragment();
    }

    public void backTopFragment() {
        if (fragments != null && fragments.size() > 1) {
            removeContent();
            backTopFragment();
        }
    }
    public void clearFragment(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (BaseFragment baseFragment : fragments) {
            ft.remove(baseFragment);
        }
        ft.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
        fragments.clear();
    }
    /**
     * set current fragment.
     */
    private void setFragment() {
        if (fragments != null && fragments.size() > 0) {
            fragment = fragments.get(fragments.size() - 1);
        } else {
            fragment = null;
        }
    }

    /**
     * get the current fragment.
     *
     * @return current fragment
     */
    public BaseFragment getFirstFragment() {
        return fragment;
    }

    /**
     * get amount of fragment.
     *
     * @return amount of fragment
     */
    public int getFragmentNum() {
        return fragments != null ? fragments.size() : 0;
    }

    /**
     * clear fragment list
     */
    protected void clearFragments() {
        if (fragments != null) {
            fragments.clear();
        }
    }

    /**
     * remove previous fragment
     */
    private void removePrevious() {
        if (fragments != null && fragments.size() > 0) {
            fragments.remove(fragments.size() - 1);
        }
    }

    /**
     * init fragment list.
     */
    private void initFragments() {
        if (fragments == null) {
            fragments = new ArrayList<>();
        }
    }

    /**
     * remove current fragment and back to front fragment.
     */
    public void removeContent() {
        removePrevious();
        setFragment();
        getSupportFragmentManager().popBackStackImmediate();
    }

    /**
     * remove all fragment from back stack.
     */
    protected void removeAllStackFragment() {
        clearFragments();
        setFragment();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    protected abstract int getViewId();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initAction();

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }


    public void Log(String msg) {
        Log.e("@@@", msg);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

}
