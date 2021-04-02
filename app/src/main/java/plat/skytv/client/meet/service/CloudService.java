package plat.skytv.client.meet.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.liboshuai.framework.entity.Consts;
import com.liboshuai.framework.manager.CloudManager;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.SpUtils;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;


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
            public boolean onReceived(Message message, int i) {
                LogUtils.i("linkCloudServer message = " + message);
                return false;
            }
        });
    }
}
