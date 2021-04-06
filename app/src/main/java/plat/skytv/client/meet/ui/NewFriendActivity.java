package plat.skytv.client.meet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liboshuai.framework.adapter.CommonAdapter;
import com.liboshuai.framework.adapter.CommonViewHolder;
import com.liboshuai.framework.base.BaseBackActivity;
import com.liboshuai.framework.bmob.BmobManager;
import com.liboshuai.framework.bmob.IMUser;
import com.liboshuai.framework.db.LitepalHelper;
import com.liboshuai.framework.db.NewFriend;
import com.liboshuai.framework.utils.LogUtils;


import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import plat.skytv.client.meet.R;

/**
 * Author:boshuai.li
 * Time:2021/4/6   16:47
 * Description: 新朋友界面
 */
public class NewFriendActivity extends BaseBackActivity {

    /**
     * 1. 查询好友的申请列表
     * 2. 通过适配器展示
     * 3. 如果同意添加位自己的好友
     * 4. 并且发送给对方自定义消息
     * 5. 对方将我添加到好友列表
     */

    /**
     * 跳转方法
     * @param activity
     */
    public static void startActivity(Activity activity) {
        activity.startActivity(new Intent(activity, NewFriendActivity.class));
    }

    private RecyclerView mNewFriendView;
    private View item_empty_view;

    private Disposable disposable;

    private CommonAdapter<NewFriend> mNewFriendAdapter;
    private List<NewFriend> mList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        initView();
    }

    private void initView() {
        mNewFriendView = findViewById(R.id.mNewFriendView);
        item_empty_view = findViewById(R.id.item_empty_view);

        mNewFriendView.setLayoutManager(new LinearLayoutManager(this));
        mNewFriendView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mNewFriendAdapter = new CommonAdapter<NewFriend>(mList, new CommonAdapter.OnBindDataListener<NewFriend>() {
            @Override
            public void onBindViewHolder(NewFriend model, CommonViewHolder viewHolder, int type, int position) {
                // 根据id 查询用户的信息
                BmobManager.getInstance().queryObjectIdUser(model.getUserId(), new FindListener<IMUser>() {
                    @Override
                    public void done(List<IMUser> list, BmobException e) {
                        if (e == null) {
                            if (list != null & list.size() > 0) {
                                IMUser imUser = list.get(0);
                                viewHolder.setImageUrl(NewFriendActivity.this, R.id.iv_photo, imUser.getPhoto());
                                viewHolder.setImageResource(R.id.iv_sex, imUser.isSex() ? R.drawable.img_boy_icon : R.drawable.img_girl_icon);
                                viewHolder.setText(R.id.tv_nickname, imUser.getNickName());
                                viewHolder.setText(R.id.tv_age, imUser.getAge() + getString(R.string.text_search_age));
                                viewHolder.setText(R.id.tv_desc, imUser.getDesc());
                                viewHolder.setText(R.id.tv_msg, model.getMsg());

                                if (model.getIsAgree() == NewFriend.STATUS_AGREE) { // 同意
                                    viewHolder.getView(R.id.ll_agree).setVisibility(View.GONE);
                                    viewHolder.getView(R.id.tv_result).setVisibility(View.VISIBLE);
                                    viewHolder.setText(R.id.tv_result, "已同意");
                                } else if (model.getIsAgree() == NewFriend.STATUS_REFUSED) {
                                    viewHolder.getView(R.id.ll_agree).setVisibility(View.GONE);
                                    viewHolder.getView(R.id.tv_result).setVisibility(View.VISIBLE);
                                    viewHolder.setText(R.id.tv_result, "已拒绝");
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_new_friend_item;
            }
        });

        mNewFriendView.setAdapter(mNewFriendAdapter);

        queryNewFriend();
    }

    /**
     * 查询数据库中的新朋友
     */
    private void queryNewFriend() {
        disposable = Observable.create(new ObservableOnSubscribe<List<NewFriend>>() { // 创建一个发射器
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<NewFriend>> emitter) throws Exception { // 在发射器中执行 操作
                List<NewFriend> newFriends = LitepalHelper.getInstance().queryNewFriend();
                LogUtils.i("queryNewFriend newFriends = " + newFriends);
                emitter.onNext(newFriends);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()) // 执行线程
                .observeOn(AndroidSchedulers.mainThread()) // 订阅线程
                .subscribe(new Consumer<List<NewFriend>>() { // 创建接收器，接受结果
                    @Override
                    public void accept(List<NewFriend> newFriends) throws Exception {
                        // 更新UI
                        if (newFriends.size() > 0) {
                            mList.addAll(newFriends);
                            mNewFriendAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
