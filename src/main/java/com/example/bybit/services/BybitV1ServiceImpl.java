package com.example.bybit.services;

import com.example.bybit.models.ImportTradeDataHolder;
import com.example.bybit.models.V1OrderObject;
import com.example.bybit.models.V1TradeObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BybitV1ServiceImpl extends BybitAbstractService implements BybitV1Service{
    @Override
    public JSONObject getV1WalletBalance() throws NoSuchAlgorithmException, InvalidKeyException {
        List<String> params = new ArrayList<>();
        params.add(String.format("api_key=%s", this.API_KEY));
        return this.getV1Response("/spot/v1/account", params);
    }

    @Override
    public List<ImportTradeDataHolder> getV1Trades() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
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

    public List<JSONObject> getV1AllTradeHistory() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
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

    @Override
    public List<ImportTradeDataHolder> getV1Orders() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
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

    private JSONObject getV1Response(String endpoint, List<String> params) throws NoSuchAlgorithmException, InvalidKeyException {
        String TIMESTAMP = Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());
        String queryString = String.join("&", params);
        queryString += String.format("&timestamp=%s", TIMESTAMP);
        String signature = this.convertService.genV1Sign(TIMESTAMP, queryString, this.API_SECRET);
        queryString += String.format("&sign=%s", signature);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = String.format("%s%s?%s", this.URL, endpoint, queryString);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();
        return convertService.getJsonObject(client, request);
    }

    private JSONObject getV1TradeHistory(ZonedDateTime startDate, ZonedDateTime endDate) throws NoSuchAlgorithmException, InvalidKeyException {
        List<String> params = new ArrayList<>();
        params.add(String.format("api_key=%s", this.API_KEY));
        params.add(String.format("endTime=%s", endDate.toInstant().toEpochMilli()));
        params.add(String.format("startTime=%s", startDate.toInstant().toEpochMilli()));
        return this.getV1Response("/spot/v1/myTrades", params);
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

    public List<JSONObject> getV1AllHistoryOrders() throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        List<JSONObject> orders = new ArrayList<>();
        ZonedDateTime startDate = this.getMinTimestampZoneDate();
        for (int i = 0; i < 12; i++) {
            Thread.sleep(500);
            JSONObject tradeObject = this.getV1HistoryOrders(i);
            orders.add(tradeObject);
        }
        return orders;
    }
}
