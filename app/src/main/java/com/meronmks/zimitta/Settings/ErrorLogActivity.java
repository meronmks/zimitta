package com.meronmks.zimitta.Settings;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meronmks.zimitta.Adapter.ErrorAdapter;
import com.meronmks.zimitta.Datas.ErrorLogs;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;

/**
 * Created by meron on 2016/09/23.
 */
public class ErrorLogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.error_log_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolBar);
        toolbar.setTitle("エラーログ");
        setSupportActionBar(toolbar);

        ListView listView = (ListView) findViewById(R.id.ErrorListView);

        listView.setAdapter(Variable.errorLogs);

        Variable.errorLogs.sort((t2, t1) -> t1.createdAt.compareTo(t2.createdAt));

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            ErrorLogs errorLogs = (ErrorLogs) adapterView.getItemAtPosition(i);
            new AlertDialog.Builder(this)
                    .setTitle("詳細")
                    .setMessage(errorLogs.message)
                    .setPositiveButton("閉じる", null)
                    .show();
        });
    }
}
