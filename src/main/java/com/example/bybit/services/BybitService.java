package com.example.bybit.services;

import com.example.bybit.models.DealsImportResult;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface BybitService {

    public DealsImportResult getBybitDealImportResult(String API_KEY, String API_SECRET, String startDate) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException;
}
