package com.liboshuai.framework.bmob;

import android.content.Context;
import android.icu.util.Freezable;

import com.liboshuai.framework.utils.LogUtils;

import java.io.File;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
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
    private static final String BMOB_SDK_ID = "a3fc2edca9eccc01110110970f1b4091";
    // private static final String BMOB_SDK_ID = "f8efae5debf319071b44339cf51153fc"; // 老师的Bmob已经开通独立域名
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
        bu.setUsername(phone);
        bu.setPassword(pwd);
        //注意：不能用save方法进行注册
        bu.signUp(imUserSaveListener);
    }

    /**
     * 登录
     *
     * @param phone
     * @param pwd
     * @param listener
     */
    public void login(String phone, String pwd, LogInListener listener) {
        BmobUser.loginByAccount(phone, pwd, listener);
    }

    /**
     * 查询所有用户
     */
    public void queryAllUser(FindListener<IMUser> listener) {
        BmobQuery<IMUser> objectBmobQuery = new BmobQuery<>();
        objectBmobQuery.findObjects(listener);
    }

    /**
     * 查询我的好友
     */
    public void queryMyFriend(FindListener<Friend> listener) {
        BmobQuery<Friend> objectBmobQuery = new BmobQuery<>();
        // 添加 查询条件
        objectBmobQuery.addWhereEqualTo("user", getUser());
        objectBmobQuery.findObjects(listener);
    }

    /**
     * 上传头像的回调
     */
    public interface UploadPhotoListener {
        void uploadDone();

        void upLoadFail(BmobException e);
    }

    /**
     * 根据电话号码，查询用户
     *
     * @param phone
     * @param listener
     */
    public void queryPhoneUser(String phone, FindListener<IMUser> listener) {
        baseQuery("mobilePhoneNumber", phone, listener);
    }

    /**
     * 根据 userid 查询用户信息
     *
     * @param userId
     * @param listener
     */
    public void queryObjectIdUser(String userId, FindListener<IMUser> listener) {
        baseQuery("objectId", userId, listener);
    }

    /**
     * \
     * 查询的基类
     *
     * @param key
     * @param value
     * @param listener
     */
    private void baseQuery(String key, String value, FindListener<IMUser> listener) {
        BmobQuery<IMUser> objectBmobQuery = new BmobQuery<>();
        objectBmobQuery.addWhereEqualTo(key, value);
        objectBmobQuery.findObjects(listener);
    }

    /**
     * 将朋友添加到我的数据库
     *
     * @param imUser   朋友用户
     * @param listener
     */
    public void addFriend(IMUser imUser, SaveListener<String> listener) {
        Friend friend = new Friend();
        friend.setUser(getUser());
        friend.setFriendUser(imUser);
        friend.save(listener);
    }

    /**
     * 通过id查询用户，添加好友
     *
     * @param userid
     * @param listener
     */
    public void addFriend(String userid, final SaveListener<String> listener) {
        queryObjectIdUser(userid, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {

                if (e == null) {
                    if (list != null && list.size() > 0) {
                        IMUser imUser = list.get(0);
                        addFriend(imUser, listener);
                    }
                }
            }
        });
    }
}
