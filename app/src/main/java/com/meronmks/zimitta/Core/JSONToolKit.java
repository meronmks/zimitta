package com.meronmks.zimitta.Core;

import com.google.gson.Gson;
import com.meronmks.zimitta.Datas.ErrorLogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by p-user on 2016/10/22.
 */

public class JSONToolKit {
//    public static List<ErrorLogs> JSONtoErrorList(String json){
//        List<ErrorLogs> list = new ArrayList<>();
//        JSONArray jsonArray = new JSONArray();
//        try {
//            jsonArray = new JSONArray(json);
//            for(int i = 0; i < jsonArray.length(); i++){
//                JSONObject object = jsonArray.getJSONObject(i);
//                list.add(object.get());
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public static String ErrorListtoJSON(List<ErrorLogs> list){
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
