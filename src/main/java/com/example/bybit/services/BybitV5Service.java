package com.example.bybit.services;

import com.example.bybit.models.ImportTradeDataHolder;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface BybitV5Service {
    List<ImportTradeDataHolder> getTransactions(String API_KEY, String API_SECRET) throws JSONException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException;
    JSONObject getWalletBalance() throws NoSuchAlgorithmException, InvalidKeyException;
    void setParameters(Map<String, String> parameters);
    JSONObject getAny() throws NoSuchAlgorithmException, InvalidKeyException;
}
