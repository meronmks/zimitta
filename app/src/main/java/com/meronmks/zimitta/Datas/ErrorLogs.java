package com.meronmks.zimitta.Datas;

import java.util.Date;

/**
 * Created by meron on 2016/09/28.
 */
public class ErrorLogs {
    public String message;
    public String overview;
    public Date createdAt;

    public static void putErrorLog(String overview, String message){
        ErrorLogs errorLogs = new ErrorLogs();
        Variable.errorLogs.add(errorLogs.getInstance(overview, message));
        if(Variable.errorLogs.getCount() >= 30){
            Variable.errorLogs.remove(Variable.errorLogs.getItem(0));
        }
    }

    private ErrorLogs getInstance(String overview, String message){
        ErrorLogs instance = new ErrorLogs();
        instance.overview = overview;
        instance.message = message;
        instance.createdAt = new Date();

        return instance;
    }


}
