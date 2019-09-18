package com.sunmi.sunmiK1Ext.model;

import android.content.Context;

import com.sunmi.sunmiK1Ext.R;

import java.util.Random;

public class RoomModel {

    public static final int one = R.string.room_one;

    public static final int two = R.string.room_two;

    public static final int all = R.string.room_all;

    ///add by mayflower on 19/8/15
    public static final int lvOne = R.string.lv_one;
    public static final int lvTwo = R.string.lv_two;
    public static final int lvAll = R.string.lv_all;

    public static int getLevel(){
        Random random = new Random();
        switch (random.nextInt(4)) {
            case 1:
                return lvOne;
            case 2:
                return lvTwo;
            default:
                return lvAll;
        }
    }

    ///

    public static int getRoom() {
        Random random = new Random();
        switch (random.nextInt(4)) {
            case 1:
                return one;
            case 2:
                return two;
            default:
                return all;
        }
    }

    public static int getRoom(Context context, String room) {
        if (context.getString(one).equals(room)) {
            return one;
        } else if (context.getString(two).equals(room)) {
            return two;

        }
        return all;

    }

}
