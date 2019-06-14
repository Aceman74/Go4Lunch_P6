package com.aceman.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.aceman.go4lunch.data.nearby_search.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Lionel JOFFRAY - on 14/06/2019.
 */
public class SharedPrefClass {

    public static void saveResultList(Context context, String saveName, String saveListName, List<Result> list) {
        SharedPreferences mMoodSavePref = context.getSharedPreferences(saveName, MODE_PRIVATE);
        SharedPreferences.Editor editor = mMoodSavePref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(saveListName, json);
        editor.apply();
    }

    public static List<Result> loadResultList(Context context, String saveName, String saveListName, List<Result> list) {
        SharedPreferences mMoodSavePref = context.getSharedPreferences(saveName, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mMoodSavePref.getString(saveListName, null);
        Type type = new TypeToken<List<Result>>() {
        }.getType();
        return list = gson.fromJson(json, type);
    }
}
