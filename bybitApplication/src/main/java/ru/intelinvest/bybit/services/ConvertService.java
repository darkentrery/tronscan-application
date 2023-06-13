package ru.intelinvest.bybit.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface ConvertService {
    JSONObject getJsonObject(OkHttpClient client, Request request);
    String genV1Sign(String timestamp, String queryString, String API_SECRET) throws NoSuchAlgorithmException, InvalidKeyException;
    String genV5Sign(String queryString, String timestamp, String API_KEY, String API_SECRET, String RECV_WINDOW) throws NoSuchAlgorithmException, InvalidKeyException;
    String bytesToHex(byte[] hash);
}
