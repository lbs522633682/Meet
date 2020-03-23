package com.liboshuai.framework.bmob;

import androidx.annotation.NonNull;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.helper.GsonUtil;

/**
 * Author:boshuai.li
 * Time:2020/3/23   10:47
 * Description: Bmob的测试数据类
 */
public class MyData extends BmobObject {

    private String name;
    // 0:male 1：girl
    private int sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    @NonNull
    @Override
    public String toString() {
        return GsonUtil.toJson(this);
    }
}
