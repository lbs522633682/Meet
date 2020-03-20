package plat.skytv.client.meet.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;

import com.liboshuai.framework.Framework;

/**
 * Author:boshuai.li
 * Time:2020/3/19   9:32
 * Description:This is BaseApp
 */
public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (getApplicationInfo().packageName.equals(getCurrentProcessName(this))) {
            Framework.getInstance().initFramework(this);
        }
    }

    /**
     * 1.获取当前进程id
     * 2.获取ActivityManager
     * 3.遍历正在运行的进程
     *
     * @return
     */
    private String getCurrentProcessName(Context context) {
        int myPid = Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo :
                    activityManager.getRunningAppProcesses()) {
                if (myPid == runningAppProcessInfo.pid) {
                    return runningAppProcessInfo.processName;
                }
            }
        }
        return null;
    }
}
