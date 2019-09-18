package com.sunmi.sunmiK1Ext.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.sunmiK1Ext.BaseFragment;
import com.sunmi.sunmiK1Ext.MainActivity;
import com.sunmi.sunmiK1Ext.R;

import java.io.File;

public class PhotoFragment extends BaseFragment {
    private TextView TitleText;
    private TextView msg;
    private FrameLayout photoToPhoto;
    private ImageView ivIdcard;
    private ImageView ivPhoto;
    private ImageView ivSuccess;

    @Override
    protected int getViewId() {
        return R.layout.fragment_photo;
    }

    @Override
    protected void initView(View view) {

        TitleText = (TextView) view.findViewById(R.id.TitleText);
        msg = (TextView) view.findViewById(R.id.msg);
        photoToPhoto = (FrameLayout) view.findViewById(R.id.photoToPhoto);
        ivIdcard = (ImageView) view.findViewById(R.id.iv_idcard);
        ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);
        ivSuccess = (ImageView) view.findViewById(R.id.iv_success);

        ivIdcard.setClipToOutline(true);
        ivPhoto.setClipToOutline(true);

        ivIdcard.setOutlineProvider(getProvider());
        ivPhoto.setOutlineProvider(getProvider());

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
    protected void initData() {
        TitleText.setText(getString(R.string.photo_check));
        ivIdcard.setImageBitmap(getDiskBitmap(((MainActivity) getActivity()).hotelUser.getIdcard_face()));
        ivPhoto.setImageBitmap(((MainActivity) getActivity()).hotelUser.getPhoto_face());

    }

    @Override
    protected void initAction() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                TitleText.setText(getString(R.string.photo_check_success));
                msg.setVisibility(View.VISIBLE);
                ivSuccess.setVisibility(View.VISIBLE);
                photoToPhoto.setVisibility(View.GONE);
                goNext();
            }
        }, 5000);
    }

    private void goNext() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
//                PhoneFragment phoneFragment = (PhoneFragment) PhoneFragment.newInstance(new PhoneFragment(), null, null);
//                replaceContent(phoneFragment);
                //changed by mayflower on 19/8/7
                MifareCardFragment mifareCardFragment = (MifareCardFragment) MifareCardFragment.newInstance(new MifareCardFragment(), null, null);
                replaceContent(mifareCardFragment);
            }
        }, 5000);
    }


    /**
     * 加载本地图片
     * http://bbs.3gstdy.com
     *
     * @param pathString
     * @return
     */
    private Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }


        return bitmap;
    }

}
