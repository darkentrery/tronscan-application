package com.example.bybit.models;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

public class V1BalanceObject extends JSONObject {
    public V1BalanceObject(JSONObject object) throws JSONException {
        super(String.valueOf(object));
    }
}
