package plat.skytv.client.meet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.bmob.IMUser;
import com.liboshuai.framework.entity.Consts;
import com.liboshuai.framework.manager.DialogManager;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.SpUtils;
import com.liboshuai.framework.utils.ToastUtil;
import com.liboshuai.framework.view.DialogView;
import com.liboshuai.framework.view.LoadingView;
import com.liboshuai.framework.view.TouchPictureV;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import plat.skytv.client.meet.MainActivity;
import plat.skytv.client.meet.R;

/**
 * Author:boshuai.li
 * Time:2020/3/19   16:14
 * Description: 登录页
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    /**
     * 1. 点击发送按钮 弹出提示框 图片验证码 验证通过之后
     * 2. 发送验证码 按钮不可点击 按钮开始倒计时 倒计时结束 按钮可点击 文字变成发送
     * 3. 通过手机号码 and 验证码 进行登录
     * 4. 登录成功 之后 获取本地对象
     */

    private static final int H_TIME = 1001;
    private static int TIME = 60; // 60s 倒计时
    private EditText et_phone;
    private EditText et_code;
    private Button btn_send_code;
    private Button btn_login;
    private TextView tv_test_login;
    private TextView tv_user_agreement;
    private DialogView mCodeView;
    private TouchPictureV mPictureV;

    private LoadingView mLoadingView; // 加载loading

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case H_TIME:
                    TIME--;
                    if (TIME > 0) {
                        btn_send_code.setText(TIME + "s");
                        mHandler.sendEmptyMessageDelayed(H_TIME, 1000L);
                    } else {
                        // 重置时间
                        TIME = 60;
                        // 倒计时结束
                        btn_send_code.setEnabled(true);
                        btn_send_code.setText(getString(R.string.text_login_send));
                    }
                    break;
            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {

        initDialogView();

        et_phone = (EditText) findViewById(R.id.et_phone);
        et_code = (EditText) findViewById(R.id.et_code);
        btn_send_code = (Button) findViewById(R.id.btn_send_code);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_test_login = (TextView) findViewById(R.id.tv_test_login);
        tv_user_agreement = (TextView) findViewById(R.id.tv_user_agreement);

        btn_send_code.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_test_login.setOnClickListener(this);

        // 获取保存的phone 显示
        String phone = SpUtils.getInstance().getString(Consts.SP_PHONE, "");
        if (!TextUtils.isEmpty(phone)) {
            et_phone.setText(phone);
        }
    }

    private void initDialogView() {

        mLoadingView = new LoadingView(this);

        mCodeView = DialogManager.getInstance().initView(this, R.layout.dialog_code_view);
        mPictureV = mCodeView.findViewById(R.id.mPictureV);
        mPictureV.setViewResultListener(new TouchPictureV.OnViewResultListener() {
            @Override
            public void onResult() {
                // 拖拽成功
                LogUtils.i("initDialogView onResult enter");
                DialogManager.getInstance().hide(mCodeView);
                sendSMS();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_code:
                DialogManager.getInstance().show(mCodeView);
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_test_login:
                loginTest();
                break;
        }
    }

    private void loginTest() {

        // 展示loading

        mLoadingView.show("正在登陆...");

        String account = SpUtils.getInstance().getString(Consts.SP_PHONE, null);
        String pwd = SpUtils.getInstance().getString(Consts.SP_PWD, null);

        // TODo 默认登录自己的账号 测试用
        //account = "15967153155";
        account = "12510603000";
        pwd = "123456";

        et_phone.setText(account);
        et_code.setText(pwd);

        // 已经登陆过，直接登录 (使用账号密码)
        BmobManager.getInstance().login(account, pwd, new LogInListener<IMUser>() {
            @Override
            public void done(IMUser o, BmobException e) {
                if (e == null) {
                    LogUtils.i("login success");
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    LogUtils.i("signUp error = " + e.toString());
                    ToastUtil.showTextToast(LoginActivity.this, "Error = " + e.toString());
                }
            }
        });
    }

    private void login() {
        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showTextToastById(this, R.string.text_login_phone_null);

            return;
        }

        String code = et_code.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showTextToastById(this, R.string.text_login_code_null);
            return;
        }

        // 展示loading

        mLoadingView.show("正在登陆...");

        String account = SpUtils.getInstance().getString(Consts.SP_PHONE, null);
        String pwd = SpUtils.getInstance().getString(Consts.SP_PWD, null);

        // TODo 默认登录自己的账号 测试用
        account = "15967153155";
        pwd = "123456";

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)) { // 本地无账号 注册流程

            BmobManager.getInstance().signUp(phone, code, new SaveListener<IMUser>() {
                @Override
                public void done(IMUser imUser, BmobException e) {
                    mLoadingView.hide();
                    if (e == null) {
                        LogUtils.i("signUp objId" + imUser.getObjectId());
                        SpUtils.getInstance().putString(Consts.SP_PHONE, phone);
                        SpUtils.getInstance().putString(Consts.SP_PWD, code);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        LogUtils.i("signUp error = " + e.toString());
                        ToastUtil.showTextToast(LoginActivity.this, "Error = " + e.toString());
                    }
                }
            });
        } else {
            // 已经登陆过，直接登录 (使用账号密码)
            BmobManager.getInstance().login(account, pwd, new LogInListener<IMUser>() {
                @Override
                public void done(IMUser o, BmobException e) {
                    if (e == null) {
                        LogUtils.i("login success");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        LogUtils.i("signUp error = " + e.toString());
                        ToastUtil.showTextToast(LoginActivity.this, "Error = " + e.toString());
                    }
                }
            });
        }


        // 短信条数可能用完，这里使用账号密码
/*        BmobManager.getInstance().signOrLoginByMobilePhone(phone, code, new LogInListener<IMUser>() {
            @Override
            public void done(IMUser imUser, BmobException e) {
                mLoadingView.hide();
                if (e == null) {
                    LogUtils.i("短信注册或登录成功：" + imUser.getUsername());
                    // 保存手机号码
                    SpUtils.getInstance().putString(Consts.SP_PHONE, phone);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    LogUtils.i("短信注册或登录失败：" + e.getErrorCode() + "-" + e.getMessage() + "\n");
                    ToastUtil.showTextToast(LoginActivity.this, "Error = " + e.toString());
                }
            }
        });*/


    }

    private void sendSMS() {
        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.text_login_phone_null), Toast.LENGTH_SHORT).show();
            return;
        }

        BmobManager.getInstance().requestSMSCode(phone, new QueryListener<Integer>() {
            @Override
            public void done(Integer smsId, BmobException e) {
                if (e == null) {
                    LogUtils.i("发送验证码成功，短信ID：" + smsId + "\n");
                    btn_send_code.setEnabled(false);
                    mHandler.sendEmptyMessage(H_TIME);

                    ToastUtil.showTextToastById(LoginActivity.this, R.string.text_login_code_success);
                } else {
                    LogUtils.i("发送验证码失败：" + e.getErrorCode() + "-" + e.getMessage() + "\n");
                    ToastUtil.showTextToastById(LoginActivity.this, R.string.text_login_code_failed);
                }
            }
        });
    }
}
