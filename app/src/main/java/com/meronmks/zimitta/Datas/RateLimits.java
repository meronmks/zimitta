package com.meronmks.zimitta.Datas;

import twitter4j.RateLimitStatus;

/**
 * Created by meron on 2016/09/21.
 */
public class RateLimits {
    private RateLimitStatus Home;
    private RateLimitStatus Mention;

    public RateLimitStatus getHome() {
        return Home;
    }

    public void setHome(RateLimitStatus home) {
        Home = home;
    }

    public RateLimitStatus getMention() {
        return Mention;
    }

    public void setMention(RateLimitStatus mention) {
        Mention = mention;
    }
}
