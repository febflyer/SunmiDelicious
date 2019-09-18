package com.sunmi.sunmiK1Ext;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.extprinterservice.ExtPrinterService;
import com.sunmi.idcardservice.MiFareCardAidl;
import com.sunmi.sunmiK1Ext.bean.HotelUser;
import com.sunmi.sunmiK1Ext.dialog.Create;
import com.sunmi.sunmiK1Ext.dialog.CustomDialog;
import com.sunmi.sunmiK1Ext.fragment.FaceFragment;
import com.sunmi.sunmiK1Ext.fragment.IDCardFragment;
import com.sunmi.sunmiK1Ext.fragment.InfoCreateFragment;
import com.sunmi.sunmiK1Ext.fragment.InfoFragment;
import com.sunmi.sunmiK1Ext.fragment.MainFragment;
import com.sunmi.sunmiK1Ext.fragment.MifareCardFragment;
import com.sunmi.sunmiK1Ext.fragment.PhoneFragment;
import com.sunmi.sunmiK1Ext.fragment.RechargeFragment;
import com.sunmi.sunmiK1Ext.present.CountDownPresent;
import com.sunmi.sunmiK1Ext.present.ScannerPresent;
import com.sunmi.sunmiK1Ext.present.SoundPresent;
import com.sunmi.sunmiK1Ext.present.StateLampPresent;
import com.sunmi.idcardservice.IDCardServiceAidl;
import com.sunmi.mifarecard.IMifareCard;
import com.sunmi.statuslampmanager.IStateLamp;
import com.sunmi.sunmiK1Ext.view.DecentBanner;
import com.sunmi.sunmiK1Ext.view.MovingImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends com.sunmi.sunmiK1Ext.BaseActivity {
    private ImageView goHome;
    private ImageView rootBg;
    private ImageView process;
    private TextView tips;
    public static boolean isDebug = false;

    public IMifareCard sendCardServiceAidl = null;         //发卡器（里边儿带了个内部非接读卡器）
    public IDCardServiceAidl idCardServiceAidl = null;     //身份证（包含非接）
    public MiFareCardAidl miFareCardServiceAidl = null;    //非接
    private IStateLamp lampServiceAidl = null;             //灯
    private ExtPrinterService extPrinterServiceAidl = null;//打印机
    private Context scannerServiceBroadcast = null;        //串口扫码器,调用的是系统广播

    MainFragment mainFragment;

    public HotelUser hotelUser = new HotelUser();
    public CountDownPresent countDownPresent;
    public SoundPresent soundPresent;

    public StateLampPresent stateLampPresent;   //灯，后续取消

    ObjectAnimator showBg = new ObjectAnimator();
    ObjectAnimator hiddenBg = new ObjectAnimator();

//    MovingImageView image;      //晃动的背景图，不做回调的话，这里就不用定义了，xml里面直接用MovingImageView类型空间就行了
    private DecentBanner decentBanner;      //轮播的背景图
    private List<View> views;
    private List<String> titles;

    boolean isOk, isProcess;
    //以下服务分别是：补光灯，身份证，非接卡，发卡器，打印机，串口扫码器，服务连接成功才加载相应演示界面。
    public static boolean isServiceOK_Lamp,     isServiceOK_IDCard,     isServiceOK_MifareCard,
            isServiceOK_SendCard,   isServiceOK_Printer,    isServiceOK_Scanner;

    @Override
    protected int getViewId() {
        return com.sunmi.sunmiK1Ext.R.layout.activity_main;
    }

    @Override
    protected void initView() {
        goHome = findViewById(com.sunmi.sunmiK1Ext.R.id.go_home);
        rootBg = (ImageView) findViewById(com.sunmi.sunmiK1Ext.R.id.rootBg);
        process = (ImageView) findViewById(com.sunmi.sunmiK1Ext.R.id.process);
        tips = (TextView) findViewById(com.sunmi.sunmiK1Ext.R.id.tips);
    }

    @Override
    protected void initData() {
        stateLampPresent = new StateLampPresent();
        mainFragment = new MainFragment();
        addContent(MainFragment.newInstance(mainFragment, null, null), false);
    }

    @Override
    protected void initAction() {
        isOk = true;
        connectService_Lamp();          //灯
        connectService_CardReader();    //身份证、非接阅读器
                                        //非接是在connectService_IDCard中一起的
        connectService_CardSender();    //发卡器
        connectService_Printer();       //打印机
        connectService_Scanner();       //扫码器，是通过系统广播连接，直接放在present里面了

        goHome.setOnClickListener(this);

        countDownPresent = CountDownPresent.getInstance();
        soundPresent = new SoundPresent(this);

        showBg.setDuration(getResources().getInteger(com.sunmi.sunmiK1Ext.R.integer.fragment_replace_time));
        showBg.setFloatValues(0.0f, 1.0f);
//        showBg.setTarget(rootBg);
        showBg.setTarget(decentBanner);
        showBg.setPropertyName("alpha");

        hiddenBg.setDuration(getResources().getInteger(com.sunmi.sunmiK1Ext.R.integer.fragment_replace_time));
        hiddenBg.setFloatValues(1.0f, 0.0f);
//        hiddenBg.setTarget(rootBg);
        hiddenBg.setTarget(decentBanner);   //这个设置给decentBanner好像不管用
        hiddenBg.setPropertyName("alpha");

        findViewById(com.sunmi.sunmiK1Ext.R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDebug = !isDebug;
                findViewById(com.sunmi.sunmiK1Ext.R.id.logo).setAlpha(isDebug ? 0.5f : 1f);
            }
        });

        //轮播图test--------------------------------------
        decentBanner = (DecentBanner) findViewById(R.id.decent_banner);
        View view1 = getLayoutInflater().inflate(R.layout.banner_popular_layout, null);
        View view2 = getLayoutInflater().inflate(R.layout.banner_daily_layout, null);
        View view3 = getLayoutInflater().inflate(R.layout.banner_recommend_layout, null);
        views = new ArrayList<>();
        views.add(view1);
        views.add(view2);
        views.add(view3);

        titles = new ArrayList<>();
        titles.add("POPULAR");
        titles.add("IMAGE");
        titles.add("RECOMMEND");
