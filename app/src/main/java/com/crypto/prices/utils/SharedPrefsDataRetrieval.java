package com.crypto.prices.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SharedPrefsDataRetrieval {

    public List<CurrencyData> getData(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        String json = sharedPref.getString("currencyList", null);
        Type type = new TypeToken<List<CurrencyData>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }
}
