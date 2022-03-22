package com.dl.playfun.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.dl.playfun.BR;

public class GiveUserBeanEntity extends BaseObservable {
    /**
     * id : 2
     * avatar : null
     */

    private int id;
    private String avatar;
    private int sex;

    public GiveUserBeanEntity(int id, String avatar) {
        this.id = id;
        this.avatar = avatar;
    }

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        notifyPropertyChanged(BR.avatar);
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}