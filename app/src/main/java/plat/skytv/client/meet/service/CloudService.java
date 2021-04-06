package plat.skytv.client.meet.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.liboshuai.framework.db.LitepalHelper;
import com.liboshuai.framework.entity.Consts;
import com.liboshuai.framework.manager.CloudManager;
import com.liboshuai.framework.utils.JsonUtil;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.SpUtils;

import gson.TextBean;
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
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        linkCloudServer();
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
                if (objectName.equals(CloudManager.MSG_TEXT_NAME)) {
                    TextMessage textMessage = (TextMessage) message.getContent();
                    String content = textMessage.getContent();
                    LogUtils.i("linkCloudServer onReceived  content = " + content);
                    TextBean textBean = JsonUtil.parseObject(content, TextBean.class);
                    // 普通消息
                    if (CloudManager.TYPE_TEXT.equals(textBean.getType())) {

                    } else if (CloudManager.TYPE_ADD_FRIEND.equals(textBean.getType())) {
                        // 添加好友消息
                        LogUtils.i("添加好友消息");
                        // BMOB 和 rongCloud 都没有提供存储方法
                        // 使用另外的方法存入本地数据库
                        LitepalHelper.getInstance().saveNewFriend(textBean.getMsg(), message.getSenderUserId());
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
