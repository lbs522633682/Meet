package com.liboshuai.framework.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import com.liboshuai.framework.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:boshuai.li
 * Time:2020/3/19   16:14
 * Description: 最上层的Act
 * <p>
 * -BaseActivity 所有的统一功能：语言切换 请求权限
 * -BaseUIActivity 沉浸式状态栏
 * -BaseBackActivity 返回键
 * ...
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * 申请所需权限
     */
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private List<String> mPerList = new ArrayList<>();

    /**
     * 检查单个权限
     *
     * @param permission
     * @return
     */
    protected boolean checkPermission(String permission) {

        // 当前版本 >= 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int check = checkSelfPermission(permission);
            LogUtils.i("checkPermission check = " + check);
            return check == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * 判断是否需要申请权限
     *
     * @return
     */
    protected boolean checkPermissionAll() {
        mPerList.clear();
        for (int i = 0; i < PERMISSIONS.length; i++) {
            boolean check = checkPermission(PERMISSIONS[i]);
            if (!check) {
                mPerList.add(PERMISSIONS[i]);
            }
        }

        return mPerList.size() > 0;
    }

    /**
     * 请求权限组
     *
     * @param permissions
     * @param requestCode
     */
    protected void requestPermission(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    /**
     * 请求 权限组
     * @param requestCode
     */
    protected void requestPermissionAll(int requestCode) {
        requestPermission((String[]) mPerList.toArray(new String[mPerList.size()]), requestCode);
    }
}
