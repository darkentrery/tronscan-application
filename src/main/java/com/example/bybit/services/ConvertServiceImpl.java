package com.example.bybit.services;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ConvertServiceImpl implements ConvertService{
    @Override
    public JSONObject getJsonObject(OkHttpClient client, Request request) {
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            assert response.body() != null;
            String stringToParse = response.body().string();
            JSONObject json = new JSONObject(stringToParse);
            return json;
        }catch (IOException | JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
