package com.sunmi.sunmiK1Ext.dialog;

import android.content.Context;
import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.sunmiK1Ext.R;


/**
 * Created by xushengfu on 2018/2/3.
 */

public class Create {

    public static CustomDialog createDialog(Context context, int icon, String message, View.OnClickListener onClickListener, CustomDialog.DialogOver dialogOver) {

        CustomDialog dialog = new CustomDialog(context);
        LinearLayout dialogRoot = dialog.findViewById(R.id.dialog_root);

        dialogRoot.setClipToOutline(true);
        dialogRoot.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 64);
            }
        });

        ImageView imageView = dialog.findViewById(R.id.dialog_icon);
        TextView tvMsg = (TextView) dialog.findViewById(R.id.dialog_msg);
        TextView btn = (TextView) dialog.findViewById(R.id.dialog_btn);

        tvMsg.setText(message);
        imageView.setImageResource(icon);
        btn.setOnClickListener(onClickListener);
        dialog.setDialogOver(dialogOver);

        return dialog;
    }

    public static CustomDialog createDialog(Context context, int icon, String message, View.OnClickListener left, View.OnClickListener right, CustomDialog.DialogOver dialogOver) {

        CustomDialog dialog = new CustomDialog(context);

        LinearLayout dialogRoot = dialog.findViewById(R.id.dialog_root);

        dialogRoot.setClipToOutline(true);
        dialogRoot.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 64);
            }
        });


        ImageView imageView = dialog.findViewById(R.id.dialog_icon);
        TextView tvMsg = (TextView) dialog.findViewById(R.id.dialog_msg);
        TextView btn = (TextView) dialog.findViewById(R.id.dialog_btn);
        TextView btn1 = (TextView) dialog.findViewById(R.id.dialog_btn1);
        TextView btn2 = (TextView) dialog.findViewById(R.id.dialog_btn2);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.dialog_2btn);

        tvMsg.setText(message);
        imageView.setImageResource(icon);
        btn1.setOnClickListener(left);
        btn2.setOnClickListener(right);
        dialog.setDialogOver(dialogOver);

        btn.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        return dialog;
    }

    public static CustomDialog createDialog(Context context, int icon, String message, String leftMsg, View.OnClickListener left, String rightMsg, View.OnClickListener right, CustomDialog.DialogOver dialogOver) {

        CustomDialog dialog = new CustomDialog(context);

        LinearLayout dialogRoot = dialog.findViewById(R.id.dialog_root);

        dialogRoot.setClipToOutline(true);
        dialogRoot.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 64);
            }
        });


        ImageView imageView = dialog.findViewById(R.id.dialog_icon);
        TextView tvMsg = (TextView) dialog.findViewById(R.id.dialog_msg);
        TextView btn = (TextView) dialog.findViewById(R.id.dialog_btn);
        TextView btn1 = (TextView) dialog.findViewById(R.id.dialog_btn1);
        TextView btn2 = (TextView) dialog.findViewById(R.id.dialog_btn2);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.dialog_2btn);

        tvMsg.setText(message);
        btn1.setText(leftMsg);
        btn2.setText(rightMsg);

        imageView.setImageResource(icon);
        btn1.setOnClickListener(left);
        btn2.setOnClickListener(right);
        dialog.setDialogOver(dialogOver);

        btn.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        return dialog;
    }

}
