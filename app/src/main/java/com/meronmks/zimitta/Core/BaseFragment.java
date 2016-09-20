package com.meronmks.zimitta.Core;

import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by meron on 2016/09/20.
 */
public class BaseFragment extends Fragment {
    protected void showToast(String text){
        if(text == null || text.length() == 0) return;
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
