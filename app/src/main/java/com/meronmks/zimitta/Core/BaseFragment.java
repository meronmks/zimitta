package com.meronmks.zimitta.Core;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.meronmks.zimitta.Menus.ItemMenu;

import twitter4j.Status;

/**
 * Created by meron on 2016/09/20.
 */
public class BaseFragment extends Fragment {
    protected void showToast(String text){
        if(text == null || text.length() == 0) return;
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        });

    }

    /**
     * メニューの表示
     */
    protected void showMenu(Status status){
        ItemMenu itemMenu = new ItemMenu(getActivity());
        itemMenu.show(status);
    }
}
