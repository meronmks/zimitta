package com.meronmks.zimitta.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Variable.CoreVariable;
import com.meronmks.zimitta.core.CoreActivity;
import com.meronmks.zimitta.core.TwitterActionClass;

import java.util.Timer;


public class UserListTimeLineFragment extends Fragment {

    private ArrayAdapter adapter;
    private Context Activity;
    private TwitterActionClass mtAction;
    private Spinner spinner;
    private Timer timer;
    private ImageButton reload;
    private ListView lv;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        Activity = getActivity();

        adapter = new ArrayAdapter(Activity, android.R.layout.simple_spinner_item);

        lv.setAdapter(CoreVariable.listTLmAdapter);

        if(mtAction == null) {
            mtAction = new TwitterActionClass(Activity, CoreVariable.listTLmAdapter, lv, "UserTimeLineList", spinner);
        }

        mtAction.getUserList(adapter);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.user_list_fragment, container, false);
        //スピナーの取得
        spinner = (Spinner)v.findViewById(R.id.spinner1);

        //ボタンの取得
        reload = (ImageButton)v.findViewById(R.id.listTlReloadButton);

        lv = (ListView)v.findViewById(R.id.userListListView);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // クリックの処理を実行する
                if(spinner.getCount() == 0) return;
                CoreVariable.listTLmAdapter.clear();
                CoreActivity.progresRun();
                mtAction.getListTimeLine(null, spinner.getSelectedItemPosition());
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //表示の定期更新
        timer = new Timer();
        InvalidateFragmentClass invalidat = new InvalidateFragmentClass();
        invalidat.invalidate(timer, CoreVariable.listTLmAdapter, "UserTimeLineList",false);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }
}
