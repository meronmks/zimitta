package com.meronmks.zimitta.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.meronmks.zimitta.Fragments.DirectMessageFragment;
import com.meronmks.zimitta.Fragments.HomeFragment;
import com.meronmks.zimitta.Fragments.MentionFragment;
import com.meronmks.zimitta.Fragments.SearchTweetFramgent;
import com.meronmks.zimitta.Fragments.UserListFragment;

/**
 * Created by meron on 2016/09/14.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new MentionFragment();
            case 2:
                return new UserListFragment();
            case 3:
                return new DirectMessageFragment();
            default:
                return new SearchTweetFramgent();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Home";
            case 1:
                return "Mention" ;
            case 2:
                return "List";
            case 3:
                return "DM";
            default:
                return "Search";
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