//        decentBanner.start(views, titles, 2, 500, R.drawable.ic_logo);
        decentBanner.start(views, titles, 5, 1000, null);
        //------------------------------------------------
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.sunmi.sunmiK1Ext.R.id.go_home:
                showDialog();
                break;
        }

    }

    CustomDialog dialog;

    void showDialog() {
        stopProcess();
        if (dialog == null) {
            dialog = Create.createDialog(this, com.sunmi.sunmiK1Ext.R.drawable.ic_empty, getString(com.sunmi.sunmiK1Ext.R.string.dialog_gohome_tips), getString(com.sunmi.sunmiK1Ext.R.string.dialog_gohome), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    backTopFragment();
                }
            }, getString(com.sunmi.sunmiK1Ext.R.string.dialog_cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            }, new CustomDialog.DialogOver() {
                @Override
                public void over() {
                    backTopFragment();
                }
            });
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    resumeProcess();
                }
            });
            dialog.show();
        } else {
            dialog.show();
        }

    }

    private void connectService_Lamp() {
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.statuslampmanager");
        intent.setAction("com.sunmi.statuslamp.service");
        bindService(intent, conLamp, BIND_AUTO_CREATE);
    }

    private ServiceConnection conLamp = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log("Lamp服务连接成功");
            lampServiceAidl = IStateLamp.Stub.asInterface(service);
            stateLampPresent.setStateLamp(lampServiceAidl);     //因为本activity要用
            isServiceOK_Lamp = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            showToast("Lamp服务连接失败");
            lampServiceAidl = null;
            isServiceOK_Lamp = false;
        }
    };

    void connectService_CardReader() {
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.idcardservice");
        intent.setAction("com.sunmi.idcard");
        bindService(intent, conIDCard, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection conIDCard = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log("身份证服务连接成功");
            idCardServiceAidl = IDCardServiceAidl.Stub.asInterface(service);
            isServiceOK_IDCard = true;

            //从身份证获取非接服务
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        miFareCardServiceAidl = idCardServiceAidl.getMiFareCardService();  //此处耗时3.5s
                        if(miFareCardServiceAidl != null) {
                            Log("非接服务连接成功");
                            isServiceOK_MifareCard = true;
                        }
                        else {
                            showToast("非接服务连接失败");
                            isServiceOK_MifareCard = false;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            showToast("idCard failed");
            idCardServiceAidl = null;
            isServiceOK_IDCard = false;
        }
    };

    private void connectService_CardSender() {
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.mifarecard");
        intent.setAction("com.sunmi.MifareCardService");
        startService(intent);
        bindService(intent, conSendCard, BIND_AUTO_CREATE);
    }

    private ServiceConnection conSendCard = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log("发卡器服务连接成功");
            sendCardServiceAidl = IMifareCard.Stub.asInterface(service);
            isServiceOK_SendCard = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            showToast("发卡器服务连接失败");
            sendCardServiceAidl = null;
            isServiceOK_SendCard = false;
        }
    };

    private void connectService_Printer() {
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.extprinterservice");
        intent.setAction("com.sunmi.extprinterservice.PrinterService");
        startService(intent);
        bindService(intent, conPrinter, BIND_AUTO_CREATE);
    }

    private ServiceConnection conPrinter = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log("打印机服务连接成功");
            extPrinterServiceAidl = ExtPrinterService.Stub.asInterface(service);;
            isServiceOK_Printer = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            showToast("打印机服务连接失败");
            extPrinterServiceAidl = null;
            isServiceOK_Printer = false;
        }
    };

    private void connectService_Scanner(){
        scannerServiceBroadcast = this;     //取MainActivity的Context
        isServiceOK_Scanner = true;
    }

    @Override
    public void replaceContent(com.sunmi.sunmiK1Ext.BaseFragment fragment, boolean addToBackStack) {
        if (isOk || isDebug) {
            super.replaceContent(fragment, addToBackStack);
        }
    }

    @Override
    public void backTopFragment() {
        super.backTopFragment();
        countDownPresent.setCountDownCallback(null);
        replaceContent(mainFragment, false);
        cancelProcess();
        stateLampPresent.closeAllLamp();
        hotelUser = new HotelUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stateLampPresent.closeAllLamp();
        stopProcess();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeProcess();
    }

    @Override
    protected void onDestroy() {
        if (idCardServiceAidl != null) {
            try {
                idCardServiceAidl.cancelAutoReading();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(conIDCard);
        }
        if (sendCardServiceAidl != null) {
            unbindService(conSendCard);
        }
        if (lampServiceAidl != null) {
            unbindService(conLamp);
        }
        if (extPrinterServiceAidl != null)
            unbindService(conPrinter);

        countDownPresent.cancel();
        soundPresent.close();
        super.onDestroy();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentAttach(com.sunmi.sunmiK1Ext.BaseFragment baseFragment) {
        if (!isOk && !isDebug) {
            return;
        }
//        stateLampPresent.closeAllLamp();
        if (baseFragment instanceof InfoFragment) {
//            stateLampPresent.startCardLed();
            process.setImageResource(com.sunmi.sunmiK1Ext.R.drawable.crumbs_03);
//            ((InfoFragment) baseFragment).setMifareCard(sendCardServiceAidl);
            ((InfoFragment) baseFragment).setPrinterServiceAidl(extPrinterServiceAidl);
            ((InfoFragment) baseFragment).setHotelUser(hotelUser);
        }
        else if (baseFragment instanceof IDCardFragment) {
            startProcess();             //从这里开始
            soundPresent.startIdcard();
//            stateLampPresent.startIdLed();
            ((IDCardFragment) baseFragment).setIdCardServiceAidl(idCardServiceAidl);
            ((IDCardFragment) baseFragment).setHotelUser(hotelUser);
        }
        else if (baseFragment instanceof FaceFragment) {
            soundPresent.startFace();
//            stateLampPresent.startCameraLed();
        }
        else if (baseFragment instanceof PhoneFragment) {
            soundPresent.startPhone();
            process.setImageResource(com.sunmi.sunmiK1Ext.R.drawable.crumbs_02);
        }
        else if (baseFragment instanceof InfoCreateFragment) {
            soundPresent.startCheck();
            process.setImageResource(com.sunmi.sunmiK1Ext.R.drawable.crumbs_02);    //初始是01，所以01的都不用再设
            ((InfoCreateFragment) baseFragment).setHotelUser(hotelUser);
        }
        else if (baseFragment instanceof MifareCardFragment){
            soundPresent.startMifareCard();
            ((MifareCardFragment) baseFragment).setMifareCardServiceAidl(miFareCardServiceAidl);
            ((MifareCardFragment) baseFragment).setHotelUser(hotelUser);
        }
        else if (baseFragment instanceof RechargeFragment){
            soundPresent.startRecharge();
            ((RechargeFragment) baseFragment).setScannerServiceAidl(this);
            ((RechargeFragment) baseFragment).setHotelUser(hotelUser);
        }
        //----------------------------------------------------------
    }

    private void startProcess() {
        isProcess = true;
        tips.setTextColor(Color.parseColor("#99333C4F"));
        goHome.setVisibility(View.VISIBLE);
        process.setVisibility(View.VISIBLE);
        process.setImageResource(com.sunmi.sunmiK1Ext.R.drawable.crumbs_01);

        hiddenBg.start();
        decentBanner.setVisibility(View.GONE);

    }

    private void cancelProcess() {
        isProcess = false;
        tips.setTextColor(Color.parseColor("#99FFFFFF"));
        goHome.setVisibility(View.GONE);
        process.setImageResource(com.sunmi.sunmiK1Ext.R.drawable.crumbs_01);
        process.setVisibility(View.INVISIBLE);

        showBg.start();
        decentBanner.setVisibility(View.VISIBLE);

    }

    public void stopProcess() {
        getFirstFragment().setStop();
        isProcess = false;
        countDownPresent.setStop(true);
    }

    public void resumeProcess() {
        getFirstFragment().setResume();
        isProcess = true;
        countDownPresent.setStop(false);
    }



}
