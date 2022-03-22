package com.dl.playfun.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.dl.playfun.BR;
import com.google.gson.annotations.SerializedName;

/**
 * @author wulei
 */
public class UserInfoEntity extends BaseObservable {

    private int id;
    private String nickname;
    @SerializedName("remark_name")
    private String remarkName;
    private String avatar;
    private int sex;
    private int age;
    @SerializedName("occupation_id")
    private Integer occupationId;
    private String constellation;
    @SerializedName("is_vip")
    private int isVip;
    @SerializedName("end_time")
    private String endTime;
    private int certification;
    @SerializedName("album_type")
    private int albumType;
    @SerializedName("burn_count")
    private Integer burnCount;
    @SerializedName("album_money")
    private Integer albumMoney;
    @SerializedName("invite_url")
    private String inviteUrl;

    //2021/4/29新增。 用户
    @SerializedName("is_auth")
    private Integer isAuth;

    //邀请码
    private String code;

    @SerializedName("p_id")
    private Integer pId;
    private BannerItemEntity message;

    //录音
    private String sound;
    @SerializedName("sound_time")
    private Integer soundTime;

    //首次有没有录制声音
    @SerializedName("is_sound")
    private Integer isSound;

    //录音审核进度
    @SerializedName("sound_status")
    private Integer soundStatus;

    public Integer getSoundStatus() {
        return soundStatus;
    }

    public void setSoundStatus(Integer soundStatus) {
        this.soundStatus = soundStatus;
    }

    public Integer getIsSound() {
        return isSound;
    }

    public void setIsSound(Integer isSound) {
        this.isSound = isSound;
    }

    public Integer getSoundTime() {
        return soundTime;
    }

    public void setSoundTime(Integer soundTime) {
        this.soundTime = soundTime;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(Integer isAuth) {
        this.isAuth = isAuth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getOccupationId() {
        return occupationId;
    }

    public void setOccupationId(Integer occupationId) {
        this.occupationId = occupationId;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public int getIsVip() {
        return isVip;
    }

    public void setIsVip(int isVip) {
        this.isVip = isVip;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getCertification() {
        return certification;
    }

    public void setCertification(int certification) {
        this.certification = certification;
    }

    @Bindable
    public int getAlbumType() {
        return albumType;
    }

    public void setAlbumType(int albumType) {
        this.albumType = albumType;
        notifyPropertyChanged(BR.albumType);
    }

    @Bindable
    public Integer getBurnCount() {
        return burnCount;
    }

    public void setBurnCount(Integer burnCount) {
        this.burnCount = burnCount;
        notifyPropertyChanged(BR.burnCount);
    }

    public Integer getAlbumMoney() {
        return albumMoney;
    }

    public void setAlbumMoney(Integer albumMoney) {
        this.albumMoney = albumMoney;
    }

    public String getInviteUrl() {
        return inviteUrl;
    }

    public void setInviteUrl(String inviteUrl) {
        this.inviteUrl = inviteUrl;
    }

    public BannerItemEntity getMessage() {
        return message;
    }

    public void setMessage(BannerItemEntity message) {
        this.message = message;
    }

}
