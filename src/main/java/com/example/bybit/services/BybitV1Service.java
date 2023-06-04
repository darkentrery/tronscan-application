package com.example.bybit.services;

import com.example.bybit.models.ImportTradeDataHolder;
import com.example.bybit.models.bybitResponses.BalanceV1Object;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface BybitV1Service {
    JSONObject getV1WalletBalance() throws NoSuchAlgorithmException, InvalidKeyException;
    List<ImportTradeDataHolder> getV1Trades() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException;
    List<ImportTradeDataHolder> getV1Orders() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException, JsonProcessingException;
    BalanceV1Object getBalanceObject() throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException;
    void setParameters(Map<String, String> parameters);
}
