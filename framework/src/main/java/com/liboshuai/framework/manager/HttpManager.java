package com.liboshuai.framework.manager;

import android.content.Context;

import com.liboshuai.framework.utils.SHA1;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * FileName: HttpManager
 * Founder: Boshuai.li
 * Profile: OkHttp
 */
public class HttpManager {

    private static final String HEAD_CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // 与服务端定好协议
    public static String HEAD_TIMESTAMP = "timestamp";

    private static volatile HttpManager mInstnce = null;
    private OkHttpClient mOkHttpClient;

    private HttpManager() {
        mOkHttpClient = new OkHttpClient();
    }

    public static HttpManager getInstance() {
        if (mInstnce == null) {
            synchronized (HttpManager.class) {
                if (mInstnce == null) {
                    mInstnce = new HttpManager();
                }
            }
        }
        return mInstnce;
    }

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader(HEAD_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .build();
        try {
            return mOkHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 请求网络
     *
     * @param map
     */
    /**
     * 请求网络
     *
     * @param url
     * @param context
     * @param map     请求参数，GET时为NULL，不为NULL 则POST
     * @return
     */
    public String startRequest(String url, Context context, HashMap<String, String> map) {
        //参数
        String timestamp = getTimeStamp() + "";


        //参数填充
        FormBody.Builder bodyBuilder = null;
        if (map != null) {
            bodyBuilder = new FormBody.Builder();
            for (String key : map.keySet()) {
                String value = map.get(key);
                if (value != null) {
                    bodyBuilder.add(key, value);
                }
            }
        }

        Request.Builder reqBuilder = new Request.Builder();
        // 添加 url 和 头部信息
        reqBuilder.url(url)
                .addHeader(HEAD_TIMESTAMP, timestamp)
                .addHeader(HEAD_CONTENT_TYPE, CONTENT_TYPE_JSON);

        // GET or POST
        if (bodyBuilder != null) {
            RequestBody requestBody = bodyBuilder.build();
            reqBuilder.post(requestBody);
        } else {
            reqBuilder.get();
        }

        Request request = reqBuilder.build();
        try {
            return mOkHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取当前时间戳
     * 将最后的三位去掉
     *
     * @return
     */
    public long getTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取融云的token
     *
     * @param map
     * @return
     */
    public String postCloudToken(HashMap<String, String> map) {

        // 参数
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = String.valueOf(Math.floor(Math.random() * 1000000));
        String signature = SHA1.sha1(CloudManager.CLOUD_SECRECT + nonce + timeStamp);


        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (String key : map.keySet()) {
            bodyBuilder.add(key, map.get(key));
        }

        RequestBody reqBody = bodyBuilder.build();

        Request request = new Request.Builder()
                .header("App-Key", CloudManager.CLOUD_KEY) // 开发者平台分配的 App Key。
                .header("Nonce", nonce) // 随机数，无长度限制。
                .header("Timestamp", timeStamp) // 时间戳
                .header("Signature", signature) // 数据签名
                .url(CloudManager.RONGCLOUD_GET_TOKEN)
                .post(reqBody)
                .build();

        try {
            return mOkHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }
}
