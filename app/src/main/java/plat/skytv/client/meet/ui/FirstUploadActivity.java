package plat.skytv.client.meet.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.liboshuai.framework.base.BaseBackActivity;
import com.liboshuai.framework.helper.BitmapHelper;
import com.liboshuai.framework.helper.FileHelper;
import com.liboshuai.framework.manager.DialogManager;
import com.liboshuai.framework.utils.LogUtils;
import com.liboshuai.framework.view.DialogView;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import plat.skytv.client.meet.R;

/**
 * Author:boshuai.li
 * Time:2020/5/14   14:33
 * Description:This is FirstUploadActivity
 */
public class FirstUploadActivity extends BaseBackActivity implements View.OnClickListener {

    private TextView tv_camera;
    private TextView tv_ablum;
    private TextView tv_cancel;
    private DialogView mPhotoSelectView;

    public static void startActivity(Activity activity, int requestCode) {
        Intent i = new Intent(activity, FirstUploadActivity.class);
        activity.startActivityForResult(i, requestCode);

    }

    // 圆形头像
    private CircleImageView iv_photo;
    private EditText et_nickname;
    private Button btn_upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_upload);
        initView();
    }

    private void initView() {

        initPhotoDialog();

        iv_photo = (CircleImageView) findViewById(R.id.iv_photo);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        btn_upload = (Button) findViewById(R.id.btn_upload);

        iv_photo.setOnClickListener(this);
        btn_upload.setOnClickListener(this);

        // 默认 上传按钮 不可点击
        btn_upload.setEnabled(false);

        et_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btn_upload.setEnabled(uploadFile != null);
                } else {
                    btn_upload.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 初始化上传对话框
     */
    private void initPhotoDialog() {
        mPhotoSelectView = DialogManager.getInstance()
                .initView(this, R.layout.dialog_select_photo, Gravity.BOTTOM);
        tv_camera = (TextView) mPhotoSelectView.findViewById(R.id.tv_camera);
        tv_camera.setOnClickListener(this);
        tv_ablum = (TextView) mPhotoSelectView.findViewById(R.id.tv_ablum);
        tv_ablum.setOnClickListener(this);
        tv_cancel = (TextView) mPhotoSelectView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:

                break;
            case R.id.iv_photo:
                DialogManager.getInstance().show(mPhotoSelectView);
                break;
            case R.id.tv_camera:
                DialogManager.getInstance().hide(mPhotoSelectView);
                // 跳转相机
                FileHelper.getInstance().toCamera(this);
                break;
            case R.id.tv_ablum:
                DialogManager.getInstance().hide(mPhotoSelectView);
                // 跳转相册
                FileHelper.getInstance().toAlbum(this);
                break;
            case R.id.tv_cancel:
                DialogManager.getInstance().hide(mPhotoSelectView);
                break;
        }
    }

    private void submit() {
        // validate
        String nickname = et_nickname.getText().toString().trim();
        if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(this, "nickname不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private File uploadFile = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.i("onActivityResult requestCode = " + requestCode);


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FileHelper.CAMERA_REQUEST_CODE) {
                uploadFile = FileHelper.getInstance().getTempFile();
            } else if (requestCode == FileHelper.ALBUM_REQUEST_CODE) {
                Uri uri = data.getData();
                if (uri != null) {
                    LogUtils.i("onActivityResult path = " + uri.getPath());
                    String imgRealPath = FileHelper.getInstance().getRealImgPathFromUri(this, uri);
                    LogUtils.i("onActivityResult imgRealPath = " + imgRealPath);
                    if (!TextUtils.isEmpty(imgRealPath)) {
                        uploadFile = new File(imgRealPath);
                    }
                }
            }
        }

        // 设置头像
        if (uploadFile != null) {
            LogUtils.i("onActivityResult uploadFile.getPath() = " + uploadFile.getPath());
            // 避免图片过大，引起OOM，进行压缩
            Bitmap bitmap = BitmapHelper.getimage(uploadFile.getPath());
            LogUtils.i("onActivityResult file size = " + uploadFile.length());

            iv_photo.setImageBitmap(bitmap);

            String nickName = et_nickname.getText().toString().trim();
            btn_upload.setEnabled(!TextUtils.isEmpty(nickName));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
