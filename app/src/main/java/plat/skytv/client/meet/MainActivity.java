package plat.skytv.client.meet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.liboshuai.framework.base.BaseUIActivity;
import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.entity.Consts;
import com.liboshuai.framework.manager.DialogManager;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.utils.SpUtils;
import com.liboshuai.framework.view.DialogView;

import java.util.List;

import plat.skytv.client.meet.fragment.ChatFragment;
import plat.skytv.client.meet.fragment.MeFragment;
import plat.skytv.client.meet.fragment.SquareFragment;
import plat.skytv.client.meet.fragment.StarFragment;
import plat.skytv.client.meet.service.CloudService;
import plat.skytv.client.meet.ui.FirstUploadActivity;

/**
 * Author:boshuai.li
 * Time:2020/3/19   16:14
 * Description: 主页
 */
public class MainActivity extends BaseUIActivity implements View.OnClickListener {

    // 上传 头像 昵称的回调
    public static final int UPLOAD_REQUEST_CODE = 1002;

    // Fragment 容器
    private FrameLayout mMainLayout;

    // 四个点击事件
    private LinearLayout ll_star;
    private LinearLayout ll_square;
    private LinearLayout ll_chat;
    private LinearLayout ll_me;

    // 声明fragment
    private ChatFragment mFraChat;
    private MeFragment mFraMe;
    private SquareFragment mFraSquare;
    private StarFragment mFraStar;

    // 声明fragment操作类
    private FragmentTransaction mTransChat;
    private FragmentTransaction mTransMe;
    private FragmentTransaction mTransSquare;
    private FragmentTransaction mTransStar;

