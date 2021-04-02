package plat.skytv.client.meet.receiver;

import android.content.Context;

import io.rong.push.PushType;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * Author:boshuai.li
 * Time:2020/8/11   14:23
 * Description: 融云的离线通知
 */
public class SealNotificationReceiver extends PushMessageReceiver {
    @Override
    public boolean onNotificationMessageArrived(Context context, PushType pushType, PushNotificationMessage pushNotificationMessage) {
        // false，弹出融云的默认通知
        // true，不会弹出通知，需要由用户自定义
        return true;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushType pushType, PushNotificationMessage pushNotificationMessage) {
        // false，融云SDK内部处理通知点击事件
        // true，由用户自定义点击事件
        return true;
    }
}
