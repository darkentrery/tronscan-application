package com.example.bybit.services;

import com.example.bybit.models.ImportTradeDataHolder;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface BybitV1Service {
    public JSONObject getV1WalletBalance() throws NoSuchAlgorithmException, InvalidKeyException;
    public List<ImportTradeDataHolder> getV1Trades() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException;
    public List<ImportTradeDataHolder> getV1Orders() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException;
    public void setParameters(Map<String, String> parameters);
}
