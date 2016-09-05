package com.meronmks.zimitta.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.TwitterActionClass;
import com.meronmks.zimitta.core.TwitterUtils;
import twitter4j.Twitter;

/**
 * Created by meronmks on 2015/04/19.
 */
public class DMSendActivity extends AppCompatActivity {

    private EditText mInputText;
    private Twitter mTwitter;
    private String[] path;
    private Uri[] uri;
    private TextView textCount;
    private SharedPreferences accountIDCount;
    private TwitterActionClass mtAction;
    public long userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_directmessege);

        path = new String[4];
        uri = new Uri[4];

        mtAction = new TwitterActionClass(this);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        accountIDCount = getSharedPreferences(getString(R.string.SelectAccount), 0);
        mTwitter = TwitterUtils.getTwitterInstance(this, accountIDCount.getLong(getString(R.string.SelectAccountNum), 0));

        mInputText = (EditText) findViewById(R.id.input_text);
        textCount = ((TextView)findViewById(R.id.textCount));

        Intent intent = getIntent();
        userID = intent.getLongExtra("mentionID", BIND_ABOVE_CLIENT);

        textCount.setText(Integer.toString(140 - mInputText.getText().length()));

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int textColor = Color.GRAY;

                int txtLength = 140 - s.length();
                textCount.setText(Integer.toString(txtLength) + "");

                if (txtLength < 0) {
                    textColor = Color.RED;
                }
                textCount.setTextColor(textColor);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tweet_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tweetButton:
                SpannableStringBuilder sb = (SpannableStringBuilder)mInputText.getText();
                mtAction.sendDirectMessage(sb.toString(),userID);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
