package plat.skytv.client.meet.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.liboshuai.framework.base.BasePagerAdapter;
import com.liboshuai.framework.manager.MediaPlayerManager;
import com.liboshuai.framework.utils.AnimUtils;

import java.util.ArrayList;
import java.util.List;

import plat.skytv.client.meet.R;
import plat.skytv.client.meet.test.TestActivity;

/**
 * Author:boshuai.li
 * Time:2020/3/19   16:14
 * Description: 引导页
 */
public class GuideActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 1.viewpage:适配器 | 帧动画
     * 2.小圆点的逻辑
     * 3.歌曲的播放
     * 4.属性动画的旋转
     * 5.跳转
     */

    private ViewPager mViewPager;
    private ImageView iv_music_switch;
    private TextView tv_guide_skip;
    private ImageView iv_guide_point_1;
    private ImageView iv_guide_point_2;
    private ImageView iv_guide_point_3;

    private View view1;
    private View view2;
    private View view3;

    private List<View> mPageList = new ArrayList<>();

    private BasePagerAdapter mPagerAdapter;
    private MediaPlayerManager mediaPlayerManager;
    private ObjectAnimator objectAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        iv_music_switch = (ImageView) findViewById(R.id.iv_music_switch);
        tv_guide_skip = (TextView) findViewById(R.id.tv_guide_skip);
        iv_guide_point_1 = (ImageView) findViewById(R.id.iv_guide_point_1);
        iv_guide_point_2 = (ImageView) findViewById(R.id.iv_guide_point_2);
        iv_guide_point_3 = (ImageView) findViewById(R.id.iv_guide_point_3);

        iv_music_switch.setOnClickListener(this);
        tv_guide_skip.setOnClickListener(this);

        view1 = View.inflate(this, R.layout.layout_pager_guide_1, null);
        view2 = View.inflate(this, R.layout.layout_pager_guide_2, null);
        view3 = View.inflate(this, R.layout.layout_pager_guide_3, null);

        mPageList.add(view1);
        mPageList.add(view2);
        mPageList.add(view3);

        // 预加载
        mViewPager.setOffscreenPageLimit(mPageList.size());

        mPagerAdapter = new BasePagerAdapter(mPageList);
        mViewPager.setAdapter(mPagerAdapter);

        ImageView iv_guide_star = (ImageView) view1.findViewById(R.id.iv_guide_star);
        AnimationDrawable animStar = (AnimationDrawable) iv_guide_star.getBackground();
        animStar.start();

        ImageView iv_guide_night = (ImageView) view2.findViewById(R.id.iv_guide_night);
        AnimationDrawable animNight = (AnimationDrawable) iv_guide_night.getBackground();
        animNight.start();

        ImageView iv_guide_smile = (ImageView) view3.findViewById(R.id.iv_guide_smile);
        AnimationDrawable animSmile = (AnimationDrawable) iv_guide_smile.getBackground();
        animSmile.start();

        // 小圆点
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 播放歌曲
        startMusic();
    }

    private void startMusic() {
        mediaPlayerManager = new MediaPlayerManager();
        // 循环播放
        mediaPlayerManager.setLooping(true);
        AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.guide);
        mediaPlayerManager.startPlay(assetFileDescriptor);

        // 设置一个旋转动画
        objectAnimator = AnimUtils.rotation(iv_music_switch);
        objectAnimator.start();
    }

    /**
     * 选择小圆点
     * @param position
     */
    private void selectPosition(int position) {

        switch (position) {
            case 0:
                iv_guide_point_1.setImageResource(R.drawable.img_guide_point_p);
                iv_guide_point_2.setImageResource(R.drawable.img_guide_point);
                iv_guide_point_3.setImageResource(R.drawable.img_guide_point);
                break;
            case 1:
                iv_guide_point_1.setImageResource(R.drawable.img_guide_point);
                iv_guide_point_2.setImageResource(R.drawable.img_guide_point_p);
                iv_guide_point_3.setImageResource(R.drawable.img_guide_point);
                break;
            case 2:
                iv_guide_point_1.setImageResource(R.drawable.img_guide_point);
                iv_guide_point_2.setImageResource(R.drawable.img_guide_point);
                iv_guide_point_3.setImageResource(R.drawable.img_guide_point_p);
                break;
        }
    }

    /**
     * music
     * 1.正在播放，就暂停
     * 2.暂停，就播放
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_music_switch:
                if (mediaPlayerManager.MEDIA_STATUS == MediaPlayerManager.MEDIA_STATUS_PALY) {
                    objectAnimator.pause();
                    mediaPlayerManager.pausePlay();
                    iv_music_switch.setImageResource(R.drawable.img_guide_music_off);
                } else if (mediaPlayerManager.MEDIA_STATUS == MediaPlayerManager.MEDIA_STATUS_PAUSE) {
                    objectAnimator.start();
                    mediaPlayerManager.continuePlay();
                    iv_music_switch.setImageResource(R.drawable.img_guide_music);
                }
                break;
            case R.id.tv_guide_skip:
                 // TODO Intent i = new Intent(this, LoginActivity.class);
                Intent i = new Intent(this, TestActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mediaPlayerManager.stopPlay();
        super.onDestroy();
    }
}
