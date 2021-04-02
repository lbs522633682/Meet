package com.liboshuai.framework.manager;

import android.content.Context;

import com.liboshuai.framework.utils.LogUtils;

import io.rong.imlib.RongIMClient;

/**
 * Author:boshuai.li
 * Time:2020/8/11   14:08
 * Description: 融云的管理器
 */
public class CloudManager {
    /**
     * 融云相关参数
     */
    public static final String RONGCLOUD_GET_TOKEN = "http://api-cn.ronghub.com/user/getToken.json";
    public static final String CLOUD_SECRECT = "SRlG9pAHfH";
    public static final String CLOUD_KEY = "25wehl3u20a0w";

    private static CloudManager mInstnce;

    private CloudManager() {
    }

    public static CloudManager getInstance() {
        if (mInstnce == null) {
            synchronized (CloudManager.class) {
                if (mInstnce == null) {
                    mInstnce = new CloudManager();
                }
            }
        }
        return mInstnce;
    }

    public void init(Context context) {
        RongIMClient.init(context, CLOUD_KEY);
    }

    /**
     * 链接融云服务
     * @param token
     */
    public void connect(String token) {

        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtils.i("connect s = " + s);
            }

            @Override
            public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {
                LogUtils.e("cloud token error code = " + connectionErrorCode);
            }

            @Override
            public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {

            }
        });
    }

    /**
     * 在断开和融云的连接后，有新消息时，仍然能够收到推送通知，调用 disconnect() 方法
     */
    public void disconnect() {
        RongIMClient.getInstance().disconnect();
    }

    /**
     * 不想收到任何推送通知并断开连接，需调用 logout() 方法. 切换账号操作也需调用 logout() 方法
     */
    public void logout() {
        RongIMClient.getInstance().logout();
    }

    /**
     * 设置消息的监听器
     * @param listener
     */
    public void setOnReceiveMessageListener(RongIMClient.OnReceiveMessageListener listener) {
        RongIMClient.setOnReceiveMessageListener(listener);
    }
}
