package com.liboshuai.framework;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.liboshuai.framework.utils.LogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author:boshuai.li
 * Time:2020/5/14   15:59
 * Description: 关于文件的帮助类
 */
public class FileHelper {

    // 相机
    public static final int CAMERA_REQUEST_CODE = 1004;
    // 相册
    public static final int ALBUM_REQUEST_CODE = 1005;

    public static volatile FileHelper mInstance;

    private SimpleDateFormat simpleDateFormat;
    private File tempFile; // 中转的文件
    private Uri mImgUri;

    private FileHelper() {
        simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_ss");
    }

    public static FileHelper getInstance() {
        if (mInstance == null) {
            synchronized (FileHelper.class) {
                if (mInstance == null) {
                    mInstance = new FileHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 开启相机
     * @param activity
     */
    public void toCamera(Activity activity) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = simpleDateFormat.format(new Date());
        tempFile = new File(Environment.getExternalStorageDirectory(), fileName + ".jpg");

        // 7.0 以下
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mImgUri = Uri.fromFile(tempFile);
        } else {
            // 7.0 利用FileProvider
            String authority = activity.getPackageName() + ".fileprovider"; // 需要在配置文件中配置
            mImgUri = FileProvider.getUriForFile(activity, authority, tempFile);
            // 添加权限

            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        LogUtils.i("toCamera mImgUri = " + mImgUri);

        i.putExtra(MediaStore.EXTRA_OUTPUT, mImgUri);

        activity.startActivityForResult(i, CAMERA_REQUEST_CODE);
    }

    public File getTempFile() {
        return tempFile;
    }
}
