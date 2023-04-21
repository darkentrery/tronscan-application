package com.example.bybit.services;

import com.example.bybit.models.DealsImportResult;
import com.example.bybit.models.ImportTradeDataHolder;
import com.example.bybit.models.TronTransactionObject;
import com.example.bybit.models.TronTransactionTrc20Object;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TronServiceImpl implements TronService {
    @Autowired
    private ConvertService convertService;

    private String address;
    private final String URL = "https://api.trongrid.io";
    private String minTimestamp = "0";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public JSONObject getTronResponse(String endpoint) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = String.format("%s%s", this.URL, endpoint);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();
        return convertService.getJsonObject(client, request);
    }

    public JSONObject postTronResponse(String endpoint, String address) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = String.format("%s%s", this.URL, endpoint);
        MediaType mediaType = MediaType.parse("application/json");
        String bodyContent = String.format("{\"address\":\"%s\",\"visible\":true}", address);
        RequestBody body = RequestBody.create(mediaType, bodyContent);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("accept", "application/json")
                .build();
        return convertService.getJsonObject(client, request);
    }

    private JSONObject getTransactionsInfoByAddress() {
        String endpoint = String.format("/v1/accounts/%s/transactions?limit=50&min_timestamp=%s", this.address, this.minTimestamp);
        return this.getTronResponse(endpoint);
    }

    private JSONObject getTransactionsInfoByAddress(String fingerprint) {
        String endpoint = String.format("/v1/accounts/%s/transactions?limit=50&min_timestamp=%s&fingerprint=%s", this.address, this.minTimestamp, fingerprint);
        return this.getTronResponse(endpoint);
    }

    private JSONObject getAccountInfo() {
        String endpoint = String.format("/v1/accounts/%s", this.address);
        return this.getTronResponse(endpoint);
    }

    private JSONObject getAssetTrc10Info(String identifier) {
        String endpoint = String.format("/v1/assets/%s", identifier);
        return this.getTronResponse(endpoint);
    }

    private JSONObject getAccount() {
        return this.postTronResponse("/wallet/getaccount", this.address);
    }

    private JSONObject getTrc20TransactionsInfoByAddress() {
        String endpoint = String.format("/v1/accounts/%s/transactions/trc20/?limit=200&min_timestamp=%s", this.address, this.minTimestamp);
        return this.getTronResponse(endpoint);
    }

    private JSONObject getTrc20TransactionsInfoByAddress(String fingerprint) {
        String endpoint = String.format("/v1/accounts/%s/transactions/trc20/?limit=200&min_timestamp=%s&fingerprint=%s", this.address, this.minTimestamp, fingerprint);
        return this.getTronResponse(endpoint);
    }

    private List<JSONObject> getAllTransactionsInfoByAddress() throws InterruptedException, JSONException {
        List<JSONObject> list = new ArrayList<>();
        JSONObject response = this.getTransactionsInfoByAddress();
        list.add(response);
        while (response.getJSONObject("meta").has("fingerprint")) {
            Thread.sleep(400);
            String nextPage = (String) response.getJSONObject("meta").get("fingerprint");
            response = this.getTransactionsInfoByAddress(nextPage);
            list.add(response);
        }
        return list;
    }

    private List<JSONObject> getTrc20AllTransactionsInfoByAddress() throws InterruptedException, JSONException {
        List<JSONObject> list = new ArrayList<>();
        JSONObject response = this.getTrc20TransactionsInfoByAddress();
        list.add(response);
        while (response.getJSONObject("meta").has("fingerprint")) {
            Thread.sleep(500);
            String nextPage = (String) response.getJSONObject("meta").get("fingerprint");
            response = this.getTrc20TransactionsInfoByAddress(nextPage);
            if (response.has("statusCode") && response.getInt("statusCode") == 400) {
                break;
            }
            list.add(response);
        }
        return list;
    }

    private Map<String, BigDecimal> getAccountAssets() throws JSONException, InterruptedException {
        Map<String, BigDecimal> assets = new HashMap<>();
        JSONObject account = this.getAccountInfo();
        JSONArray assetsV2 = account.getJSONArray("data").getJSONObject(0).getJSONArray("assetV2");
        JSONArray assetsTrc20 = account.getJSONArray("data").getJSONObject(0).getJSONArray("trc20");
        for (int i = 0; i < assetsV2.length(); i++) {
            Thread.sleep(200);
            JSONObject assetV2 = assetsV2.getJSONObject(i);
            JSONObject assetInfo = this.getAssetTrc10Info(assetV2.getString("key"));
            String assetName = assetInfo.getJSONArray("data").getJSONObject(0).getString("name");
            assets.put(assetName, new BigDecimal(assetV2.getInt("value")));
        }

        for (int i = 0; i < assetsTrc20.length(); i++) {
            Thread.sleep(200);
            JSONObject assetTrc20 = assetsTrc20.getJSONObject(i);
            String key = assetTrc20.names().getString(0);
            JSONObject assetInfo = this.postTronResponse("/wallet/getaccount", key);
            String assetName = assetInfo.getString("account_name");
            assets.put(assetName, new BigDecimal(assetTrc20.getString(key)));
        }
        return assets;
    }

    private List<ImportTradeDataHolder> getTradeDataHolders() throws JSONException, InterruptedException {
        List<ImportTradeDataHolder> tradeDataHolders = new ArrayList<>();
        List<JSONObject> allTransactionsInfoByAddress = this.getAllTransactionsInfoByAddress();
        for (JSONObject response : allTransactionsInfoByAddress) {
            JSONArray transactions = response.getJSONArray("data");
            for (int i = 0; i < transactions.length(); i++) {
                TronTransactionObject transaction = new TronTransactionObject(transactions.getJSONObject(i));
                ImportTradeDataHolder tradeDataHolder = new ImportTradeDataHolder(transaction);
                tradeDataHolders.add(tradeDataHolder);
            }
        }
        return tradeDataHolders;
    }

    private List<ImportTradeDataHolder> getTrc20TradeDataHolders() throws JSONException, InterruptedException {
        List<ImportTradeDataHolder> tradeDataHolders = new ArrayList<>();
        List<JSONObject> allTransactionsInfoByAddress = this.getTrc20AllTransactionsInfoByAddress();
        for (JSONObject response : allTransactionsInfoByAddress) {
            JSONArray transactions = response.getJSONArray("data");
            for (int i = 0; i < transactions.length(); i++) {
                TronTransactionTrc20Object transaction = new TronTransactionTrc20Object(transactions.getJSONObject(i));
                ImportTradeDataHolder tradeDataHolder = new ImportTradeDataHolder(transaction);
                tradeDataHolders.add(tradeDataHolder);
            }
        }
        return tradeDataHolders;
    }

    @Override
    public DealsImportResult getTronDetailImportResult(String address, String startDate) throws JSONException, InterruptedException {
        DealsImportResult result = new DealsImportResult();
        this.setAddress(address);
        this.setMinTimestamp(startDate);
        Map<String, BigDecimal> assets = this.getAccountAssets();
        result.setCurrentMoneyRemainders(assets);
        List<ImportTradeDataHolder> tradeDataHolders = this.getTradeDataHolders();
        List<ImportTradeDataHolder> trc20TradeDataHolders = this.getTrc20TradeDataHolders();
        List<String> numbers = new ArrayList<>();
        for (ImportTradeDataHolder tradeDataHolder : tradeDataHolders) {
            numbers.add(tradeDataHolder.getTradeSystemId());
            for (ImportTradeDataHolder trc20TradeDataHolder : trc20TradeDataHolders) {
                if (tradeDataHolder.getTradeSystemId().equals(trc20TradeDataHolder.getTradeSystemId())) {
                    tradeDataHolder.setCurrency(trc20TradeDataHolder.getCurrency());
                    tradeDataHolder.setQuantity(trc20TradeDataHolder.getQuantity());
                    break;
                }
            }
        }
        for (ImportTradeDataHolder trc20TradeDataHolder : trc20TradeDataHolders) {
            if (!numbers.contains(trc20TradeDataHolder.getTradeSystemId())) {
                tradeDataHolders.add(trc20TradeDataHolder);
            }
        }
        result.setTransactions(tradeDataHolders);

        return result;
    }
}
