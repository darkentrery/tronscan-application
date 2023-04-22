package com.example.bybit.models;

import org.json.JSONException;
import org.json.JSONObject;

public class TronTransactionObject extends JSONObject {
    public TronTransactionObject(JSONObject object) throws JSONException {
        super(String.valueOf(object));
    }
}
