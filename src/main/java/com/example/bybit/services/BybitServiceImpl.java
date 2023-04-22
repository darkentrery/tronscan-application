package com.example.bybit.services;

import com.example.bybit.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BybitServiceImpl implements BybitService{

    @Autowired
    private BybitV1Service bybitV1Service;

    @Autowired
    private BybitV5Service bybitV5Service;

    private final String RECV_WINDOW = "5000";
    private final String URL = "https://api-testnet.bybit.com";
    private String API_KEY;
    private String API_SECRET;
    private String minTimestamp = "0";

    public String getAPI_KEY() {
        return API_KEY;
    }

    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public String getAPI_SECRET() {
        return API_SECRET;
    }

    public void setAPI_SECRET(String API_SECRET) {
        this.API_SECRET = API_SECRET;
    }

    public String getMinTimestamp() {
        return minTimestamp;
    }

    public void setMinTimestamp(String minTimestamp) {
        try {
            ZonedDateTime date = LocalDate.parse(minTimestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneId.of("UTC"));
            long epochMilli = date.toInstant().toEpochMilli();
            this.minTimestamp = Long.toString(epochMilli);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getParameters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("accessKey", this.API_KEY);
        parameters.put("secretKey", this.API_SECRET);
        parameters.put("minTimestamp", this.minTimestamp);
        parameters.put("recvWindow", this.RECV_WINDOW);
        parameters.put("url", this.URL);
        return parameters;
    }

    @Override
    public DealsImportResult getBybitDealImportResult(String API_KEY, String API_SECRET, String startDate) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {
        DealsImportResult result = new DealsImportResult();
        this.setMinTimestamp(startDate);
        if (API_KEY != null && API_SECRET != null) {
            this.setAPI_KEY(API_KEY);
            this.setAPI_SECRET(API_SECRET);
            bybitV1Service.setParameters(this.getParameters());
            bybitV5Service.setParameters(this.getParameters());


            JSONObject v1Balance = bybitV1Service.getV1WalletBalance();
            List<ImportTradeDataHolder> orders = bybitV1Service.getV1Orders();
            List<ImportTradeDataHolder> trades = bybitV1Service.getV1Trades();
            for (ImportTradeDataHolder trade : trades) {
                for (ImportTradeDataHolder order : orders) {
                    if (trade.getTradeSystemId().equals(order.getTradeSystemId())) {
                        trade.setOperation(order.getOperation());
                    }
                }
            }

            List<ImportTradeDataHolder> transactions = bybitV5Service.getTransactions(API_KEY, API_SECRET);
            JSONObject balance = bybitV5Service.getWalletBalance();
            result.extendTransactions(transactions);
            result.setCurrentMoneyRemainders(balance);
            result.extendTransactions(trades);
            if (v1Balance != null) {
                V1BalanceObject v1BalanceObject = new V1BalanceObject(v1Balance);
                result.setCurrentMoneyRemainders(v1BalanceObject);
            }
        }
        return result;
    }
}
