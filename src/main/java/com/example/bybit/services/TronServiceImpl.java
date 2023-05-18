package com.example.bybit.services;

import com.example.bybit.models.*;
import com.example.bybit.models.troneResponses.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
@Setter
@Service
public class TronServiceImpl implements TronService {
    @Autowired
    private ConvertService convertService;

    private String address;

    @Value("${url.tron}")
    String URL;

    private String minTimestamp = "0";
    private String hexAddress;

    public void setMinTimestamp(String minTimestamp) {
        try {
            ZonedDateTime date = LocalDate.parse(minTimestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneId.of("UTC"));
            long epochMilli = date.toInstant().toEpochMilli();
            this.minTimestamp = Long.toString(epochMilli);
        } catch (Exception e) {
            this.minTimestamp = "0";
        }
    }

    private String getTronResponse(String endpoint) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s%s", this.URL, endpoint);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        return responseEntity.getBody();
    }

    private Object getResponseObject(String response, Class objectClass) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
//        JSONObject json = new JSONObject(response);
        Object readValue = mapper.readValue(response, objectClass);
        return readValue;
    }

//    public JSONObject getTronResponse(String endpoint) {
//        OkHttpClient client = new OkHttpClient().newBuilder().build();
//        String url = String.format("%s%s", this.URL, endpoint);
//        Request request = new Request.Builder()
//                .url(url)
//                .get()
//                .addHeader("Content-Type", "application/json")
//                .build();
//        return convertService.getJsonObject(client, request);
//    }

//    public JSONObject postTronResponse(String endpoint, String address) {
//        OkHttpClient client = new OkHttpClient().newBuilder().build();
//        String url = String.format("%s%s", this.URL, endpoint);
//        MediaType mediaType = MediaType.parse("application/json");
//        String bodyContent = String.format("{\"address\":\"%s\",\"visible\":true}", address);
//        RequestBody body = RequestBody.create(mediaType, bodyContent);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("accept", "application/json")
//                .build();
//        return convertService.getJsonObject(client, request);
//    }

    public String postTronResponse(String endpoint, String address) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject personJsonObject = new JSONObject();
        personJsonObject.put("address", address);
        personJsonObject.put("visible", true);
        HttpEntity<String> request = new HttpEntity<String>(personJsonObject.toString(), headers);
        String url = String.format("%s%s", this.URL, endpoint);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        return responseEntity.getBody();
    }

    private TronResponseObject getTransactionsInfoByAddress() throws JsonProcessingException {
        String endpoint = String.format("/v1/accounts/%s/transactions?limit=50&min_timestamp=%s", this.address, this.minTimestamp);
        String responseString = this.getTronResponse(endpoint);
        return (TronResponseObject) this.getResponseObject(responseString, TronResponseObject.class);
    }

    private TronResponseObject getTransactionsInfoByAddress(String fingerprint) throws JsonProcessingException {
        String endpoint = String.format("/v1/accounts/%s/transactions?limit=50&min_timestamp=%s&fingerprint=%s", this.address, this.minTimestamp, fingerprint);
        String responseString = this.getTronResponse(endpoint);
        return (TronResponseObject) this.getResponseObject(responseString, TronResponseObject.class);
    }

    private TronResponseAccountObject getAccountInfo() throws JsonProcessingException {
        String endpoint = String.format("/v1/accounts/%s", this.address);
        String responseString = this.getTronResponse(endpoint);
        return (TronResponseAccountObject) this.getResponseObject(responseString, TronResponseAccountObject.class);
    }

    private TronAssetTrc10ResponseObject getAssetTrc10Info(String identifier) throws JsonProcessingException {
        String endpoint = String.format("/v1/assets/%s", identifier);
        String responseString = this.getTronResponse(endpoint);
        return (TronAssetTrc10ResponseObject) this.getResponseObject(responseString, TronAssetTrc10ResponseObject.class);
    }

    private TronAssetTrc20ResponseObject getAssetTrc20Info(String address) throws JsonProcessingException {
        String responseString = this.postTronResponse("/wallet/getaccount", address);
        return (TronAssetTrc20ResponseObject) this.getResponseObject(responseString, TronAssetTrc20ResponseObject.class);
    }

