package com.example.bybit.models;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

public class TronTransactionObject extends JSONObject {
    public TronTransactionObject(JSONObject object) throws JSONException {
        super(String.valueOf(object));
    }
}
