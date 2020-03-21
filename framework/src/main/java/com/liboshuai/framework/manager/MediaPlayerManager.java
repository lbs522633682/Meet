package com.liboshuai.framework.manager;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.RequiresApi;

import com.liboshuai.framework.utils.LogUtils;

import java.io.IOException;

/**
 * Author:boshuai.li
 * Time:2020/3/17   14:08
 * Description: 媒体管理类
 */
public class MediaPlayerManager {
    /**
     * 1.熟悉 MediaPlayer的常用api
     * 2.定义获取进度的监听方法，一般通过Handler来做
     */

    // 播放状态
    public static final int MEDIA_STATUS_PALY = 0;
    // 暂停
    public static final int MEDIA_STATUS_PAUSE = 1;
    // 停止
    public static final int MEDIA_STATUS_STOP = 2;

    public int MEDIA_STATUS = MEDIA_STATUS_STOP;

    private static final int H_PROGRESS_WHAT = 1000;

    // 当前状态，默认停止
    private MediaPlayer mediaPlayer;
    private OnMusicProgressListener musicProgressListener;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case H_PROGRESS_WHAT:
                    if (musicProgressListener != null) {
                        // 返回当前进度 及 百分比
                        int p = (int) (((float) getCurrentPosition() / (float) getDuration()) * 100);
                        musicProgressListener.OnProgress(getCurrentPosition(), p);
                        mHandler.sendEmptyMessageDelayed(H_PROGRESS_WHAT, 1000);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public MediaPlayerManager() {
        mediaPlayer = new MediaPlayer();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /**
     * 播放
     *
     * @param path
     */
    public void startPlay(AssetFileDescriptor path) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path.getFileDescriptor(), path.getStartOffset(), path.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
            MEDIA_STATUS = MEDIA_STATUS_PALY;
            mHandler.sendEmptyMessage(H_PROGRESS_WHAT);
        } catch (IOException e) {
            LogUtils.e(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 播放
     *
     * @param path
     */
    public void startPlay(String path) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            MEDIA_STATUS = MEDIA_STATUS_PALY;
            mHandler.sendEmptyMessage(H_PROGRESS_WHAT);
        } catch (IOException e) {
            LogUtils.e(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 暂停
     */
    public void pausePlay() {
        if (isPlaying()) {
            mediaPlayer.pause();
            MEDIA_STATUS = MEDIA_STATUS_PAUSE;
            mHandler.removeMessages(H_PROGRESS_WHAT);
        }

    }

    /**
     * 继续播放
     */
    public void continuePlay() {
        mediaPlayer.start();
        MEDIA_STATUS = MEDIA_STATUS_PALY;
        mHandler.sendEmptyMessage(H_PROGRESS_WHAT);
    }

    /**
     * 暂停
     */
    public void stopPlay() {
        mediaPlayer.stop();
        MEDIA_STATUS = MEDIA_STATUS_STOP;
        mHandler.removeMessages(H_PROGRESS_WHAT);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    /**
     * 是否循环
     *
     * @param looping
     */
    public void setLooping(boolean looping) {
        mediaPlayer.setLooping(looping);
    }

    /**
     * 定位进度
     *
     * @param msec
     */
    public void seekTo(int msec) {
        mediaPlayer.seekTo(msec);
    }

    /**
     * 播放结束的监听
     *
     * @param listener
     */
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        mediaPlayer.setOnCompletionListener(listener);
    }

    /**
     * 播放错误的监听
     *
     * @param listener
     */
    public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        mediaPlayer.setOnErrorListener(listener);
    }

    /**
     * 播放进度的监听
     *
     * @param listener
     */
    public void setOnProgressListener(OnMusicProgressListener listener) {
        musicProgressListener = listener;
    }

    public interface OnMusicProgressListener {

        /**
         * 返回当前进度和百分比
         *
         * @param currentPosition
         * @param pos
         */
        void OnProgress(int currentPosition, int pos);
    }
}
