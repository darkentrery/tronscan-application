package ru.intelinvest.bybit.services;

import ru.intelinvest.bybit.models.ImportTradeDataHolder;
import ru.intelinvest.bybit.models.bybitResponses.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.RateLimiter;
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
    public List<ImportTradeDataHolder> getV1Trades() {
        List<ImportTradeDataHolder> transactions = new ArrayList<>();
        List<TradesV1Object> tradeList = this.getV1AllTradeHistory();
        for (TradesV1Object tradesV1Object : tradeList) {
            for (TradeV1Object tradeV1Object : tradesV1Object.getResult()) {
                transactions.add(tradeV1Object.toImportTradeDataHolder());
            }
        }
        return transactions;
    }

    @Override
    public BalanceV1Object getBalanceObject() throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        List<String> params = new ArrayList<>();
        params.add(String.format("api_key=%s", this.API_KEY));
        String responseString = this.getResponse("/spot/v1/account", params);
        return (BalanceV1Object) this.getResponseObject(responseString, BalanceV1Object.class);
    }

    @Override
    public List<ImportTradeDataHolder> getV1Orders() {
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

    public List<TradesV1Object> getV1AllTradeHistory() {
        List<TradesV1Object> trades = new ArrayList<>();
        ZonedDateTime startDate = this.getMinTimestampZoneDate();
        ZonedDateTime endDate = ZonedDateTime.now();
        ZonedDateTime useStartDate = endDate.minusMonths(1);
        if (useStartDate.toInstant().toEpochMilli() < startDate.toInstant().toEpochMilli()) {
            useStartDate = startDate;
        }
        RateLimiter rateLimiter = RateLimiter.create(2);
        while (startDate.toInstant().toEpochMilli() < endDate.toInstant().toEpochMilli()) {
            rateLimiter.acquire();
            try {
                TradesV1Object tradesV1Object = this.getTradesObject(useStartDate, endDate);
                trades.add(tradesV1Object);
            } catch (Exception ex) {

            }
            endDate = endDate.minusMonths(1);
            useStartDate = useStartDate.minusMonths(1);
            if (useStartDate.toInstant().toEpochMilli() < startDate.toInstant().toEpochMilli()) {
                useStartDate = startDate;
            }
        }
        return trades;
    }

    public List<OrdersV1Object> getV1AllHistoryOrders() {
        List<OrdersV1Object> orders = new ArrayList<>();
        ZonedDateTime startDate = this.getMinTimestampZoneDate();
        ZonedDateTime endDate = ZonedDateTime.now();
        ZonedDateTime useStartDate = endDate.minusMonths(1);
        if (useStartDate.toInstant().toEpochMilli() < startDate.toInstant().toEpochMilli()) {
            useStartDate = startDate;
        }
        RateLimiter rateLimiter = RateLimiter.create(2);
        while (startDate.toInstant().toEpochMilli() < endDate.toInstant().toEpochMilli()) {
            rateLimiter.acquire();
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

    private OrdersV1Object getOrdersObject(ZonedDateTime startDate, ZonedDateTime endDate) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        List<String> params = new ArrayList<>();
        params.add(String.format("api_key=%s", this.API_KEY));
        params.add(String.format("endTime=%s", endDate.toInstant().toEpochMilli()));
        params.add(String.format("startTime=%s", startDate.toInstant().toEpochMilli()));
        String responseString = this.getResponse("/spot/v1/history-orders", params);
        return (OrdersV1Object) this.getResponseObject(responseString, OrdersV1Object.class);
    }

    private TradesV1Object getTradesObject(ZonedDateTime startDate, ZonedDateTime endDate) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        List<String> params = new ArrayList<>();
        params.add(String.format("api_key=%s", this.API_KEY));
        params.add(String.format("endTime=%s", endDate.toInstant().toEpochMilli()));
        params.add(String.format("startTime=%s", startDate.toInstant().toEpochMilli()));
        String responseString = this.getResponse("/spot/v1/myTrades", params);
        return (TradesV1Object) this.getResponseObject(responseString, TradesV1Object.class);
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
}
