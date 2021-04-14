package plat.skytv.client.meet.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.bmob.IMUser;
import com.liboshuai.framework.db.LitepalHelper;
import com.liboshuai.framework.db.NewFriend;
import com.liboshuai.framework.entity.Consts;
import com.liboshuai.framework.event.EventManager;
import com.liboshuai.framework.event.MessageEvent;
import com.liboshuai.framework.helper.GlideHelper;
import com.liboshuai.framework.helper.WindowHelper;
import com.liboshuai.framework.manager.CloudManager;
import com.liboshuai.framework.manager.MediaPlayerManager;
import com.liboshuai.framework.utils.JsonUtil;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.SpUtils;
import com.liboshuai.framework.utils.TimeUtils;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;
import gson.TextBean;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import io.rong.calllib.IRongCallListener;
import io.rong.calllib.IRongReceivedCallListener;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;
import plat.skytv.client.meet.R;


/**
 * Author:boshuai.li
 * Time:2020/5/14   14:22
 * Description:This is CloudService
 */
public class CloudService extends Service implements View.OnClickListener {


    private static final int H_TIME_WHAT = 1000;

    private int mCallTimer = 0; // 通话计时
    private Handler mTimerHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            if (msg.what == H_TIME_WHAT) {
                mCallTimer ++;
                String timeText = TimeUtils.formatTimer(mCallTimer * 1000);
                audio_tv_status.setText(timeText);
                mTimerHander.sendEmptyMessageDelayed(H_TIME_WHAT, 1000);
            }
            return false;
        }
    });

    private Disposable disposable;

    private CircleImageView audio_iv_photo;
    private TextView audio_tv_status;
    private LinearLayout audio_ll_recording;
    private ImageView audio_iv_recording;
    private LinearLayout audio_ll_answer;
    private ImageView audio_iv_answer;
    private LinearLayout audio_ll_hangup;
    private ImageView audio_iv_hangup;
    private LinearLayout audio_ll_hf;
    private ImageView audio_iv_hf;
    private ImageView audio_iv_small;

    private View mFullAudioView;

    // 播放来电铃声
    private MediaPlayerManager mAudioPlayManager;
    private MediaPlayerManager mHangUpPlayManager;

    private String mCallId = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initWindow();
        initPlayService();
        linkCloudServer();
    }

    private void initPlayService() {
        mAudioPlayManager = new MediaPlayerManager();
        mHangUpPlayManager = new MediaPlayerManager();
//        mMediaPlayManager.setLooping(true); // 没有生效
        mAudioPlayManager.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mAudioPlayManager.startPlay(CloudManager.callAudioPath);
            }
        });
    }

    private void initWindow() {
        WindowHelper.getInstance().initWindow(this);
        mFullAudioView = WindowHelper.getInstance().getView(R.layout.layout_chat_audio);

        audio_iv_photo = (CircleImageView) mFullAudioView.findViewById(R.id.audio_iv_photo);
        audio_tv_status = (TextView) mFullAudioView.findViewById(R.id.audio_tv_status);
        audio_ll_recording = (LinearLayout) mFullAudioView.findViewById(R.id.audio_ll_recording);
        audio_iv_recording = (ImageView) mFullAudioView.findViewById(R.id.audio_iv_recording);
        audio_ll_answer = (LinearLayout) mFullAudioView.findViewById(R.id.audio_ll_answer);
        audio_iv_answer = (ImageView) mFullAudioView.findViewById(R.id.audio_iv_answer);
        audio_ll_hangup = (LinearLayout) mFullAudioView.findViewById(R.id.audio_ll_hangup);
        audio_iv_hangup = (ImageView) mFullAudioView.findViewById(R.id.audio_iv_hangup);
        audio_ll_hf = (LinearLayout) mFullAudioView.findViewById(R.id.audio_ll_hf);
        audio_iv_hf = (ImageView) mFullAudioView.findViewById(R.id.audio_iv_hf);
        audio_iv_small = (ImageView) mFullAudioView.findViewById(R.id.audio_iv_small);

        audio_ll_recording.setOnClickListener(this);
        audio_ll_answer.setOnClickListener(this);
        audio_ll_hangup.setOnClickListener(this);
        audio_ll_hf.setOnClickListener(this);
        audio_iv_small.setOnClickListener(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    /**
     * 服务启动的时候 去链接融云的服务
     */
    private void linkCloudServer() {
        String token = SpUtils.getInstance().getString(Consts.SP_TOKEN, "");
        LogUtils.i("linkCloudServer token = " + token);
        CloudManager.getInstance().connect(token);
        CloudManager.getInstance().setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) { // 接收到的消息
                LogUtils.i("linkCloudServer onReceived message = " + message);

                String objectName = message.getObjectName();
                // 文本类型消息
                if (CloudManager.MSG_TEXT_NAME.equals(objectName)) {
                    TextMessage textMessage = (TextMessage) message.getContent();
                    String content = textMessage.getContent();
                    LogUtils.i("linkCloudServer onReceived  content = " + content);
                    TextBean textBean = JsonUtil.parseObject(content, TextBean.class);

                    if (textBean != null) {
                        // 普通消息
                        if (CloudManager.TYPE_TEXT.equals(textBean.getType())) {
                            MessageEvent messageEvent = new MessageEvent(EventManager.FLAG_SEND_TEXT);
                            messageEvent.setText(textBean.getMsg());
                            messageEvent.setUserId(message.getSenderUserId());
                            EventManager.post(messageEvent);
                        } else if (CloudManager.TYPE_ADD_FRIEND.equals(textBean.getType())) {
                            // 添加好友消息
                            LogUtils.i("添加好友消息");
                            // 再添加数据库之前查询，如果重复则不添加
                            disposable = Observable.create(new ObservableOnSubscribe<List<NewFriend>>() { // 创建一个发射器
                                @Override
                                public void subscribe(@NonNull ObservableEmitter<List<NewFriend>> emitter) throws Exception { // 在发射器中执行 操作
                                    List<NewFriend> newFriends = LitepalHelper.getInstance().queryNewFriend();
                                    LogUtils.i("linkCloudServer queryNewFriend newFriends = " + newFriends);
                                    emitter.onNext(newFriends);
                                    emitter.onComplete();
                                }
                            }).subscribeOn(Schedulers.newThread()) // 执行线程
                                    .observeOn(AndroidSchedulers.mainThread()) // 订阅线程
                                    .subscribe(new Consumer<List<NewFriend>>() { // 创建接收器，接受结果
                                        @Override
                                        public void accept(List<NewFriend> newFriends) throws Exception {
                                            boolean isHave = false; // 是否在数据库中找到
                                            if (newFriends != null && newFriends.size() > 0) {
                                                for (int j = 0; j < newFriends.size(); j++) {
                                                    NewFriend newFriend = newFriends.get(j);
                                                    if (newFriend.getUserId().equals(message.getSenderUserId())) {
                                                        isHave = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (!isHave) {
                                                LogUtils.i("保存添加好友信息");
                                                // BMOB 和 rongCloud 都没有提供存储方法
                                                // 使用另外的方法存入本地数据库
                                                LitepalHelper.getInstance().saveNewFriend(textBean.getMsg(), message.getSenderUserId());
                                            }
                                        }
                                    });

                        } else if (CloudManager.TYPE_AGREED_FRIEND.equals(textBean.getType())) {
                            // 同意添加好友消息
                            // 1. 添加到好友列表
                            BmobManager.getInstance().addFriend(message.getSenderUserId(), new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        // 2. 刷新好友列表
                                    }
                                }
                            });
                        } else {
                            // 未知消息类型
                        }
                    }
                } else if (CloudManager.MSG_IMAGE_NAME.equals(objectName)) { // 图片消息接收
                    ImageMessage imageMessage = (ImageMessage) message.getContent();
                    String imgUrl = imageMessage.getRemoteUri().toString();
                    LogUtils.i("接收到图片消息，url = " + imgUrl);
                    if (!TextUtils.isEmpty(imgUrl)) {
                        // 发送 messageEvent
                        MessageEvent messageEvent = new MessageEvent(EventManager.FLAG_SEND_IMAGE);
                        messageEvent.setImgUrl(imgUrl);
                        messageEvent.setUserId(message.getSenderUserId());
                        EventManager.post(messageEvent);
                    }
                } else if (CloudManager.MSG_LOCATION_NAME.equals(objectName)) { // 位置消息
                    LocationMessage locationMessage = (LocationMessage) message.getContent();
                    MessageEvent messageEvent = new MessageEvent(EventManager.FLAG_SEND_LOCATION);
                    messageEvent.setLa(locationMessage.getLat());
                    messageEvent.setLo(locationMessage.getLng());
                    messageEvent.setAddress(locationMessage.getPoi());
                    messageEvent.setUserId(message.getSenderUserId());
                    EventManager.post(messageEvent);
                }

                return false;
            }
        });

        // 设置来电监听
        CloudManager.getInstance().setReceivedCallListener(new IRongReceivedCallListener() {
            @Override
            public void onReceivedCall(RongCallSession rongCallSession) {
                // 接收到来电
                LogUtils.i("onReceivedCall rongCallSession = " + JsonUtil.toJSON(rongCallSession));

                /**
                 * 1. 获取拨打和接听的 用户id
                 * 2. 来电的播放铃声
                 * 3. 加载个人信息 填充
                 * 4. 显示window
                 */

                // 检查设备可用性
                if (!CloudManager.getInstance().isVoIPEnabled(CloudService.this)) {
                    return;
                }

                // 呼叫端UserId
                String callUserId = rongCallSession.getCallerUserId();
                // 更新UI
                updateWindowInfo(0, callUserId);
                // 通话ID
                mCallId = rongCallSession.getCallId();

                if (rongCallSession.getMediaType() == RongCallCommon.CallMediaType.AUDIO) {
                    LogUtils.i("onReceivedCall 收到语音通话");
                    WindowHelper.getInstance().showView(mFullAudioView);
                } else if (rongCallSession.getMediaType() == RongCallCommon.CallMediaType.VIDEO) {
                    LogUtils.i("onReceivedCall 收到视频通话");
                }
            }

            @Override
            public void onCheckPermission(RongCallSession rongCallSession) {
                // 检查权限的回调
                LogUtils.i("onCheckPermission enter");
            }
        });

        CloudManager.getInstance().setVoIPCallListener(new IRongCallListener() {
            @Override
            public void onCallOutgoing(RongCallSession rongCallSession, SurfaceView surfaceView) {
                // 电话播出的回调
                LogUtils.i("onCallOutgoing rongCallSession = " + JsonUtil.toJSON(rongCallSession));

                String targetId = rongCallSession.getTargetId();
                // 获取通话id
                mCallId = rongCallSession.getCallId();

                updateWindowInfo(1, targetId);

                // 展示播出窗口
                if (rongCallSession.getMediaType() == RongCallCommon.CallMediaType.AUDIO) {
                    LogUtils.i("onReceivedCall 收到语音通话");
                    WindowHelper.getInstance().showView(mFullAudioView);
                } else if (rongCallSession.getMediaType() == RongCallCommon.CallMediaType.VIDEO) {
                    LogUtils.i("onReceivedCall 收到视频通话");
                }
            }

            @Override
            public void onCallConnected(RongCallSession rongCallSession, SurfaceView surfaceView) {
                // 已建立通话
                LogUtils.i("onCallConnected rongCallSession = " + JsonUtil.toJSON(rongCallSession));

                /**
                 * 1. 开始计时
                 * 2. 关闭铃声
                 * 3. 更新按钮
                 */

                // 关闭铃声
                if (mAudioPlayManager.isPlaying()) {
                    mAudioPlayManager.stopPlay();
                }

                // 开始计时
                mTimerHander.sendEmptyMessage(H_TIME_WHAT);

                // 更新按钮
                if (rongCallSession.getMediaType() == RongCallCommon.CallMediaType.AUDIO) {
                    LogUtils.i("onReceivedCall 收到语音通话");
                    goneAudioView(true, false, true, true, true);
                } else if (rongCallSession.getMediaType() == RongCallCommon.CallMediaType.VIDEO) {
                    LogUtils.i("onReceivedCall 收到视频通话");
                }
            }

            @Override
            public void onCallDisconnected(RongCallSession rongCallSession, RongCallCommon.CallDisconnectedReason callDisconnectedReason) {
                // 通话结束
                LogUtils.i("onCallDisconnected rongCallSession = " + JsonUtil.toJSON(rongCallSession));

                // 铃声挂断
                if (mAudioPlayManager.isPlaying()) {
                    mAudioPlayManager.stopPlay();
                }

                // 播放挂断铃声
                mHangUpPlayManager.startPlay(CloudManager.callAudioHangup);

                // 关闭计时器
                mTimerHander.removeMessages(H_TIME_WHAT);
                // 重置通话时间
                mCallTimer = 0;
                // 关闭通话界面
                if (rongCallSession.getMediaType() == RongCallCommon.CallMediaType.AUDIO) {
                    LogUtils.i("onReceivedCall 收到语音通话");
                    WindowHelper.getInstance().hideView(mFullAudioView);
                } else if (rongCallSession.getMediaType() == RongCallCommon.CallMediaType.VIDEO) {
                    LogUtils.i("onReceivedCall 收到视频通话");
                }
            }

            @Override
            public void onRemoteUserRinging(String s) {
                // 被呼叫端 正在响铃
            }

            @Override
            public void onRemoteUserJoined(String s, RongCallCommon.CallMediaType callMediaType, int i, SurfaceView surfaceView) {

            }

            @Override
            public void onRemoteUserInvited(String s, RongCallCommon.CallMediaType callMediaType) {

            }

            @Override
            public void onRemoteUserLeft(String s, RongCallCommon.CallDisconnectedReason callDisconnectedReason) {

            }

            @Override
            public void onMediaTypeChanged(String s, RongCallCommon.CallMediaType callMediaType, SurfaceView surfaceView) {

            }

            @Override
            public void onError(RongCallCommon.CallErrorCode callErrorCode) {

            }

            @Override
            public void onRemoteCameraDisabled(String s, boolean b) {

            }

            @Override
            public void onRemoteMicrophoneDisabled(String s, boolean b) {

            }

            @Override
            public void onNetworkReceiveLost(String s, int i) {

            }

            @Override
            public void onNetworkSendLost(int i, int i1) {

            }

            @Override
            public void onFirstRemoteVideoFrame(String s, int i, int i1) {

            }
        });
    }

    /**
     * @param recording
     * @param answer
     * @param hangup
     * @param hf        免提
     * @param small
     */
    private void goneAudioView(boolean recording, boolean answer, boolean hangup, boolean hf, boolean small) {
        audio_ll_recording.setVisibility(recording ? View.VISIBLE : View.GONE);
        audio_ll_answer.setVisibility(answer ? View.VISIBLE : View.GONE);
        audio_ll_hangup.setVisibility(hangup ? View.VISIBLE : View.GONE);
        audio_ll_hf.setVisibility(hf ? View.VISIBLE : View.GONE);
        audio_iv_small.setVisibility(small ? View.VISIBLE : View.GONE);
    }

    /**
     * 更新窗口UI信息
     *
     * @param index 0:接收 1 拨打
     * @param id
     */
    private void updateWindowInfo(int index, String id) {

        if (index == 0) {
            goneAudioView(false, true, true, false, false);
            mAudioPlayManager.startPlay(CloudManager.callAudioPath);
        } else {
            goneAudioView(false, false, true, false, false);
        }

        BmobManager.getInstance().queryObjectIdUser(id, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        IMUser imUser = list.get(0);
                        GlideHelper.loadUrl(CloudService.this, imUser.getPhoto(), audio_iv_photo);
                        if (index == 0) {
                            audio_tv_status.setText(imUser.getNickName() + "来电了...");
                        } else {
                            audio_tv_status.setText("正在呼叫" + imUser.getNickName() + "...");
                        }
                    }
                }
            }
        });
    }

    private boolean mIsRecording = false; // 是否正在录音
    private boolean mIsHF = false; // 是否免提

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.audio_ll_recording:
                // 录音
                if (mIsRecording) {
                    mIsRecording = false;
                    audio_iv_recording.setImageResource(R.drawable.img_recording);
                    CloudManager.getInstance().stopAudioRecording();
                } else {
                    mIsRecording = true;
                    CloudManager.getInstance().startAudioRecording("/sdcard/Meet/" + System.currentTimeMillis() + ".wav");
                    audio_iv_recording.setImageResource(R.drawable.img_recording_p);
                }
                break;
            case R.id.audio_ll_answer:
                // 接听
                CloudManager.getInstance().acceptCall(mCallId);
                break;
            case R.id.audio_ll_hangup:
                // 挂断
                CloudManager.getInstance().hangUpCall(mCallId);
                break;
            case R.id.audio_ll_hf:
                // 免提
                mIsHF = !mIsHF;
                CloudManager.getInstance().setEnableSpeakerphone(mIsHF);
                audio_iv_hf.setImageResource(mIsHF ? R.drawable.img_hf_p : R.drawable.img_hf);
                break;
            case R.id.audio_iv_small:
                // 缩小窗口
                break;
        }

    }
}
