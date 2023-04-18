package com.example.bybit.services;

import org.springframework.boot.configurationprocessor.json.JSONObject;


public interface TronService {
    public JSONObject getTronResponse(String address);
}
