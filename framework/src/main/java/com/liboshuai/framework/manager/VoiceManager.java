package com.liboshuai.framework.manager;

import android.content.Context;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.liboshuai.framework.utils.LogUtils;

/**
 * Author:boshuai.li
 * Time:2021/4/13
 * Description: 讯飞语音管理类
 */
public class VoiceManager {

    private static VoiceManager minstance;
    private RecognizerDialog mIat;

    private VoiceManager(Context context) {
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=5e7c1ecf");

        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIat = new RecognizerDialog(context, new InitListener() {
            @Override
            public void onInit(int i) {
                LogUtils.i("VoiceManager onInit i = " + i);
            }
        });


        //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        mIat.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
        mIat.setParameter(SpeechConstant.SUBJECT, null);
        //设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        //此处engineType为“cloud”
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置语音输入语言，zh_cn为简体中文
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //设置结果返回语言
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
        //取值范围{1000～10000}
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
        //自动停止录音，范围{0~10000}
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
    }

    public static VoiceManager getInstance(Context context) {

        if (minstance == null) {
            synchronized (VoiceManager.class) {
                if (minstance == null) {
                    minstance = new VoiceManager(context);
                }
            }
        }

        return minstance;
    }

    /**
     * 开始讲话
     * @param recognizerDialogListener
     */
    public void startSpeak(RecognizerDialogListener recognizerDialogListener) {
        //开始识别并设置监听器
        mIat.setListener(recognizerDialogListener);
        //显示听写对话框
        mIat.show();
    }
}
