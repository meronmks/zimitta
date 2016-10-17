package com.meronmks.zimitta.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.meronmks.zimitta.Core.BaseActivity;
import com.meronmks.zimitta.R;

/**
 * Created by meron on 2016/10/16.
 */

public class AccountChangeActivity extends BaseActivity{

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_list);
        toolbar = (Toolbar)findViewById(R.id.ToolBar);
        toolbar.setTitle("アカウント選択");
        setSupportActionBar(toolbar);

    }
}
