package com.example.bybit.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class TronServiceImpl implements TronService {
    @Autowired
    private ConvertService convertService;

    @Override
    public JSONObject getTronResponse(String address) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = String.format("https://api.trongrid.io/v1/accounts/%s/transactions", address);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();
        JSONObject json = convertService.getJsonObject(client, request);
        return json;
    };
}
