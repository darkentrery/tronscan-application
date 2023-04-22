package com.example.bybit.models;

import org.json.JSONException;
import org.json.JSONObject;

public class TronTransactionTrc20Object extends JSONObject {
    public TronTransactionTrc20Object(JSONObject object) throws JSONException {
        super(String.valueOf(object));
    }
}
