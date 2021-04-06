package plat.skytv.client.meet.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.liboshuai.framework.db.LitepalHelper;
import com.liboshuai.framework.db.NewFriend;
import com.liboshuai.framework.entity.Consts;
import com.liboshuai.framework.manager.CloudManager;
import com.liboshuai.framework.utils.JsonUtil;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.SpUtils;

import java.util.List;

import gson.TextBean;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;


/**
 * Author:boshuai.li
 * Time:2020/5/14   14:22
 * Description:This is CloudService
 */
public class CloudService extends Service {

    private Disposable disposable;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        linkCloudServer();
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
                    // 普通消息
                    if (CloudManager.TYPE_TEXT.equals(textBean.getType())) {

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
                    } else {
                        // 未知消息类型
                    }
                }

                return false;
            }
        });
    }
}
