package com.sunmi.sunmiK1Ext.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    public static String DATE_TO_STRING_SHORT_PATTERN = "yyyy/MM/dd";

    /**
     * 获取现在时间
     *
     * @return返回短时间格式 yyyy-MM-dd
     */
    public static String getNowDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TO_STRING_SHORT_PATTERN);
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return dateString;
    }


    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);//+1今天的时间加一天
        date = calendar.getTime();
        return date;
    }

    public static String formatTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TO_STRING_SHORT_PATTERN);
        String dateString = formatter.format(date);
        return dateString;
    }
}
