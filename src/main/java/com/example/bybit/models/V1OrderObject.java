package com.example.bybit.models;

import org.json.JSONException;
import org.json.JSONObject;

public class V1OrderObject extends JSONObject {
    public V1OrderObject(JSONObject object) throws JSONException {
        super(String.valueOf(object));
    }
}
