package com.meronmks.zimitta.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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


public class TimeLineFragment extends Fragment {

    private Context Activity;
    private static TwitterActionClass mtAction;
    private Timer timer;
    private ListView lv;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        Activity = getActivity();

        lv.setAdapter(CoreVariable.TLmAdapter);

        if(mtAction == null) {
            mtAction = new TwitterActionClass(Activity, CoreVariable.TLmAdapter, lv, "TL", null);
        }

        if(CoreVariable.TLmAdapter.getCount() == 0){
            mtAction.getRateLimitStatus();
            CoreActivity.progresRun();
            mtAction.getTimeLine(null);
        }
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.core_fragment, container, false);

        lv = (ListView)v.findViewById(R.id.coreListView);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Activity);

        //表示の定期更新
        timer = new Timer();
        InvalidateFragmentClass invalidat = new InvalidateFragmentClass();
        invalidat.invalidate(timer, CoreVariable.TLmAdapter, "TL",sp.getBoolean("Streeming_stok", false));

        if(sp.getBoolean("Streem_Flug",false)){
            StreamingStart();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();

    }

    public static void StreamingStart(){
        if(mtAction!=null) {
            mtAction.startStreaming();
        }
    }

    public static void StreamingStop(){
        if(mtAction!=null) {
            mtAction.stopStreaming();
        }
    }
}
