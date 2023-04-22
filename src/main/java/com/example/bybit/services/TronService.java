package com.example.bybit.services;

import com.example.bybit.models.DealsImportResult;
import org.json.JSONException;


public interface TronService {
    DealsImportResult getTronDetailImportResult(String address, String startDate) throws JSONException, InterruptedException;
}
