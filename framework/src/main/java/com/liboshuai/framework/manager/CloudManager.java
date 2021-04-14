package com.liboshuai.framework.manager;

import android.content.Context;
import android.net.Uri;

import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.calllib.IRongCallListener;
import io.rong.calllib.IRongReceivedCallListener;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
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

    // Textbean msgType
    // 普通消息
    public static final String TYPE_TEXT = "TYPE_TEXT";
    // 添加好友的消息
    public static final String TYPE_ADD_FRIEND = "TYPE_ADD_FRIEND";
    // 同意添加好友的消息
    public static final String TYPE_AGREED_FRIEND = "TYPE_AGREED_FRIEND";

    //来电铃声
    public static final String callAudioPath = "http://downsc.chinaz.net/Files/DownLoad/sound1/201501/5363.wav";
    //挂断铃声
    public static final String callAudioHangup = "http://downsc.chinaz.net/Files/DownLoad/sound1/201501/5351.wav";


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
            public void onTokenIncorrect() {

            }

            @Override
            public void onSuccess(String s) {
                LogUtils.i("connect s = " + s);
                // 测试消息 发送
                //CloudManager.getInstance().sendTextMessage("很高兴认识你", "b757a3c83d");
            }

            @Override
            public void onError(RongIMClient.ErrorCode connectionErrorCode) {
                LogUtils.e("cloud token error code = " + connectionErrorCode);
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
    private void sendTextMessage(String message, String targetId) {
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

    private RongIMClient.SendImageMessageCallback sendImageMessageCallback = new RongIMClient.SendImageMessageCallback() {
        /**
         * 消息发送前回调, 回调时消息已存储数据库
         * @param message 已存库的消息体
         */
        @Override
        public void onAttached(Message message) {

        }

        /**
         * 消息发送成功
         * @param message 发送成功后的消息体
         */
        @Override
        public void onSuccess(Message message) {
            LogUtils.i("sendImageMessage onSuccess message = " + message);
        }

        /**
         * 消息发送失败
         * @param message   发送失败的消息体
         * @param errorCode 具体的错误
         */
        @Override
        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
            LogUtils.i("sendImageMessage onError message = " + message + ", errorCode = " + errorCode);
        }

        /**
         * 上传进度
         * @param message  发送的消息实体
         * @param progress 进度值: 0 - 100
         */
        @Override
        public void onProgress(Message message, int progress) {
            LogUtils.i("sendImageMessage message = " + message + ", progress = " + progress);
        }
    };

    /**
     * 发送图片消息
     *
     * @param targetId 对方的id
     * @param imgFile  图片文件
     */
    public void sendImageMessage(String targetId, File imgFile) {
        // 1. 仅支持本地图片
        // 2. 图片地址需以 `file://` 开头, 不是以 `file://` 开头的需拼接
        //String path = "file://图片的路径";
        //Uri localUri = Uri.parse(path);
        ImageMessage imageMessage = ImageMessage.obtain(null, Uri.fromFile(imgFile));
        RongIMClient.getInstance().sendImageMessage(Conversation.ConversationType.PRIVATE,
                targetId, imageMessage,
                null,
                null,
                sendImageMessageCallback);

    }

    /**
     * 发送位置消息
     *
     * @param targetId
     * @param la
     * @param lo
     * @param poi
     */
    public void sendLocationMessage(String targetId, double la, double lo, String poi) {
        LocationMessage locationMessage = LocationMessage.obtain(la, lo, poi, null);
        Message message = Message.obtain(targetId, Conversation.ConversationType.PRIVATE, locationMessage);
        RongIMClient.getInstance().sendLocationMessage(message, null, null, iSendMessageCallback);
    }

    /**
     * 获取会话记录
     *
     * @param callback
     */
    public void getConversationList(RongIMClient.ResultCallback<List<Conversation>> callback) {
        RongIMClient.getInstance().getConversationList(callback);
    }

    /**
     * 获取与目标用户的历史聊天记录
     *
     * @param targetId
     * @param callback
     */
    public void getHistoryMessages(String targetId, RongIMClient.ResultCallback<List<Message>> callback) {
        RongIMClient.getInstance().getHistoryMessages(Conversation.ConversationType.PRIVATE,
                targetId,
                -1,
                1000,
                callback);
    }

    /**
     * 从远程服务器拉取与对方的 聊天记录
     *
     * @param targetId
     * @param callback
     */
    public void getRemoteHistoryMessages(String targetId, RongIMClient.ResultCallback<List<Message>> callback) {
        RongIMClient.getInstance().getRemoteHistoryMessages(Conversation.ConversationType.PRIVATE,
                targetId,
                0,
                20,
                callback);

    }

    /*************************************CallLib api********************************************/

    /**
     * 发起音视频 通话
     *
     * @param targetId
     * @param mediaType {@Link RongCallCommon.CallMediaType.VIDEO} 通话类型 语音 or 视频
     */
    private void startCall(String targetId, RongCallCommon.CallMediaType mediaType) {
        Conversation.ConversationType conversationType = Conversation.ConversationType.PRIVATE;
        List<String> userIds = new ArrayList<>();
        userIds.add(targetId);
        String extra = "";
        RongCallClient.getInstance().startCall(conversationType, targetId, userIds, null, mediaType, extra);
    }

    /**
     * 音频通话
     *
     * @param targetId
     */
    public void startAudioCall(Context context, String targetId) {
        if (!isVoIPEnabled(context)) {
            return;
        }
        startCall(targetId, RongCallCommon.CallMediaType.AUDIO);
    }

    /**
     * 视频通话
     *
     * @param targetId
     */
    public void startVideoCall(Context context, String targetId) {
        if (!isVoIPEnabled(context)) {
            return;
        }
        startCall(targetId, RongCallCommon.CallMediaType.VIDEO);
    }

    /**
     * 设置音视频通话监听
     *
     * @param iRongReceivedCallListener
     */
    public void setReceivedCallListener(IRongReceivedCallListener iRongReceivedCallListener) {
        RongCallClient.setReceivedCallListener(iRongReceivedCallListener);

    }

    /**
     * 设置通话状态的监听
     *
     * @param callListener
     */
    public void setVoIPCallListener(IRongCallListener callListener) {
        RongCallClient.getInstance().setVoIPCallListener(callListener);
    }

    /**
     * 接听来电
     *
     * @param callId
     */
    public void acceptCall(String callId) {
        // im未连接或者不在通话中，RongCallClient 和 RongCallSession 为空
        if (RongCallClient.getInstance() != null && RongCallClient.getInstance().getCallSession() != null) {
            LogUtils.i("acceptCall callId = " + callId + ", CallSessionCallId = " + RongCallClient.getInstance().getCallSession().getCallId());
            RongCallClient.getInstance().acceptCall(RongCallClient.getInstance().getCallSession().getCallId());
        }
    }

    /**
     * 挂断来电
     *
     * @param callId
     */
    public void hangUpCall(String callId) {
        // im未连接或者不在通话中，RongCallClient 和 RongCallSession 为空
        if (RongCallClient.getInstance() != null && RongCallClient.getInstance().getCallSession() != null) {
            LogUtils.i("hangUpCall callId = " + callId + ", CallSessionCallId = " + RongCallClient.getInstance().getCallSession().getCallId());
            RongCallClient.getInstance().hangUpCall(RongCallClient.getInstance().getCallSession().getCallId());
        }

    }


    /**
     * 切换媒体类型
     *
     * @param callMediaType
     */
    public void changeCallMediaType(RongCallCommon.CallMediaType callMediaType) {
        RongCallClient.getInstance().changeCallMediaType(callMediaType);
    }

    /**
     * 切换前后相机
     */
    public void switchCamera() {
        RongCallClient.getInstance().switchCamera();
    }

    /**
     * 设置本地摄像头是否可用
     *
     * @param enable
     */
    public void setEnableLocalVideo(boolean enable) {
        RongCallClient.getInstance().setEnableLocalVideo(enable);
    }

    /**
     * 设置本地音频是否可用
     *
     * @param enable
     */
    public void setEnableLocalAudio(boolean enable) {
        RongCallClient.getInstance().setEnableLocalAudio(enable);
    }

    /**
     * 设置免提开关
     *
     * @param enable
     */
    public void setEnableSpeakerphone(boolean enable) {
        RongCallClient.getInstance().setEnableSpeakerphone(enable);
    }

    /**
     * 开启录音
     *
     * @param filePath
     */
    public void startAudioRecording(String filePath) {
        RongCallClient.getInstance().startAudioRecording(filePath);
    }

    /**
     * 关闭录音
     */
    public void stopAudioRecording() {
        RongCallClient.getInstance().stopAudioRecording();
    }

    /**
     * 检查音视频设备是否可用
     */
    public boolean isVoIPEnabled(Context context) {
        if (!RongCallClient.getInstance().isVoIPEnabled(context)) {
            ToastUtil.showTextToast(context, "设备不支持音视频通话");
            return false;
        }
        return true;
    }

}
