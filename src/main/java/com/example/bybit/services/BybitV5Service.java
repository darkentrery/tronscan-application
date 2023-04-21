package com.example.bybit.services;

import com.example.bybit.models.ImportTradeDataHolder;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface BybitV5Service {
    public List<ImportTradeDataHolder> getTransactions(String API_KEY, String API_SECRET) throws JSONException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException;
    public JSONObject getWalletBalance() throws NoSuchAlgorithmException, InvalidKeyException;
    public void setParameters(Map<String, String> parameters);
}
