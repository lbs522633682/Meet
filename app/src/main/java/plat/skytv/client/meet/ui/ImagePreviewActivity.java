package plat.skytv.client.meet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.liboshuai.framework.base.BaseUIActivity;
import com.liboshuai.framework.entity.Consts;
import com.liboshuai.framework.helper.GlideHelper;

import java.io.File;

import plat.skytv.client.meet.R;

/**
 * Author:boshuai.li
 * Time:2021/4/10
 * Description:图片预览得acti
 */
public class ImagePreviewActivity extends BaseUIActivity implements View.OnClickListener {


    /**
     *
     * @param context
     * @param isUrl true-url  false-url则是文件路径
     * @param url
     */
    public static void startActivity(Context context, boolean isUrl, String url) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(Consts.INTENT_IMAGE_TYPE, isUrl);
        intent.putExtra(Consts.INTENT_IMAGE_URL, url);
        context.startActivity(intent);
    }

    private PhotoView photo_view;
    private TextView tv_download;
    private ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initView();
    }

    private void initView() {
        photo_view = findViewById(R.id.photo_view);
        tv_download = findViewById(R.id.tv_download);
        iv_back = findViewById(R.id.iv_back);

        iv_back.setOnClickListener(this);

        boolean isUrl = getIntent().getBooleanExtra(Consts.INTENT_IMAGE_TYPE, false);

        String imgUrl = getIntent().getStringExtra(Consts.INTENT_IMAGE_URL);

        if (isUrl) {
            GlideHelper.loadUrl(this, imgUrl, photo_view);
        } else {
            GlideHelper.loadFile(this, new File(imgUrl), photo_view);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
