package com.liboshuai.framework.bmob;

import androidx.annotation.NonNull;

import com.liboshuai.framework.utils.JsonUtil;

import cn.bmob.v3.BmobUser;

/**
 * Author:boshuai.li
 * Time:2020/5/12   15:00
 * Description: 用户类
 */
public class IMUser extends BmobUser {

    // token 属性

    private String tokenPhoto; // 获取token的头像地址
    private String tokenNickName; // 获取token的昵称
    // 基本属性

    private String nickName;
    private String photo;

    // 其他属性


    private boolean sex = true; // 性别：true = 男 false = 女
    private String desc; // 简介
    private int age = 0;
    private String birthday;
    private String constellation;// 星座
    private String hobby; // 爱好
    private String status; // 单身状态

    public String getTokenPhoto() {
        return tokenPhoto;
    }

    public void setTokenPhoto(String tokenPhoto) {
        this.tokenPhoto = tokenPhoto;
    }

    public String getTokenNickName() {
        return tokenNickName;
    }

    public void setTokenNickName(String tokenNickName) {
        this.tokenNickName = tokenNickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return JsonUtil.toJSON(this);
    }
}
