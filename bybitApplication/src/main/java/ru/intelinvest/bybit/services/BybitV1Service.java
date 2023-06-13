package ru.intelinvest.bybit.services;

import ru.intelinvest.bybit.models.ImportTradeDataHolder;
import ru.intelinvest.bybit.models.bybitResponses.BalanceV1Object;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface BybitV1Service {
    List<ImportTradeDataHolder> getV1Trades();
    List<ImportTradeDataHolder> getV1Orders();
    BalanceV1Object getBalanceObject() throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException;
    void setParameters(Map<String, String> parameters);
}
