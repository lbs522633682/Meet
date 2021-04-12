package com.liboshuai.framework.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liboshuai.framework.event.EventManager;
import com.liboshuai.framework.event.MessageEvent;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    public static final int PERMISSION_REQ_CODE = 1000;
    public static final int PERMISSION_WINDOW_REQUEST_CODE = 1001;

    /**
     * 申请所需权限
     */
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    // 待申请的权限集合
    private List<String> mPerList = new ArrayList<>();
    // 没有同意的权限集合
    private List<String> mNotAllowPerList = new ArrayList<>();

    // 监听权限申请结果
    private OnPermissionResult permissionResult;
    // 回调code
    private int requestCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventManager.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        /* Do something */
        ToastUtil.showTextToast(this, "EventBus 接收消息");
    }

    /**
     * 封装方法
     *
     * @param requestCode
     * @param permissionResult
     */
    protected void request(int requestCode, OnPermissionResult permissionResult) {
        if (!checkPermissionAll()) {
            requestPermissionAll(requestCode, permissionResult);
        }
    }

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
                LogUtils.i("checkPermissionAll i = " + PERMISSIONS[i]);
                mPerList.add(PERMISSIONS[i]);
            }
        }

        return mPerList.size() > 0 ? false : true;
    }

    /**
     * 请求权限组
     *
     * @param permissions
     * @param requestCode
     */
    protected void requestPermission(String[] permissions, int requestCode) {
        LogUtils.i("requestPermission permissions = " + permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    /**
     * 请求权限组
     *
     * @param permissions
     */
    protected void requestPermission(String[] permissions) {
        LogUtils.i("requestPermission permissions = " + permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQ_CODE);
        }
    }

    /**
     * 请求 权限组
     *
     * @param requestCode
     */
    protected void requestPermissionAll(int requestCode, OnPermissionResult permissionResult) {
        this.permissionResult = permissionResult;
        this.requestCode = requestCode;
        requestPermission((String[]) mPerList.toArray(new String[mPerList.size()]), requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mNotAllowPerList.clear();
        if (requestCode == this.requestCode) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        // 申请失败
                        mNotAllowPerList.add(permissions[i]);
                    }
                }

                if (permissionResult != null) {
                    if (mNotAllowPerList.size() == 0) {
                        permissionResult.OnSuccess();
                    } else {
                        permissionResult.OnFail(mNotAllowPerList);
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 权限申请回调
     */
    protected interface OnPermissionResult {
        void OnSuccess();

        /**
         * 将未允许的 权限回调出去
         *
         * @param mNotAllowPerList
         */
        void OnFail(List<String> mNotAllowPerList);
    }


    /**
     * 检查是否拥有 窗口权限
     * 该应用是否可显示在其他应用之上
     *
     * @return
     */
    protected boolean checkWindowPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }

        return true;
    }

    /**
     * 请求窗口权限
     *
     * @param requestCode
     */
    protected void requestWindowPermissions(int requestCode) {
        Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                , Uri.parse("package:" + getPackageName()));
        startActivityForResult(i, requestCode);
    }
}
