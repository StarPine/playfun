package com.dl.playfun.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.dl.playfun.BR;
import com.dl.playfun.R;
import com.dl.playfun.app.AppContext;
import com.dl.playfun.data.typeadapter.BooleanTypeAdapter;
import com.dl.playfun.utils.ApiUitl;
import com.dl.playfun.utils.StringUtil;
import com.dl.playfun.utils.Utils;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author wulei
 */
public class UserDataEntity extends BaseObservable {

    /**
     * id : 5
     * nickname : 广州
     * avatar : null
     * birthday : 1991-03-10
     * occupation : 媒体出版
     * program_ids : null
     * hope_object_ids : null
     * permanent_city_ids : 1,2,3,4,5
     * desc : 找真心朋友
     * weight : 60
     * height : 170
     * is_weixin_show : 1
     * weixin : qq123
     */

    private int id;
    private String nickname;
    private String avatar;
    private String birthday;
    private transient Calendar birthdayCal;
    @SerializedName("occupation_id")
    private Integer occupationId;
    @SerializedName("program_ids")
    private List<Integer> programIds;
    @SerializedName("hope_object_ids")
    private List<Integer> hopeObjectIds;
    @SerializedName("permanent_city_ids")
    private List<Integer> permanentCityIds;
    private String desc;
    private Integer weight;
    private Integer height;
    @JsonAdapter(BooleanTypeAdapter.class)
    @SerializedName("is_weixin_show")
    private boolean isWeixinShow;
    private String weixin;
    private String insgram;
    private Integer sex; //1男 0女
    @SerializedName("is_vip")
    private Integer isVip;
    private Integer certification;
    @SerializedName("end_time")
    private String endTime;
    @JsonAdapter(BooleanTypeAdapter.class)
    @SerializedName("is_invite_code")
    private boolean isInviteCode;
    @SerializedName("invite_url")
    private String inviteUrl;

    @SerializedName("p_id")
    private Integer pId;

    @SerializedName("dialog_im_vip_img")
    private String dialogImVipImg;
    //录音
    private String sound;
    @SerializedName("sound_time")
    private Integer soundTime;

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

    public String getDialogImVipImg() {
        return dialogImVipImg;
    }

    public void setDialogImVipImg(String dialogImVipImg) {
        this.dialogImVipImg = dialogImVipImg;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Bindable
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        notifyPropertyChanged(BR.avatar);
    }

    @Bindable
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar calendar) {
        this.birthdayCal = calendar;
        if (Utils.isManilaApp(AppContext.instance())) {
            String dayNumberSuffix = StringUtil.getDayNumberSuffix(calendar.get(Calendar.DAY_OF_MONTH));
            DateFormat dateFormat = new SimpleDateFormat("MMMM d'" + dayNumberSuffix + "',yyyy", Locale.ENGLISH);
            this.birthday = TimeUtils.millis2String(calendar.getTimeInMillis(), dateFormat);
        } else {
            this.birthday = (calendar.get(Calendar.YEAR)) + StringUtils.getString(R.string.year) + (calendar.get(Calendar.MONTH) + 1) + StringUtils.getString(R.string.month) + calendar.get(Calendar.DAY_OF_MONTH) + StringUtils.getString(R.string.daily);
        }
        notifyPropertyChanged(BR.birthday);
    }

    public Calendar getBirthdayCal() {
        return birthdayCal;
    }

    @Bindable
    public Integer getOccupationId() {
        return occupationId;
    }

    public void setOccupationId(Integer occupationId) {
        this.occupationId = occupationId;
        notifyPropertyChanged(BR.occupationId);
    }

    @Bindable
    public List<Integer> getProgramIds() {
        return programIds;
    }

    public void setProgramIds(List<Integer> programIds) {
        this.programIds = programIds;
        notifyPropertyChanged(BR.programIds);
    }

    @Bindable
    public List<Integer> getHopeObjectIds() {
        return hopeObjectIds;
    }

    public void setHopeObjectIds(List<Integer> hopeObjectIds) {
        this.hopeObjectIds = hopeObjectIds;
        notifyPropertyChanged(BR.hopeObjectIds);
    }

    @Bindable
    public List<Integer> getPermanentCityIds() {
        return permanentCityIds;
    }

    public void setPermanentCityIds(List<Integer> permanentCityIds) {
        this.permanentCityIds = permanentCityIds;
        notifyPropertyChanged(BR.permanentCityIds);
    }

    @Bindable
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
        notifyPropertyChanged(BR.desc);
    }

    @Bindable
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
        notifyPropertyChanged(BR.weight);
    }

    @Bindable
    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
        notifyPropertyChanged(BR.height);
    }

    public boolean isWeixinShow() {
        return isWeixinShow;
    }

    public void setWeixinShow(boolean weixinShow) {
        isWeixinShow = weixinShow;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getInsgram() {
        return insgram;
    }

    public void setInsgram(String insgram) {
        this.insgram = insgram;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getIsVip() {
        return isVip;
    }

    public void setIsVip(Integer isVip) {
        this.isVip = isVip;
    }

    public Integer getCertification() {
        return certification;
    }

    public void setCertification(Integer certification) {
        this.certification = certification;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isInviteCode() {
        return isInviteCode;
    }

    public void setInviteCode(boolean inviteCode) {
        isInviteCode = inviteCode;
    }

    public String getInviteUrl() {
        return inviteUrl;
    }

    public void setInviteUrl(String inviteUrl) {
        this.inviteUrl = inviteUrl;
    }

    public boolean isCompleteInfo() {
        boolean isComplete = sex != null && !StringUtils.isEmpty(birthday) && !StringUtils.isEmpty(nickname) && permanentCityIds != null && !permanentCityIds.isEmpty() && hopeObjectIds != null && !hopeObjectIds.isEmpty();
//        if (isInviteCode && sex != null && !StringUtils.isEmpty(birthday) && !StringUtils.isEmpty(nickname) && permanentCityIds != null && !permanentCityIds.isEmpty() && hopeObjectIds != null && !hopeObjectIds.isEmpty()) {
        return isComplete;
    }

    public boolean isPerfect() {
        if (StringUtil.isEmpty(avatar)) {
            return false;
        }
        if (StringUtils.isEmpty(nickname)) {
            return false;
        }
        if (ObjectUtils.isEmpty(permanentCityIds)) {
            return false;
        }
        if (ObjectUtils.isEmpty(birthday)) {
            return false;
        }
        if (ObjectUtils.isEmpty(occupationId) || occupationId.intValue() == 0) {
            return false;
        }
        if (ObjectUtils.isEmpty(programIds) || programIds.size() == 0) {
            return false;
        }
        if (ObjectUtils.isEmpty(hopeObjectIds) || hopeObjectIds.size() == 0) {
            return false;
        }
        if (sex == 0 && (StringUtils.isEmpty(weixin) && StringUtils.isEmpty(insgram))) {
            return false;
        }
        if (sex == 0 && (!StringUtils.isEmpty(weixin) || !StringUtils.isEmpty(insgram))) {
            return true;
        }
        return sex != 0 || (!StringUtils.isEmpty(weixin) && !ApiUitl.isContainChinese(weixin));
    }
}
