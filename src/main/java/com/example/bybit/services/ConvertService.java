package com.example.bybit.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface ConvertService {
    public JSONObject getJsonObject(OkHttpClient client, Request request);
    public String genV1Sign(String timestamp, String queryString, String API_SECRET) throws NoSuchAlgorithmException, InvalidKeyException;
    public String genV5Sign(String queryString, String timestamp, String API_KEY, String API_SECRET, String RECV_WINDOW) throws NoSuchAlgorithmException, InvalidKeyException;
}