    private ImageView iv_star;
    private TextView tv_star;
    private ImageView iv_square;
    private TextView tv_square;
    private ImageView iv_chat;
    private TextView tv_chat;
    private ImageView iv_me;
    private TextView tv_me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        // 模拟数据到后台
        // SimulationData.testData();
    }

    /**
     * 请求权限
     */
    private void requestPerssions() {

        // 1.请求窗口权限
        if (!checkWindowPermissions()) {
            requestWindowPermissions(PERMISSION_WINDOW_REQUEST_CODE);
        }

        // 2.请求全部权限组
        request(PERMISSION_REQ_CODE, new OnPermissionResult() {
            @Override
            public void OnSuccess() {
                LogUtils.i("requestPerssions OnSuccess");
            }

            @Override
            public void OnFail(List<String> mNotAllowPerList) {
                LogUtils.i("requestPerssions OnFail mNotAllowPerList = " + mNotAllowPerList.toString());
            }
        });
    }


    private void initView() {

        requestPerssions();

        mMainLayout = (FrameLayout) findViewById(R.id.mMainLayout);

        ll_star = (LinearLayout) findViewById(R.id.ll_star);
        iv_star = (ImageView) findViewById(R.id.iv_star);
        tv_star = (TextView) findViewById(R.id.tv_star);

        ll_square = (LinearLayout) findViewById(R.id.ll_square);
        iv_square = (ImageView) findViewById(R.id.iv_square);
        tv_square = (TextView) findViewById(R.id.tv_square);


        ll_chat = (LinearLayout) findViewById(R.id.ll_chat);
        iv_chat = (ImageView) findViewById(R.id.iv_chat);
        tv_chat = (TextView) findViewById(R.id.tv_chat);

        ll_me = (LinearLayout) findViewById(R.id.ll_me);
        iv_me = (ImageView) findViewById(R.id.iv_me);
        tv_me = (TextView) findViewById(R.id.tv_me);

        ll_star.setOnClickListener(this);
        ll_square.setOnClickListener(this);
        ll_chat.setOnClickListener(this);
        ll_me.setOnClickListener(this);

        // 设置文本
        tv_star.setText(getString(R.string.text_main_star));
        tv_square.setText(getString(R.string.text_main_square));
        tv_me.setText(getString(R.string.text_main_me));
        tv_chat.setText(getString(R.string.text_main_chat));


        initFragment();

        // 默认选择 星球tab
        checkMainTab(0);

        checkToken();
    }

    private void checkToken() {
        // 获取token 需要三个参数 1.用户ObjectId 2.头像地址 3.昵称
        String token = SpUtils.getInstance().getString(Consts.SP_TOKEN, null);
        if (!TextUtils.isEmpty(token)) {
            // 启动云服务去链接 融云服务
            startService(new Intent(this, CloudService.class));
        } else {
            // 1. 有三个参数
            String tokenPhoto = BmobManager.getInstance().getUser().getTokenPhoto();
            String tokenNickName = BmobManager.getInstance().getUser().getTokenNickName();
            LogUtils.i("checkToken tokenNickName = " + tokenNickName);
            if (!TextUtils.isEmpty(tokenPhoto) && !TextUtils.isEmpty(tokenNickName)) {
                createToken();
            } else {
                createUploadDialog();
            }
        }
    }

    /**
     * 创建一个上传的对话框
     */
    private void createUploadDialog() {
        DialogView uploadView = DialogManager.getInstance().initView(this, R.layout.dialog_first_upload);
        uploadView.setCancelable(false);
        ImageView iv_go_upload = uploadView.findViewById(R.id.iv_go_upload);
        iv_go_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.getInstance().hide(uploadView);
                // 进入上传页面
                FirstUploadActivity.startActivity(MainActivity.this, UPLOAD_REQUEST_CODE);
            }
        });

        DialogManager.getInstance().show(uploadView);
    }

    /**
     * 创建一个token
     */
    private void createToken() {
        LogUtils.i("createToken enter");
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        if (mFraChat == null) {
            mFraChat = new ChatFragment();
            mTransChat = getSupportFragmentManager().beginTransaction();
            mTransChat.add(R.id.mMainLayout, mFraChat);
            mTransChat.commit();
        }

        if (mFraMe == null) {
            mFraMe = new MeFragment();
            mTransMe = getSupportFragmentManager().beginTransaction();
            mTransMe.add(R.id.mMainLayout, mFraMe);
            mTransMe.commit();
        }

        if (mFraStar == null) {
            mFraStar = new StarFragment();
            mTransStar = getSupportFragmentManager().beginTransaction();
            mTransStar.add(R.id.mMainLayout, mFraStar);
            mTransStar.commit();
        }

        if (mFraSquare == null) {
            mFraSquare = new SquareFragment();
            mTransSquare = getSupportFragmentManager().beginTransaction();
            mTransSquare.add(R.id.mMainLayout, mFraSquare);
            mTransSquare.commit();
        }
    }

    /**
     * 展示Fragment
     *
     * @param fragment
     */
    private void showFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            hideAllFragment(fragmentTransaction);
            fragmentTransaction.show(fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    /**
     * 隐藏所有的fragment
     *
     * @param fragmentTransaction
     */
    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (mFraSquare != null) {
            fragmentTransaction.hide(mFraSquare);
        }
        if (mFraMe != null) {
            fragmentTransaction.hide(mFraMe);
        }
        if (mFraChat != null) {
            fragmentTransaction.hide(mFraChat);
        }
        if (mFraStar != null) {
            fragmentTransaction.hide(mFraStar);
        }
    }

    /**
     * tab页 状态选择
     *
     * @param index
     */
    private void checkMainTab(int index) {
        switch (index) {
            case 0:
                showFragment(mFraStar);
                iv_star.setImageResource(R.drawable.img_star_p);
                iv_chat.setImageResource(R.drawable.img_chat);
                iv_me.setImageResource(R.drawable.img_me);
                iv_square.setImageResource(R.drawable.img_square);

                tv_star.setTextColor(getResources().getColor(R.color.colorAccent));
                tv_chat.setTextColor(Color.BLACK);
                tv_square.setTextColor(Color.BLACK);
                tv_me.setTextColor(Color.BLACK);
                break;
            case 1:
                showFragment(mFraSquare);
                iv_star.setImageResource(R.drawable.img_star);
                iv_chat.setImageResource(R.drawable.img_chat);
                iv_me.setImageResource(R.drawable.img_me);
                iv_square.setImageResource(R.drawable.img_square_p);

                tv_star.setTextColor(Color.BLACK);
                tv_chat.setTextColor(Color.BLACK);
                tv_square.setTextColor(getResources().getColor(R.color.colorAccent));
                tv_me.setTextColor(Color.BLACK);
                break;
            case 2:
                showFragment(mFraChat);

                iv_star.setImageResource(R.drawable.img_star);
                iv_chat.setImageResource(R.drawable.img_chat_p);
                iv_me.setImageResource(R.drawable.img_me);
                iv_square.setImageResource(R.drawable.img_square);

                tv_star.setTextColor(Color.BLACK);
                tv_chat.setTextColor(getResources().getColor(R.color.colorAccent));
                tv_square.setTextColor(Color.BLACK);
                tv_me.setTextColor(Color.BLACK);
                break;
            case 3:
                showFragment(mFraMe);
                iv_star.setImageResource(R.drawable.img_star);
                iv_chat.setImageResource(R.drawable.img_chat);
                iv_me.setImageResource(R.drawable.img_me_p);
                iv_square.setImageResource(R.drawable.img_square);

                tv_star.setTextColor(Color.BLACK);
                tv_chat.setTextColor(Color.BLACK);
                tv_square.setTextColor(Color.BLACK);
                tv_me.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_star:
                checkMainTab(0);
                break;
            case R.id.ll_square:
                checkMainTab(1);
                break;
            case R.id.ll_chat:
                checkMainTab(2);
                break;
            case R.id.ll_me:
                checkMainTab(3);
                break;
        }
    }

    /**
     * fragment 优化
     *
     * @param fragment
     */
    @Override
    public void onAttachFragment(Fragment fragment) {
        if (mFraStar != null && fragment instanceof StarFragment) {
            mFraStar = (StarFragment) fragment;
        }

        if (mFraChat != null && fragment instanceof ChatFragment) {
            mFraChat = (ChatFragment) fragment;
        }

        if (mFraMe != null && fragment instanceof MeFragment) {
            mFraMe = (MeFragment) fragment;
        }

        if (mFraSquare != null && fragment instanceof SquareFragment) {
            mFraSquare = (SquareFragment) fragment;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == UPLOAD_REQUEST_CODE) {
                // 上传头像成功
                checkToken();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
