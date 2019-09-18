package com.sunmi.sunmiK1Ext.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class HotelUser implements Parcelable {
    private String name;
    private String IDCard;
    private byte[] miCard;
    private byte[] miCardInfo;
    private String roomId;
    private String paymentNo;
    private String phoneNum;
    private String idcard_face;
    private Bitmap photo_face;

    private long money_sum;     //额外临时加的
    private String miCardNo;    //会员编号，可以是卡读出来的，和无卡时随机生成的

    protected HotelUser(Parcel in) {
        name = in.readString();
        IDCard = in.readString();
        miCard = in.createByteArray();
        miCardInfo = in.createByteArray();
        roomId = in.readString();
        paymentNo = in.readString();
        phoneNum = in.readString();
        idcard_face = in.readString();
        photo_face = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(IDCard);
        dest.writeByteArray(miCard);
        dest.writeByteArray(miCardInfo);
        dest.writeString(roomId);
        dest.writeString(paymentNo);
        dest.writeString(phoneNum);
        dest.writeString(idcard_face);
        dest.writeParcelable(photo_face, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HotelUser> CREATOR = new Creator<HotelUser>() {
        @Override
        public HotelUser createFromParcel(Parcel in) {
            return new HotelUser(in);
        }

        @Override
        public HotelUser[] newArray(int size) {
            return new HotelUser[size];
        }
    };

    public String getIdcard_face() {
        return idcard_face;
    }

    public void setIdcard_face(String idcard_face) {
        this.idcard_face = idcard_face;
    }

    public Bitmap getPhoto_face() {
        return photo_face;
    }

    public void setPhoto_face(Bitmap photo_face) {
        this.photo_face = photo_face;
    }

    public HotelUser() {

    }


    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public byte[] getMiCard() {
        return miCard;
    }

    public void setMiCard(byte[] miCard) {
        this.miCard = miCard;
    }

    public byte[] getMiCardInfo() {
        return miCardInfo;
    }

    public void setMiCardInfo(byte[] miCardInfo) {
        this.miCardInfo = miCardInfo;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    //零食架的(临时加的)
    public long getMoneySum(){ return money_sum; }
    public void setMoneySum(long money_sum){ this.money_sum = money_sum; }

    public String getMiCardNo(){ return miCardNo; }
    public void setMiCardNo(String miCardNo){ this.miCardNo = miCardNo; }
}
