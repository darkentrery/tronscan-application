package com.example.bybit.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.boot.configurationprocessor.json.JSONObject;

public interface ConvertService {
    public JSONObject getJsonObject(OkHttpClient client, Request request);
}
