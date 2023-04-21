package com.example.bybit.models;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

public class TronTransactionTrc20Object extends JSONObject {
    public TronTransactionTrc20Object(JSONObject object) throws JSONException {
        super(String.valueOf(object));
    }
}
