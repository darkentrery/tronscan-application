package com.example.bybit.services;

import com.example.bybit.models.DealsImportResult;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;


public interface TronService {
    public DealsImportResult getTronDetailImportResult(String address, String startDate) throws JSONException, InterruptedException;
}
