package com.example.bybit.models;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

public class V5TradeObject extends JSONObject {
    public V5TradeObject(JSONObject object) throws JSONException {
        super(String.valueOf(object));
    }
}
