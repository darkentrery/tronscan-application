package com.example.bybit.models;

import org.json.JSONException;
import org.json.JSONObject;

public class V5TradeObject extends JSONObject {
    public V5TradeObject(JSONObject object) throws JSONException {
        super(String.valueOf(object));
    }
}
