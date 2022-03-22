package com.dl.playfun.ui.message.chatdetail;

import java.io.Serializable;

public class CustomMessageData implements Serializable {
    // 经纬度位置
    final static int TYPE_LOCATION = 1001;
    // 阅后即焚
    final static int TYPE_BURN = 1002;
    // 现金红包
    final static int TYPE_RED_PACKAGE = 1003;
    // 钻石红包
    final static int TYPE_COIN_RED_PACKAGE = 1004;

    int type = 0;

    private int senderUserID;

    private String msgId;
    private Integer id;
    private String text;

    private double lat;
    private double lng;
    private String address;

    private int number;

    private String imgPath;

    private CustomMessageData() {
    }

    public static CustomMessageData genLocationMessage(String name, String address, double lat, double lng) {
        CustomMessageData customMessageData = new CustomMessageData();
        customMessageData.setType(TYPE_LOCATION);
        customMessageData.setText(name);
        customMessageData.setAddress(address);
        customMessageData.setLat(lat);
        customMessageData.setLng(lng);
        return customMessageData;
    }

    public static CustomMessageData genCoinRedPackageMessage(int id, int number, String desc) {
        CustomMessageData customMessageData = new CustomMessageData();
        customMessageData.setType(TYPE_COIN_RED_PACKAGE);
        customMessageData.setId(id);
        customMessageData.setText(desc);
        customMessageData.setNumber(number);
        return customMessageData;
    }

    public static CustomMessageData genBurnMessage(String imgPath) {
        CustomMessageData customMessageData = new CustomMessageData();
        customMessageData.setType(TYPE_BURN);
        customMessageData.setImgPath(imgPath);
        return customMessageData;
    }

    public int getSenderUserID() {
        return senderUserID;
    }

    public void setSenderUserID(int senderUserID) {
        this.senderUserID = senderUserID;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}