package com.meronmks.zimitta.TwitterUtil;

import twitter4j.Status;
import twitter4j.UserStreamAdapter;

/**
 * Created by meron on 2016/09/14.
 */
public class StreamAdapter extends UserStreamAdapter {

    @Override
    public void onStatus(Status status) {
        super.onStatus(status);
    }
}
