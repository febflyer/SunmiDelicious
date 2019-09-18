package com.sunmi.sunmiK1Ext.utils;

import android.content.res.Resources;

/**
 * <pre>
 *     author : FaDai
 *     e-mail : i_fadai@163.com
 *     time   : 2017/12/04
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class ViewUtils {

    private static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;

    /**
     *   dp转换px，根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *   @param dp   dp值
     *   @return  转换后的px值
     */
    public static int dp2Px(int dp) {
        return Math.round(dp * DENSITY);
    }
}
