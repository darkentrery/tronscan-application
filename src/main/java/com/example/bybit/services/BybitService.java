package com.example.bybit.services;

import okhttp3.Response;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface BybitService {

    public JSONObject getBybitResponse(String API_KEY, String API_SECRET) throws NoSuchAlgorithmException, InvalidKeyException;
}
