package com.dl.playfun.entity;

import com.google.gson.annotations.SerializedName;

public class GoodsEntity {

    /**
     * id : 1
     * goods_name : 60个
     * tag_price : 6.00
     * price : 6.00
     */

    private int id;
    @SerializedName("google_goods_id")
    private String googleGoodsId;
    @SerializedName("apple_goods_id")
    private String appleGoodsId;
    @SerializedName("goods_name")
    private String goodsName;
    @SerializedName("monthly_price")
    private String monthlyPrice;
    @SerializedName("pay_price")
    private String payPrice;
    @SerializedName("actual_value")
    private  Integer actualValue;

    @SerializedName("is_first")
    private Integer isFirst;
    @SerializedName("first_text")
    private String firstText;

    public Integer getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(Integer isFirst) {
        this.isFirst = isFirst;
    }

    public String getFirstText() {
        return firstText;
    }

    public void setFirstText(String firstText) {
        this.firstText = firstText;
    }

    public Integer getActualValue() {
        return actualValue;
    }

    public void setActualValue(Integer actualValue) {
        this.actualValue = actualValue;
    }

    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoogleGoodsId() {
        return googleGoodsId;
    }

    public void setGoogleGoodsId(String googleGoodsId) {
        this.googleGoodsId = googleGoodsId;
    }

    public String getAppleGoodsId() {
        return appleGoodsId;
    }

    public void setAppleGoodsId(String appleGoodsId) {
        this.appleGoodsId = appleGoodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(String monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public String getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(String payPrice) {
        this.payPrice = payPrice;
    }

}
