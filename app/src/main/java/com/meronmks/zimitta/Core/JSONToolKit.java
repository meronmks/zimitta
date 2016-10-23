package com.meronmks.zimitta.Core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meronmks.zimitta.Datas.ErrorLogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by p-user on 2016/10/22.
 */

public class JSONToolKit {
    public static List<ErrorLogs> JSONtoErrorList(String json){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<ErrorLogs>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    public static String ErrorListtoJSON(List<ErrorLogs> list){
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
