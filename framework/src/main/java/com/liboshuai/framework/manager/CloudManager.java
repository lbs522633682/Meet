package com.liboshuai.framework.manager;

import android.content.Context;

import com.liboshuai.framework.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

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

    // ObjectName
    public static final String MSG_TEXT_NAME = "RC:TxtMsg";
    public static final String MSG_IMAGE_NAME = "RC:ImgMsg";
    public static final String MSG_LOCATION_NAME = "RC:LBSMsg";

    // msgType
    // 普通消息
    public static final String TYPE_TEXT = "TYPE_TEXT";
    // 添加好友的消息
    public static final String TYPE_ADD_FRIEND = "TYPE_ADD_FRIEND";
    // 同意添加好友的消息
    public static final String TYPE_AGREED_FRIEND = "TYPE_AGREED_FRIEND";


    private static CloudManager mInstnce;

    /**
     * 发送消息的结果回调
     */
    IRongCallback.ISendMessageCallback iSendMessageCallback = new IRongCallback.ISendMessageCallback() {
        @Override
        public void onAttached(Message message) { // 消息存到本地数据库的回调
            //LogUtils.i("iSendMessageCallback onAttached message = " + message);
        }

        @Override
        public void onSuccess(Message message) {
            LogUtils.i("iSendMessageCallback onSuccess message = " + message);
        }

        @Override
        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
            LogUtils.i("iSendMessageCallback onError message = " + message);
        }
    };

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
     *
     * @param token
     */
    public void connect(String token) {

        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtils.i("connect s = " + s);
                // 测试消息 发送
                CloudManager.getInstance().sendTextMessage("很高兴认识你", "b757a3c83d");
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
     *
     * @param listener
     */
    public void setOnReceiveMessageListener(RongIMClient.OnReceiveMessageListener listener) {
        RongIMClient.setOnReceiveMessageListener(listener);
    }

    /**
     * 发送文本消息
     *
     * @param message
     * @param targetId
     */
    public void sendTextMessage(String message, String targetId) {
        /*public void sendMessage(Conversation.ConversationType type, String targetId, MessageContent
        content, String pushContent, String pushData, IRongCallback.ISendMessageCallback callback) {*/
        TextMessage textMessage = TextMessage.obtain(message);
        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE,
                targetId,
                textMessage,
                null,
                null,
                iSendMessageCallback);
    }

    /**
     * 发送多类型的 文本消息
     *
     * @param message
     * @param type
     * @param targetId
     */
    public void sendTextMessage(String message, String type, String targetId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", message);
            jsonObject.put("type", type); // 如果没有type，则为普通类型
            sendTextMessage(jsonObject.toString(), targetId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
