package com.example.bybit.services;

import com.example.bybit.models.*;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BybitServiceImpl implements BybitService{
    @Autowired
    private ConvertService convertService;

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

    public ZonedDateTime getMinTimestampZoneDate() {
        Instant i = Instant.ofEpochMilli(Long.parseLong(this.minTimestamp));
        ZonedDateTime z = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
        ZonedDateTime now = ZonedDateTime.now();
        if (z.toInstant().toEpochMilli() < now.minusMonths(12).toInstant().toEpochMilli()) {
            z = now.minusMonths(12);
        }
        return z;
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

    private JSONObject getV5Response(String cursor, String endpoint, String queryString) throws NoSuchAlgorithmException, InvalidKeyException {
        String TIMESTAMP = Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());
        if (!cursor.equals("")) {
            queryString += String.format("&cursor=%s", cursor);
        }
        String signature = convertService.genV5Sign(queryString, TIMESTAMP, this.API_KEY, this.API_SECRET, this.RECV_WINDOW);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = String.format("%s%s?%s", this.URL, endpoint, queryString);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("X-BAPI-API-KEY", API_KEY)
                .addHeader("X-BAPI-SIGN", signature)
                .addHeader("X-BAPI-TIMESTAMP", TIMESTAMP)
                .addHeader("X-BAPI-RECV-WINDOW", this.RECV_WINDOW)
                .addHeader("Content-Type", "application/json")
                .build();
        JSONObject json = convertService.getJsonObject(client, request);
        return json;
    }

    private JSONObject getV1Response(String endpoint, List<String> params) throws NoSuchAlgorithmException, InvalidKeyException {
        String TIMESTAMP = Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());
        String queryString = String.join("&", params);
        queryString += String.format("&timestamp=%s", TIMESTAMP);
        String signature = convertService.genV1Sign(TIMESTAMP, queryString, this.API_SECRET);
        queryString += String.format("&sign=%s", signature);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = String.format("%s%s?%s", this.URL, endpoint, queryString);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();
        JSONObject json = convertService.getJsonObject(client, request);
        return json;
    }

    private JSONObject getTransactionLog(String cursor) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {
        String queryString = String.format("accountType=UNIFIED&limit=5&startTime=%s", this.minTimestamp);
        JSONObject transactions = this.getV5Response(cursor, "/v5/account/transaction-log", queryString);
        if (transactions.getInt("retCode") == 10016) {
            Thread.sleep(3000);
            String time = ZonedDateTime.now().toString();
            System.out.println(time);
            return this.getTransactionLog(cursor);
        }
        return transactions;
    }

    private JSONObject getWalletBalance() throws NoSuchAlgorithmException, InvalidKeyException {
        String queryString = "accountType=UNIFIED";
        return this.getV5Response("", "/v5/account/wallet-balance", queryString);
    }

    private JSONObject getV1WalletBalance() throws NoSuchAlgorithmException, InvalidKeyException {
        List<String> params = new ArrayList<>();
        params.add(String.format("api_key=%s", this.API_KEY));
        return this.getV1Response("/spot/v1/account", params);
    }

    private JSONObject getV1TradeHistory(ZonedDateTime startDate, ZonedDateTime endDate) throws NoSuchAlgorithmException, InvalidKeyException {
        List<String> params = new ArrayList<>();
        params.add(String.format("api_key=%s", this.API_KEY));
        params.add(String.format("endTime=%s", Long.toString(endDate.toInstant().toEpochMilli())));
        params.add(String.format("startTime=%s", Long.toString(startDate.toInstant().toEpochMilli())));
        return this.getV1Response("/spot/v1/myTrades", params);
    }

    private List<JSONObject> getV1AllTradeHistory() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        List<JSONObject> trades = new ArrayList<>();
        ZonedDateTime startDate = this.getMinTimestampZoneDate();
        ZonedDateTime endDate = ZonedDateTime.now();
        ZonedDateTime useStartDate = endDate.minusMonths(1);
        if (useStartDate.toInstant().toEpochMilli() < startDate.toInstant().toEpochMilli()) {
            useStartDate = startDate;
        }
        while (startDate.toInstant().toEpochMilli() < endDate.toInstant().toEpochMilli()) {
            Thread.sleep(500);
            JSONObject tradeObject = this.getV1TradeHistory(useStartDate, endDate);
            trades.add(tradeObject);
            endDate = endDate.minusMonths(1);
            useStartDate = useStartDate.minusMonths(1);
            if (useStartDate.toInstant().toEpochMilli() < startDate.toInstant().toEpochMilli()) {
                useStartDate = startDate;
            }
        }
        return trades;
    }

    private JSONObject getV1HistoryOrders(int countMonth) throws NoSuchAlgorithmException, InvalidKeyException {
        String endTime = Long.toString(ZonedDateTime.now().minusMonths(countMonth).toInstant().toEpochMilli());
        String startTime = Long.toString(ZonedDateTime.now().minusMonths(countMonth + 1).toInstant().toEpochMilli());
        List<String> params = new ArrayList<>();
        params.add(String.format("api_key=%s", this.API_KEY));
        params.add(String.format("endTime=%s", endTime));
        params.add(String.format("startTime=%s", startTime));
        return this.getV1Response("/spot/v1/history-orders", params);
    }

    private List<JSONObject> getV1AllHistoryOrders() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        List<JSONObject> orders = new ArrayList<>();
        ZonedDateTime startDate = this.getMinTimestampZoneDate();
        for (int i = 0; i < 12; i++) {
            Thread.sleep(500);
            JSONObject tradeObject = this.getV1HistoryOrders(i);
            orders.add(tradeObject);
        }
        return orders;
    }

    private List<JSONObject> getAllResponses(String API_KEY, String API_SECRET) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {
        String cursor = "";
        List<JSONObject> responses = new ArrayList<>();
        JSONObject jsonTransactions = this.getTransactionLog(cursor);
        if (jsonTransactions.getInt("retCode") == 0) {
            cursor = jsonTransactions.getJSONObject("result").getString("nextPageCursor");
            responses.add(jsonTransactions);
            while (!cursor.equals("null")) {
                Thread.sleep(500);
                jsonTransactions = this.getTransactionLog(cursor);
                cursor = jsonTransactions.getJSONObject("result").getString("nextPageCursor");
                responses.add(jsonTransactions);
            }
        }
        return responses;
    }

    private List<ImportTradeDataHolder> getTransactions(String API_KEY, String API_SECRET) throws JSONException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        List<ImportTradeDataHolder> transactions = new ArrayList<>();
        List<JSONObject> responses = this.getAllResponses(API_KEY, API_SECRET);
        for (JSONObject response : responses) {
            try {
                JSONArray jsonTransactions = response.getJSONObject("result").getJSONArray("list");
                for (int i = 0; i < jsonTransactions.length(); i++) {
                    V5TradeObject transaction = new V5TradeObject(jsonTransactions.getJSONObject(i));
                    ImportTradeDataHolder tradeDataHolder = new ImportTradeDataHolder(transaction);
                    transactions.add(tradeDataHolder);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return transactions;
    }

    private List<ImportTradeDataHolder> getV1Trades() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        List<ImportTradeDataHolder> transactions = new ArrayList<>();
        List<JSONObject> tradeList = this.getV1AllTradeHistory();
        for (JSONObject trade : tradeList) {
            try {
                JSONArray jsonTransactions = trade.getJSONArray("result");
                for (int i = 0; i < jsonTransactions.length(); i++) {
                    JSONObject transaction = jsonTransactions.getJSONObject(i);
                    V1TradeObject tradeObject = new V1TradeObject(transaction);
                    ImportTradeDataHolder tradeDataHolder = new ImportTradeDataHolder(tradeObject);
                    transactions.add(tradeDataHolder);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return transactions;
    }

    private List<ImportTradeDataHolder> getV1Orders() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        List<ImportTradeDataHolder> transactions = new ArrayList<>();
        List<JSONObject> ordersList = this.getV1AllHistoryOrders();
        for (JSONObject order : ordersList) {
            try {
                JSONArray jsonTransactions = order.getJSONArray("result");
                for (int i = 0; i < jsonTransactions.length(); i++) {
                    JSONObject transaction = jsonTransactions.getJSONObject(i);
                    if (!transaction.getString("status").equals("CANCELED")) {
                        V1OrderObject tradeObject = new V1OrderObject(transaction);
                        ImportTradeDataHolder tradeDataHolder = new ImportTradeDataHolder(tradeObject);
                        transactions.add(tradeDataHolder);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println(order);
            }
        }
        return transactions;
    }

    @Override
    public DealsImportResult getBybitDealImportResult(String API_KEY, String API_SECRET, String startDate) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {
        DealsImportResult result = new DealsImportResult();
        this.setMinTimestamp(startDate);
        if (API_KEY != null && API_SECRET != null) {
            this.setAPI_KEY(API_KEY);
            this.setAPI_SECRET(API_SECRET);

            JSONObject v1Balance = this.getV1WalletBalance();
            List<ImportTradeDataHolder> orders = this.getV1Orders();
            List<ImportTradeDataHolder> trades = this.getV1Trades();
            for (ImportTradeDataHolder trade : trades) {
                for (ImportTradeDataHolder order : orders) {
                    if (trade.getTradeSystemId().equals(order.getTradeSystemId())) {
                        trade.setOperation(order.getOperation());
                    }
                }
            }

            List<ImportTradeDataHolder> transactions = this.getTransactions(API_KEY, API_SECRET);
            JSONObject balance = this.getWalletBalance();
            result.extendTransactions(transactions);
            result.setCurrentMoneyRemainders(balance);
            result.extendTransactions(trades);
            if (v1Balance != null) {
                V1BalanceObject v1BalanceObject = new V1BalanceObject(v1Balance);
                result.setCurrentMoneyRemainders(v1BalanceObject);
            }
        }
        return result;
    };
}
