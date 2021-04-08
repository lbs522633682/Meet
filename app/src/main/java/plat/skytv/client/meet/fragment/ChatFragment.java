package plat.skytv.client.meet.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.liboshuai.framework.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import plat.skytv.client.meet.R;
import plat.skytv.client.meet.fragment.chat.AllFriendFragment;
import plat.skytv.client.meet.fragment.chat.CallRecordFragment;
import plat.skytv.client.meet.fragment.chat.ChatRecordFragment;

/**
 * Author:boshuai.li
 * Time:2020/5/13   11:47
 * Description: 聊天
 */
public class ChatFragment extends BaseFragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    // 所有联系人
    private AllFriendFragment allFriendFragment;
    // 通话记录
    private CallRecordFragment callRecordFragment;
    // 聊天记录
    private ChatRecordFragment chatRecordFragment;

    // 三个子fragment的 集合
    private List<BaseFragment> mFragmentList = new ArrayList<>();

    private String[] mTitles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mTabLayout = view.findViewById(R.id.mTabLayout);
        mViewPager = view.findViewById(R.id.mViewPager);

        mTitles = new String[]{getString(R.string.text_chat_tab_title_1),
                getString(R.string.text_chat_tab_title_2),
                getString(R.string.text_chat_tab_title_3)};

        allFriendFragment = new AllFriendFragment();
        callRecordFragment = new CallRecordFragment();
        chatRecordFragment = new ChatRecordFragment();
        mFragmentList.add(chatRecordFragment);
        mFragmentList.add(callRecordFragment);
        mFragmentList.add(allFriendFragment);

        for (int i = 0; i < mTitles.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitles[i]));
        }

        // 设置预加载
        mViewPager.setOffscreenPageLimit(mTitles.length);
        mViewPager.setAdapter(new ChatPageAdapter(getFragmentManager()));
        // tablayout和viewpager联动
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                defTabStyle(tab, 20);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 用来改变tab的样式
                tab.setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // 默认第一个选中
        defTabStyle(mTabLayout.getTabAt(0), 20);
    }

    /**
     * 设置tab的样式
     *
     * @param tab
     * @param size
     */
    private void defTabStyle(TabLayout.Tab tab, int size) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_tab_text, null);
        TextView tv_tab = view.findViewById(R.id.tv_tab);
        tv_tab.setTextSize(size);
        tv_tab.setTextColor(Color.WHITE);
        tv_tab.setText(tab.getText());

        // 设置自定义的样式
        tab.setCustomView(view);
    }

    /**
     * y
     */
    class ChatPageAdapter extends FragmentStatePagerAdapter {

        public ChatPageAdapter(FragmentManager fm, int behavior) {
            // 默认使用 BEHAVIOR_SET_USER_VISIBLE_HINT
            super(fm, behavior);
        }

        public ChatPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            //super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }
}
