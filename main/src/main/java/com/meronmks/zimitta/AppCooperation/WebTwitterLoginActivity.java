package com.meronmks.zimitta.AppCooperation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.meronmks.zimitta.R;


public class WebTwitterLoginActivity extends AppCompatActivity implements OnClickListener {

	private EditText mailText;
	private EditText passText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webloginlayout);

		getSupportActionBar().setDisplayShowHomeEnabled(false);	//ActionBarからアイコンを消す

        //ボタンのクリックリスナー設定
        Button login = (Button)findViewById(R.id.listTlReloadButton);
        login.setOnClickListener(WebTwitterLoginActivity.this);

        mailText = (EditText)findViewById(R.id.editText1);
		passText = (EditText)findViewById(R.id.editText2);

	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		switch(v.getId()){
        case R.id.listTlReloadButton:
        	Intent SettingsApplications = new Intent(WebTwitterLoginActivity.this, ApplicationsCooperationActivity.class);
        	SettingsApplications.putExtra("meil", mailText.getText().toString());
        	SettingsApplications.putExtra("pass", passText.getText().toString());
    		startActivity(SettingsApplications);
        	break;
		}
	}

}
