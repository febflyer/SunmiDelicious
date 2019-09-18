package com.sunmi.sunmiK1Ext.present;

import android.os.RemoteException;

import com.sunmi.statuslampmanager.IStateLamp;

public class StateLampPresent {

    private IStateLamp stateLamp = null;

    public StateLampPresent() {
    }

    public void setStateLamp(IStateLamp stateLamp) {
        this.stateLamp = stateLamp;
    }

    public void startIdLed(){
        controlLamp(0,"Led-1");
    }

    public void startCameraLed(){
        controlLamp(0,"Led-4");
    }

    public void startCardLed(){
        controlLamp(0,"Led-2");
    }

    public void NoCardLed(){
        controlLamp(0,"Led-3");
    }
    /**
     * 功能：控制单个状态灯
     * 参数：
     * [in]status       状态，0亮，1灭
     * [in]lamp         Led灯，参数：
     *                      "Led-1"
     *                      "Led-2"
     *                      "Led-3"
     *                      "Led-4"
     *                      "Led-5"
     *                      "Led-6"
     * 返回值：无
     */
    private void controlLamp(int status, String lamp) {
        if (stateLamp == null) {
            return;
        }
        try {
            stateLamp.controlLamp(status, lamp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能：控制单个状态灯循环 显示
     * 参数：
     * [in]status       状态，0开始循环，1停止循环
     * [in]lightTime    报警灯亮时间，单位：毫秒(ms)
     * [in]putoutTime   报警灯灭时间，单位：毫秒(ms)
     * [in]lamp         Led灯，参数：
     * "Led-1"
     * "Led-2"
     * "Led-3"
     * "Led-4"
     * "Led-5"
     * "Led-6"
     * 返回值：无
     */
    private void controlLampForLoop(int status, long lightTime, long putoutTime, String lamp) {
        try {
            stateLamp.controlLampForLoop(status, lightTime, putoutTime, lamp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能：关闭所有状态灯
     */
    public void closeAllLamp() {
        if (stateLamp == null) {
            return;
        }
        try {
            stateLamp.closeAllLamp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
