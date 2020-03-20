package plat.skytv.client.meet;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;

import com.liboshuai.framework.base.BaseUIActivity;
import com.liboshuai.framework.manager.MediaPlayerManager;
import com.liboshuai.framework.utils.LogUtils;

public class MainActivity extends BaseUIActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MediaPlayerManager mediaPlayerManager = new MediaPlayerManager();
        AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.guide);

        mediaPlayerManager.setOnProgressListener(new MediaPlayerManager.OnMusicProgressListener() {
            @Override
            public void OnProgress(int currentPosition, int poi) {
                LogUtils.i("currentPosition = " + currentPosition + ", poi = " + poi);
            }
        });

        mediaPlayerManager.startPlay(assetFileDescriptor);
    }

}
