package com.liboshuai.framework.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.liboshuai.framework.utils.SystemUI;

/**
 * Author:boshuai.li
 * Time:2020/3/17   11:45
 * Description:This is BaseUIActivity
 */
public class BaseUIActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUI.fixSystemUI(this);
    }
}
