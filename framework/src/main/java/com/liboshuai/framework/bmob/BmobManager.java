package com.liboshuai.framework.bmob;

import android.content.Context;

import com.liboshuai.framework.utils.LogUtils;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Author:boshuai.li
 * Time:2020/3/23   10:25
 * Description: Bmob的管理类
 */
public class BmobManager {

    // 自己申请的key 需要开启独立域名的功能才能用
    // private static final String BMOB_SDK_ID = "a3fc2edca9eccc01110110970f1b4091";
    private static final String BMOB_SDK_ID = "f8efae5debf319071b44339cf51153fc";

    private static BmobManager mBmobManager;

    private BmobManager() {

    }

    public static BmobManager getInstance() {
        if (mBmobManager == null) {
            synchronized (BmobManager.class) {
                if (mBmobManager == null) {
                    mBmobManager = new BmobManager();
                }
            }
        }
        return mBmobManager;
    }

    /**
     * 获取本地已经登录对象
     *
     * @return
     */
    public IMUser getUser() {
        return BmobUser.getCurrentUser(IMUser.class);
    }

    /**
     * 初始化Bmob
     *
     * @param context
     */
    public void initBmob(Context context) {
        Bmob.initialize(context, BMOB_SDK_ID);
    }

    /**
     * @param phone
     * @param listener
     */
    public void requestSMSCode(String phone, QueryListener<Integer> listener) {
        BmobSMS.requestSMSCode(phone, "", listener);
    }

    /**
     * 一键注册或登录:
     *
     * @param phone
     * @param code     验证码
     * @param listener
     */
    public void signOrLoginByMobilePhone(String phone, String code, LogInListener<IMUser> listener) {
        BmobUser.signOrLoginByMobilePhone(phone, code, listener);

    }

    /**
     * 判断用户是否登录
     *
     * @return
     */
    public boolean isLogin() {
        return BmobUser.isLogin();
    }

    /**
     * 上传头像并更新到服务端
     *
     * @param nickname
     * @param uploadFile
     * @param listener
     */
    public void uploadPhoto(final String nickname, File uploadFile, final UploadPhotoListener listener) {

        LogUtils.i("uploadPhoto nickname = " + nickname + ", file = " + uploadFile.getPath());
        final IMUser imUser = getUser();

        // 由于 不能开启独立域名
        final BmobFile bmobFile = new BmobFile(uploadFile);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                LogUtils.i("uploadPhoto uploadblock e = " + e);
                if (e != null) {
                    listener.upLoadFail(e);
                } else {
                    // 上传头像成功, 设置信息
                    imUser.setNickName(nickname);
                    imUser.setPhoto(bmobFile.getFileUrl());

                    imUser.setTokenNickName(nickname);
                    imUser.setTokenPhoto(bmobFile.getFileUrl());

                    // 更新设置完的信息
                    imUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            LogUtils.i("uploadPhoto updateUser e = " + e);
                            if (e != null) {
                                listener.upLoadFail(e);
                            } else {
                                // 更新用户信息成功
                                listener.uploadDone();
                            }
                        }
                    });
                }
            }
        });
    }


    /**
     * 使用账号密码登录
     *
     * @param phone
     * @param pwd
     * @param imUserSaveListener
     */
    public void signUp(String phone, String pwd, SaveListener<IMUser> imUserSaveListener) {
        BmobUser bu = new BmobUser();
        bu.setUsername("phone");
        bu.setPassword(pwd);
        //注意：不能用save方法进行注册
        bu.signUp(imUserSaveListener);
    }

    /**
     * 登录
     * @param phone
     * @param pwd
     * @param listener
     */
    public void login(String phone, String pwd, LogInListener listener) {
        BmobUser.loginByAccount(phone, pwd, listener);
    }

    /**
     * 上传头像的回调
     */
    public interface UploadPhotoListener {
        void uploadDone();

        void upLoadFail(BmobException e);
    }
}
