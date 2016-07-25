package com.meronmks.zimitta.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Variable.CoreVariable;
import com.meronmks.zimitta.core.CoreActivity;
import com.meronmks.zimitta.core.TwitterActionClass;

import java.util.Timer;

/**
 * Created by p-user on 2015/06/03.
 */
public class DirectMessageTLFragment extends Fragment {
    private Context Activity;
    private TwitterActionClass mtAction;
    private Timer timer;
    private ListView lv;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity = getActivity();

        lv.setAdapter(CoreVariable.DirectMessageAdapter);

        if(mtAction == null) {
            mtAction = new TwitterActionClass(Activity, CoreVariable.DirectMessageAdapter, lv, "DM", null);
        }

        if(CoreVariable.DirectMessageAdapter.getCount() == 0){
            CoreActivity.progresRun();
            mtAction.getDirectMessage(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //表示の定期更新
        timer = new Timer();
        InvalidateFragmentClass invalidat = new InvalidateFragmentClass();
        invalidat.invalidate(timer, CoreVariable.DirectMessageAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.core_fragment, container, false);

        lv = (ListView)v.findViewById(R.id.coreListView);

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }
}
