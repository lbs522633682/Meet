package plat.skytv.client.meet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liboshuai.framework.base.BaseFragment;

import plat.skytv.client.meet.R;

/**
 * Author:boshuai.li
 * Time:2020/5/13   11:47
 * Description: 广场
 */
public class SquareFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square, null);
        return view;
    }
}
