package com.example.bybit.services;

import com.example.bybit.models.ImportTradeDataHolder;
import com.example.bybit.models.V1OrderObject;
import com.example.bybit.models.V1TradeObject;
import com.example.bybit.models.bybitResponses.BalanceV1Object;
import com.example.bybit.models.bybitResponses.OrderV1Object;
import com.example.bybit.models.bybitResponses.OrdersV1Object;
import com.example.bybit.models.troneResponses.TronResponseObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    @Override
    public List<ImportTradeDataHolder> getV1Orders() throws InterruptedException {
        List<ImportTradeDataHolder> transactions = new ArrayList<>();
        List<OrdersV1Object> ordersList = this.getV1AllHistoryOrders();
        for (OrdersV1Object ordersV1Object : ordersList) {
            for (OrderV1Object orderV1Object : ordersV1Object.getResult()) {
                if (!orderV1Object.getStatus().equals("CANCELED")) {
                    transactions.add(orderV1Object.toImportTradeDataHolder());
                }
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

    @Override
    public BalanceV1Object getBalanceObject() throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        List<String> params = new ArrayList<>();
        params.add(String.format("api_key=%s", this.API_KEY));
        String responseString = this.getResponse("/spot/v1/account", params);
        return (BalanceV1Object) this.getResponseObject(responseString, BalanceV1Object.class);
    }

    private OrdersV1Object getOrdersObject(ZonedDateTime startDate, ZonedDateTime endDate) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        List<String> params = new ArrayList<>();
        params.add(String.format("api_key=%s", this.API_KEY));
        params.add(String.format("endTime=%s", endDate.toInstant().toEpochMilli()));
        params.add(String.format("startTime=%s", startDate.toInstant().toEpochMilli()));
        String responseString = this.getResponse("/spot/v1/history-orders", params);
        return (OrdersV1Object) this.getResponseObject(responseString, OrdersV1Object.class);
    }

    private String getResponse(String endpoint, List<String> params) throws NoSuchAlgorithmException, InvalidKeyException {
        String TIMESTAMP = Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());
        String queryString = String.join("&", params);
        queryString += String.format("&timestamp=%s", TIMESTAMP);
        String signature = this.convertService.genV1Sign(TIMESTAMP, queryString, this.API_SECRET);
        queryString += String.format("&sign=%s", signature);
        String url = String.format("%s%s?%s", this.URL, endpoint, queryString);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        return responseEntity.getBody();
    }

    private JSONObject getV1TradeHistory(ZonedDateTime startDate, ZonedDateTime endDate) throws NoSuchAlgorithmException, InvalidKeyException {
        List<String> params = new ArrayList<>();
        params.add(String.format("api_key=%s", this.API_KEY));
        params.add(String.format("endTime=%s", endDate.toInstant().toEpochMilli()));
        params.add(String.format("startTime=%s", startDate.toInstant().toEpochMilli()));
        return this.getV1Response("/spot/v1/myTrades", params);
    }

    public List<OrdersV1Object> getV1AllHistoryOrders() throws InterruptedException {
        List<OrdersV1Object> orders = new ArrayList<>();
        ZonedDateTime startDate = this.getMinTimestampZoneDate();
        ZonedDateTime endDate = ZonedDateTime.now();
        ZonedDateTime useStartDate = endDate.minusMonths(1);
        if (useStartDate.toInstant().toEpochMilli() < startDate.toInstant().toEpochMilli()) {
            useStartDate = startDate;
        }
        while (startDate.toInstant().toEpochMilli() < endDate.toInstant().toEpochMilli()) {
            Thread.sleep(500);
            try {
                OrdersV1Object ordersV1Object = this.getOrdersObject(useStartDate, endDate);
                orders.add(ordersV1Object);
            } catch (Exception ex) {

            }
            endDate = endDate.minusMonths(1);
            useStartDate = useStartDate.minusMonths(1);
            if (useStartDate.toInstant().toEpochMilli() < startDate.toInstant().toEpochMilli()) {
                useStartDate = startDate;
            }
        }
        return orders;
    }
}
