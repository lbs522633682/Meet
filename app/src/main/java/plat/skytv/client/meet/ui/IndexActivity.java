package plat.skytv.client.meet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.entity.Consts;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.SpUtils;

import plat.skytv.client.meet.MainActivity;
import plat.skytv.client.meet.R;

/**
 * Author:boshuai.li
 * Time:2020/3/17   11:42
 * Description: 启动页
 */
public class IndexActivity extends AppCompatActivity {

    /**
     * 1.启动页全屏
     * 2.延迟进入主页
     * 3.根据逻辑是进入引导页还是登录页
     * 4.适配刘海屏
     */

    private static final int H_SKIP_MAIN = 1001;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == H_SKIP_MAIN) {
                startMain();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        mHandler.sendEmptyMessageDelayed(H_SKIP_MAIN, 2 * 1000);
    }

    /**
     * 打开主页
     * 1.第一次启动，跳转到引导页 重置标志位
     * 2.不是第一次启动，是否曾经登陆过 否：跳转登录页 是：跳转主页
     */
    private void startMain() {
        // 是否是首次运行
        boolean isFirstRun = SpUtils.getInstance().getBoolean(Consts.SP_FIRST_APP, true);
        Intent intent = new Intent();
        if (isFirstRun) {
            intent.setClass(this, GuideActivity.class);
            // 首次运行
            SpUtils.getInstance().putBoolean(Consts.SP_FIRST_APP, false);
        } else {
            LogUtils.i("startMain isnot first");
            // 2. 非首次运行 判断是否登录过
            String token = SpUtils.getInstance().getString(Consts.SP_TOKEN, null);
            if (TextUtils.isEmpty(token)) {
                // 3. bmob 是否登录
                LogUtils.i("isLogin = " + BmobManager.getInstance().isLogin());
                if (BmobManager.getInstance().isLogin()) {
                    // 跳转主页
                    intent.setClass(this, MainActivity.class);
                } else {
                    // 跳转登录页
                    intent.setClass(this, LoginActivity.class);
                }
            } else {
                intent.setClass(this, MainActivity.class);
            }
        }
        startActivity(intent);
        finish();
    }

}