//    private JSONObject getAccount() {
//        return this.postTronResponse("/wallet/getaccount", this.address);
//    }

    private TronResponseObject getTrc20TransactionsInfoByAddress() throws JsonProcessingException {
        String endpoint = String.format("/v1/accounts/%s/transactions/trc20/?limit=200&min_timestamp=%s", this.address, this.minTimestamp);
        String responseString = this.getTronResponse(endpoint);
        return (TronResponseObject) this.getResponseObject(responseString, TronResponseObject.class);
    }

    private TronResponseObject getTrc20TransactionsInfoByAddress(String fingerprint) throws JsonProcessingException {
        String endpoint = String.format("/v1/accounts/%s/transactions/trc20/?limit=200&min_timestamp=%s&fingerprint=%s", this.address, this.minTimestamp, fingerprint);
        String responseString = this.getTronResponse(endpoint);
        return (TronResponseObject) this.getResponseObject(responseString, TronResponseObject.class);
    }

    private List<TronResponseObject> getAllTransactionsInfoByAddress() throws InterruptedException, JSONException, JsonProcessingException {
        List<TronResponseObject> list = new ArrayList<>();
        TronResponseObject response = this.getTransactionsInfoByAddress();
        list.add(response);
        while (response.getMeta().getFingerprint() != null) {
            Thread.sleep(400);
            String nextPage = response.getMeta().getFingerprint();
            response = this.getTransactionsInfoByAddress(nextPage);
            if (!response.getSuccess()) {
                break;
            }
            list.add(response);
        }

//        while (response.getJSONObject("meta").has("fingerprint")) {
//            Thread.sleep(400);
//            String nextPage = (String) response.getJSONObject("meta").get("fingerprint");
//            response = this.getTransactionsInfoByAddress(nextPage);
//            if (response.has("statusCode") && response.getInt("statusCode") == 400) {
//                break;
//            }
//            list.add(response);
//        }

        return list;
    }

    private List<TronResponseObject> getTrc20AllTransactionsInfoByAddress() throws InterruptedException, JSONException, JsonProcessingException {
        List<TronResponseObject> list = new ArrayList<>();
        TronResponseObject response = this.getTrc20TransactionsInfoByAddress();
        list.add(response);
        while (response.getMeta().getFingerprint() != null) {
            Thread.sleep(500);
            String nextPage = response.getMeta().getFingerprint();
            response = this.getTrc20TransactionsInfoByAddress(nextPage);
            if (!response.getSuccess()) {
                break;
            }
            list.add(response);
        }
        return list;
    }

    private Map<String, BigDecimal> getAccountAssets() throws JSONException, InterruptedException, JsonProcessingException {
        Map<String, BigDecimal> assets = new HashMap<>();
        TronResponseAccountObject account = this.getAccountInfo();
        this.setHexAddress(account.getData().get(0).getAddress());
        for (int i = 0; i < account.getAssets().size(); i++) {
            Thread.sleep(200);
            AssetV2Object assetV2 = account.getAssets().get(i);
            TronAssetTrc10ResponseObject assetInfo = this.getAssetTrc10Info(assetV2.getKey());
            String assetName = assetInfo.getName();
            assets.put(assetName, new BigDecimal(assetV2.getValue()));
        }

        for (int i = 0; i < account.getAssetsTrc20().size(); i++) {
            Thread.sleep(200);
            AssetTrc20Object assetTrc20 = account.getAssetsTrc20().get(i);
            String key = assetTrc20.getTokenName();
            TronAssetTrc20ResponseObject assetInfo = this.getAssetTrc20Info(key);
            String assetName = assetInfo.getAccount_name();
            assets.put(assetName, new BigDecimal(assetTrc20.getValue()));
        }
        return assets;
    }

    private List<ImportTradeDataHolder> getTradeDataHolders() throws JSONException, InterruptedException, JsonProcessingException {
        List<ImportTradeDataHolder> tradeDataHolders = new ArrayList<>();
        List<TronResponseObject> allTransactionsInfoByAddress = this.getAllTransactionsInfoByAddress();
        for (TronResponseObject response : allTransactionsInfoByAddress) {
            for (TronResponseTransactionObject transaction : response.getData()) {
                tradeDataHolders.add(transaction.toImportTradeDataHolder(this.hexAddress));
            }
        }
        List<ImportTradeDataHolder> newTradeDataHolders = new ArrayList<>();
        Map<String, List<ImportTradeDataHolder>> transactionsId = new HashMap<>();
        tradeDataHolders.forEach(tradeDataHolder -> {
            if (transactionsId.containsKey(tradeDataHolder.getTradeSystemId())) {
                transactionsId.get(tradeDataHolder.getTradeSystemId()).add(tradeDataHolder);
            } else {
                List<ImportTradeDataHolder> array = new ArrayList<>();
                array.add(tradeDataHolder);
                transactionsId.put(tradeDataHolder.getTradeSystemId(), array);
            }
        });
        transactionsId.forEach((id, holders) -> {
            if (holders.size() > 1) {
                newTradeDataHolders.add(new ImportTradeDataHolder(holders.get(0), holders.get(1)));
            } else {
                newTradeDataHolders.add(holders.get(0));
            }
        });
        return newTradeDataHolders;
    }

    private List<ImportTradeDataHolder> getTrc20TradeDataHolders() throws JSONException, InterruptedException, JsonProcessingException {
        List<ImportTradeDataHolder> tradeDataHolders = new ArrayList<>();
        List<TronResponseObject> allTransactionsInfoByAddress = this.getTrc20AllTransactionsInfoByAddress();
        for (TronResponseObject response : allTransactionsInfoByAddress) {
            for (TronResponseTransactionObject transaction : response.getData()) {
                tradeDataHolders.add(transaction.toImportTradeDataHolder(this.address));
            }
        }
        return tradeDataHolders;
    }

    @Override
    public DealsImportResult getTronDetailImportResult(String address, String startDate) throws JSONException, InterruptedException, IOException {
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
                if (tradeDataHolder.getTradeSystemId() != null && tradeDataHolder.getTradeSystemId().equals(trc20TradeDataHolder.getTradeSystemId())) {
                    tradeDataHolder.setCurrency(trc20TradeDataHolder.getCurrency());
                    tradeDataHolder.setQuantity(trc20TradeDataHolder.getQuantity());
                    tradeDataHolder.setOperation(trc20TradeDataHolder.getOperation());
                    break;
                }
            }
        }
        for (ImportTradeDataHolder trc20TradeDataHolder : trc20TradeDataHolders) {
            if (!numbers.contains(trc20TradeDataHolder.getTradeSystemId())) {
                tradeDataHolders.add(trc20TradeDataHolder);
            }
        }
        Collections.sort(tradeDataHolders, Comparator.comparing(ImportTradeDataHolder::getDate).reversed());
        result.setTransactions(tradeDataHolders);

        return result;
    }
}
