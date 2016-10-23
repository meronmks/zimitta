package com.meronmks.zimitta.Datas;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.meronmks.zimitta.Core.JSONToolKit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by meron on 2016/09/28.
 */
public class ErrorLogs {
    public String message;
    public String overview;
    public Date createdAt;
    private static final String ERROR_JSON = "ERROR_JSON_TEXT";

    public static void putErrorLog(String overview, String message){
        ErrorLogs errorLogs = new ErrorLogs();
        Variable.errorLogs.add(errorLogs.getInstance(overview, message));
        if(Variable.errorLogs.getCount() >= 30){
            Variable.errorLogs.remove(Variable.errorLogs.getItem(0));
        }
    }

    public static void saveLog(Context context){
        List<ErrorLogs> list = new ArrayList<>();
        for(int i = 0 ; i < Variable.errorLogs.getCount(); i++){
            list.add(Variable.errorLogs.getItem(i));
        }
        String jsonArray = JSONToolKit.ErrorListtoJSON(list);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(ERROR_JSON, jsonArray).apply();
    }

    public static void loadLog(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sp.getString(ERROR_JSON, "");
        List<ErrorLogs> list = JSONToolKit.JSONtoErrorList(json);
        if(list == null)return;
        Variable.errorLogs.addAll(list);
    }

    private ErrorLogs getInstance(String overview, String message){
        ErrorLogs instance = new ErrorLogs();
        instance.overview = overview;
        instance.message = message;
        instance.createdAt = new Date();

        return instance;
    }
}
