package com.meronmks.zimitta.Variable;

/**
 * Created by p-user on 2016/07/19.
 */
public class RateLimitVariable {
    public int HourlyLimit;
    public int RemainingHits;
    public int ResetTimeInSeconds;
    public int SecondsUntilReset;

    public RateLimitVariable(){
        HourlyLimit = 0;
        RemainingHits = 0;
        ResetTimeInSeconds = 0;
        SecondsUntilReset = 0;
    }
}
