package com.liboshuai.framework.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import plat.skytv.client.framework.BuildConfig;

/**
 * Author:boshuai.li
 * Time:2020/3/16   16:34
 * Description:This is LogUtils
 *
 * 不仅打印日志，还可以记录日志到file中
 */
public class LogUtils {

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    public static void i(String text) {
        if (BuildConfig.LOG_DEBUG) {

            if (!TextUtils.isEmpty(text)) {
                Log.i(BuildConfig.LOG_TAG, text);
                writeToFile(text);
            }
        }
    }
    public static void e(String text) {
        if (BuildConfig.LOG_DEBUG) {
            if (!TextUtils.isEmpty(text)) {
                Log.e(BuildConfig.LOG_TAG, text);
                writeToFile(text);
            }
        }
    }

    private static void writeToFile(String text) {

        String fileName = "/sdcard/Meet/Meet.log";

        String log = mSimpleDateFormat.format(new Date())+ " " + text + "\r\n";

        // 检查文件路径

        File fileGroup = new File("/sdcard/Meet/");
        if (!fileGroup.exists()) {
            fileGroup.mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileOutputStream = new FileOutputStream(fileName, true);
            bufferedWriter = new BufferedWriter(
                    // 避免产生编码问题
                    new OutputStreamWriter(fileOutputStream, Charset.forName("gbk")));
            bufferedWriter.write(log);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
