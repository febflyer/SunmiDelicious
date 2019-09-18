package com.sunmi.sunmiK1Ext.present;

import android.os.RemoteException;

import com.sunmi.mifarecard.IMifareCard;

public class MiCardPresent {
    private IMifareCard mService = null;



    MiCardCallBack cardCallBack;

    int nRet;


    public interface MiCardCallBack {
        void onFail(String msg);

        void onGetCardNo(byte[] cardNo);

        void onGetCardInfo(byte[] cardInfo);

        void onMicardSuccess();

    }

    public MiCardPresent(IMifareCard mService, MiCardCallBack cardCallBack) {
        this.mService = mService;
        this.cardCallBack = cardCallBack;
    }
    public void setCardCallBack(MiCardCallBack cardCallBack) {
        this.cardCallBack = cardCallBack;
    }
    public void onCardIssuingAndCardReader() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //移卡
                    byte[] SendBuf = new byte[3];
                    SendBuf[0] = 0x46;
                    SendBuf[1] = 0x43;
                    SendBuf[2] = 0x37;
                    nRet = mService.sendCmd(SendBuf, 3);
                    if (nRet != 0) {
                        showToast("设备发卡到读卡位置异常，退出发卡流程");
                        return;
                    }

                    int TimeOut = 0;
                    byte[] StateInfo = new byte[20];

                    //直到卡移动到正确的位置，才进行下一步
                    while (TimeOut < 150) {
                        TimeOut++;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        nRet = mService.sensorQuery(StateInfo);
                        if (nRet != 0) {
                        }

                        if ((StateInfo[3] & 0x03) == 0x03) {
                            break;
                        }
                    }
                    if ((StateInfo[3] & 0x03) != 0x03) {
                        showToast("发卡失败，退出发卡流程");
                        return;
                    }

//            //寻卡
//            for (int i = 0; i < 3; i++) {
//                nRet = mService.detectS70Card();
//                if (nRet == 0) {
//                    break;
//                }
//            }
//            if (nRet != 0) {
//                showToast("寻卡失败，退出发卡流程");
//                return;
//            }
//
//            //获取卡号
//            byte[] cardNo = new byte[32];//卡号
//            for (int i = 0; i < 3; i++) {
//                nRet = mService.getS70CardID(cardNo);
//                if (nRet == 0) {
//                    break;
//                }
//            }
//            if (nRet != 0) {
//                showToast("获取卡号失败，退出发卡流程");
//                return;
//            }
                    if (cardCallBack!=null) {
                        cardCallBack.onGetCardNo(SendBuf);
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    public void giveCard() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {

                    //弹卡
                    byte[] SendBuf = new byte[3];

                    SendBuf[0] = 0x46;
                    SendBuf[1] = 0x43;
                    SendBuf[2] = 0x34;
                    nRet = mService.sendCmd(SendBuf, 3);

                    if (nRet != 0) {
                        showToast("弹卡失败，退出发卡流程");
                        return;
                    }
                    int TimeOut = 0;
                    byte[] StateInfo = new byte[20];

                    while (TimeOut < 50) {
                        TimeOut++;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        nRet = mService.sensorQuery(StateInfo);
                        if (nRet != 0) {
                            showToast("状态查询异常，退出发卡流程");
                            return;
                        }
                        if ((StateInfo[3] & 0x01) == 0x01) {
                            break;
                        }
                    }
                    if ((StateInfo[3] & 0x01) != 0x01) {
                        showToast("退卡失败，退出发卡流程");
                        return;
                    }
                    if (cardCallBack!=null) {
                        cardCallBack.onMicardSuccess();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    void showToast(String msg) {
        if (cardCallBack!=null) {
            cardCallBack.onFail(msg);
        }
    }
}
