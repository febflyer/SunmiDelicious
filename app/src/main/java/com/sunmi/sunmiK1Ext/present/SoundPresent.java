package com.sunmi.sunmiK1Ext.present;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.provider.MediaStore;

import com.sunmi.sunmiK1Ext.R;

import java.security.PublicKey;

public class SoundPresent{

    SoundPool soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);

    public SoundPresent(Context context) {
        soundPool.load(context, R.raw.check, 1);// 1
        soundPool.load(context, R.raw.face, 1);// 1
        soundPool.load(context, R.raw.gun, 1);// 1
        soundPool.load(context, R.raw.idcard, 1);// 1
        soundPool.load(context, R.raw.idcard_success, 1);// 1
        soundPool.load(context, R.raw.phone, 1);// 1
        soundPool.load(context,R.raw.mifare,1);
        soundPool.load(context,R.raw.recharge,1);
    }

    public void startIdcard() {
        soundPool.play(4, 1, 1, 10, 0, 1); }

    public void startIdcardSuccess() {
        soundPool.play(5, 1, 1, 10, 0, 1);
    }

    public void startFace() {
        soundPool.play(2, 1, 1, 10, 0, 1);
    }

    public void startCheck() {
        soundPool.play(1, 1, 1, 10, 0, 1);
    }

    public void startPhone() {
        soundPool.play(6, 1, 1, 10, 0, 1);
    }

    public void startGun(){
        soundPool.play(3, 1, 1, 10, 0, 1);
    }

    public void startMifareCard(){ soundPool.play(7,4,4,10,0,1); }

    public void startRecharge(){ soundPool.play(8,1,1,10,0,1); }

    public void close(){
        soundPool.release();
    }

}
