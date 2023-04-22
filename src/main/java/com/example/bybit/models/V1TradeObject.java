package com.example.bybit.models;

import org.json.JSONException;
import org.json.JSONObject;


public class V1TradeObject extends JSONObject {
    public V1TradeObject(JSONObject object) throws JSONException {
        super(String.valueOf(object));
    }
}
