package com.meronmks.zimitta.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.meronmks.zimitta.Fragments.*;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Variable.CoreVariable;

public class MainTabFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;

	public MainTabFragmentPagerAdapter(FragmentManager fm,Context c) {
		super(fm);
        context = c;
	}

    /**
     * どのタブにどのfragmentを適応するかを決めるメソッド
     * @param position
     * @return 対象にするfragment
     */
	@Override
	public Fragment getItem(int position) {
        if(isTabletMode()){
            switch (position) {
                case 0:
                    return new MentionFragment();
                case 1:
                    return new UserListTimeLineFragment();
                case 2:
                    return new DirectMessageTLFragment();
                default:
                    return new SearchTweetFramgent();
            }
        }else {
            switch (position) {
                case 0:
                    return new TimeLineFragment();
                case 1:
                    return new MentionFragment();
                case 2:
                    return new UserListTimeLineFragment();
                case 3:
                    return new DirectMessageTLFragment();
                default:
                    return new SearchTweetFramgent();
            }
        }
	}

    /**
     * タブを作る数を返すメソッド
     * @return 作成するタブ数
     */
	@Override
	public int getCount() {
        if(isTabletMode()) {
            return 4;
        }else{
            return 5;
        }
	}

    /**
     * タブのタイトル設定
     * @param position
     * @return 各タブの名称
     */
	@Override
	public CharSequence getPageTitle(int position) {
        if (isTabletMode()) {
            switch (position) {
                case 0:
                    return "Mention";
                case 1:
                    return "List";
                case 2:
                    return "DM";
                default:
                    return "Search";
            }
        } else {
            switch (position) {
                case 0:
                    return "Home API:" + CoreVariable.HomeTimeline.RemainingHits + "/" + CoreVariable.HomeTimeline.HourlyLimit;
                case 1:
                    return "Mention API:" + CoreVariable.MentionsTimeline.RemainingHits + "/" + CoreVariable.MentionsTimeline.HourlyLimit;
                case 2:
                    return "List API:" + CoreVariable.UserListStatuses.RemainingHits + "/" + CoreVariable.UserListStatuses.HourlyLimit;
                case 3:
                    return "DM";
                default:
                    return "Search";
            }
        }
    }

    /**
     * タブレットかどうか判断
     */
    private boolean isTabletMode(){
        return context.getResources().getBoolean(R.bool.is_tablet);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
