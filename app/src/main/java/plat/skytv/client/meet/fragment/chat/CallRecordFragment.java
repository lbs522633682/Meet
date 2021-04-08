package plat.skytv.client.meet.fragment.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.liboshuai.framework.base.BaseFragment;

import plat.skytv.client.meet.R;

/**
 * Author:boshuai.li
 * Time:2021/4/8   11:47
 * Description: 通话记录
 */
public class CallRecordFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_record, null);
        return view;
    }
}
