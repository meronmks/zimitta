package com.meronmks.zimitta.Datas;

import android.view.MenuItem;

/**
 * Created by meron on 2016/12/15.
 */

public class MenuItems {
    public String name;
    public Tags tag;

    public enum Tags{
        Detail,
        Replay,
        RT,
        Fav,
        RTandFav,
        User,
        HashTag,
        Delete,
        Share
    }

    public MenuItems getInstans(String name, Tags tags){
        this.name = name;
        this.tag = tags;
        return this;
    }
}
