package com.example.bybit.models;

import org.json.JSONException;
import org.json.JSONObject;

public class V1BalanceObject extends JSONObject {
    public V1BalanceObject(JSONObject object) throws JSONException {
        super(String.valueOf(object));
    }
}
