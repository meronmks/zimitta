package com.meronmks.zimitta.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Variable.CoreVariable;
import com.meronmks.zimitta.core.TwitterActionClass;

import java.util.Timer;


public class MentionFragment extends Fragment {

    private Context Activity;
    private TwitterActionClass mtAction;
	private Timer timer;
    private ListView lv;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onActivityCreated(savedInstanceState);

        lv.setAdapter(CoreVariable.mentionTLmAdapter);

		if(mtAction == null) {
			mtAction = new TwitterActionClass(Activity, CoreVariable.mentionTLmAdapter, lv, "Mention", null);
		}

		if(CoreVariable.mentionTLmAdapter.getCount() == 0){
			mtAction.getMention(null);
		}
		CoreVariable.isMentionMenu = true;
	}

 	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Activity = getActivity();
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

		//表示の定期更新
		timer = new Timer();
		InvalidateFragmentClass invalidat = new InvalidateFragmentClass();
		invalidat.invalidate(timer, CoreVariable.mentionTLmAdapter, "Mention",false);

	}

	@Override
	public void onPause() {
		super.onPause();
		timer.cancel();
	}
}
