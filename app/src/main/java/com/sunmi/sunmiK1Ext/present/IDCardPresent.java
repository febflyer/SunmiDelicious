package com.sunmi.sunmiK1Ext.present;

import android.os.HandlerThread;
import android.os.RemoteException;

import com.sunmi.idcardservice.CardCallback;
import com.sunmi.idcardservice.IDCardInfo;
import com.sunmi.idcardservice.IDCardServiceAidl;
import com.sunmi.idcardservice.MiFareCardAidl;
import com.sunmi.sunmiK1Ext.utils.ByteUtils;

public class IDCardPresent {
    private IDCardServiceAidl idCardServiceAidl = null;     //身份证
    private MiFareCardAidl miFareCardAidl = null;           //非接，add by mayflower on 19/8/6
    IDCardCallBack idCardCallBack;

    public interface IDCardCallBack {
        void onFail(String msg);

        void getCardData(IDCardInfo info, int code);

        void getMifareCardData(byte[] data, int code);  //add by mayflower on 19/8/6
    }


    public IDCardPresent(IDCardServiceAidl mService, MiFareCardAidl miFareCardAidl,IDCardCallBack idCardCallBack) {
        this.idCardServiceAidl = mService;
        this.miFareCardAidl = miFareCardAidl;
        this.idCardCallBack = idCardCallBack;
    }


    public void startReadCard() {
        try {
            idCardServiceAidl.readCardAuto(cardCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void cancelAutoReading() {
        try {
            idCardServiceAidl.cancelAutoReading();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    CardCallback cardCallback = new CardCallback.Stub() {
        @Override
        public void getCardData(final IDCardInfo info, final int code) throws RemoteException {
            if (code == 10) {
                try {
                    idCardServiceAidl.cancelAutoReading();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                idCardCallBack.getCardData(info,code);
            } else if (code != 1 && code != -10) {
                idCardCallBack.onFail("身份证读取异常 ==" + code);
            }
        }
    };

    //add by mayflower on 19/8/6,for MiFareCard-----------------------------------------------------
    private boolean isThreadRunning = false;       //线程状态，为了安全终止线程

    public interface MifareCardCallBack{
        void onFail(String msg);
        void getMifareCardData(String data, int code);
    }

    public void stopReadMiCard(){
        isThreadRunning = false;
    }

    public void startReadMiCard(){
        if(isThreadRunning || miFareCardAidl == null)
            return;
        isThreadRunning = true;   //表示线程起来了
        new ReadMifareThread().start();
    }

    public class ReadMifareThread extends Thread{
        @Override
        public void run(){
            super.run();

            int ret = 0;
            byte[] snr = new byte[10];      //卡片UID
            byte[] cardtype = new byte[8];  //卡片类型，0AH  TypeA卡;  0BH  TypeB卡
            int mode = 0;       //认证模式 0 KEYA 模式 1 KEYB 模式
            int addr = 0;       //绝对地址 = 扇区号 * 4 + 块地址，地址0是厂家信息，已固化只读
            byte[] key = new byte[10];      //默认密码：FFFFFFFFFFFF，这几个如果想要设定，可以放到成员属性
            for(int i=0;i<6;i++){ key[i] = -1;}
            key[6] = 0;
            byte[] rdata = new byte[32];    //读卡数据

            while (isThreadRunning){
                try {
                    //寻卡，支持的卡片都能成功
                    ret = miFareCardAidl.rfCard(cardtype, snr);
                    if(ret != 0){ continue;}
                    //卡片UID
                    if(isThreadRunning)     //预防突然杀进程
                    {
                        stopReadMiCard();   //只读一次
                        idCardCallBack.getMifareCardData(snr, 10);
                    }

//                    //认证，密码正确才能成功认证
//                    ret = miFareCardAidl.rfAuthEntication(mode,addr,key);
//                    if(ret != 0){ continue;}
//
//                    //读卡，认证成功后才能成功读卡
//                    ret = miFareCardAidl.rfRead(addr,rdata);
//                    if(ret != 0){ continue;}
//                    //绝对地址读到的数据
//                    if(isThreadRunning)
//                        idCardCallBack.getMifareCardData(ByteUtils.bytes2HexStr(rdata,0,rdata.length), 10);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    ////////////////////////////////////////////----------------------------------------------------
}
