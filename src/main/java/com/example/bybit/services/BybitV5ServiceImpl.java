package com.example.bybit.services;

import com.example.bybit.models.ImportTradeDataHolder;
import com.example.bybit.models.V5TradeObject;
import com.example.bybit.models.bybitResponses.BalanceV1Object;
import com.example.bybit.models.bybitResponses.BalanceV5Object;
import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BybitV5ServiceImpl extends BybitAbstractService implements BybitV5Service{
    @Override
    public List<ImportTradeDataHolder> getTransactions(String API_KEY, String API_SECRET) throws JSONException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        List<ImportTradeDataHolder> transactions = new ArrayList<>();
        List<JSONObject> responses = this.getAllResponses();
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

    @Override
    public JSONObject getWalletBalance() throws NoSuchAlgorithmException, InvalidKeyException {
        String queryString = "accountType=UNIFIED";
        return this.getV5Response("", "/v5/account/wallet-balance", queryString);
    }

    @Override
    public JSONObject getAny() throws NoSuchAlgorithmException, InvalidKeyException {
//        OkHttpClient client = new OkHttpClient().newBuilder().build();
//        String url = String.format("%s%s", this.URL, "/s1/byfi/query-orders");
//        MediaType mediaType = MediaType.parse("application/json");
//        String bodyContent = String.format("{\"address\":\"%s\",\"visible\":true}");
//        RequestBody body = RequestBody.create(mediaType, bodyContent);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("accept", "application/json")
//                .build();
//        return convertService.getJsonObject(client, request);
        String queryString = "category=linear&symbol=ETH";
        return this.getV5Response("", "/v5/market/funding/history", queryString);
    }

    @Override
    public BalanceV5Object getBalanceObject() throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException {
        List<String> params = new ArrayList<>();
        params.add("accountType=UNIFIED");
        String responseString = this.getResponse("/v5/account/wallet-balance", params);
        return (BalanceV5Object) this.getResponseObject(responseString, BalanceV5Object.class);
    }

    private List<JSONObject> getAllResponses() throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {
        String cursor = "";
        List<JSONObject> responses = new ArrayList<>();
        JSONObject jsonTransactions = this.getTransactionLog(cursor);
        if (jsonTransactions.getInt("retCode") == 0) {
            responses.add(jsonTransactions);
            while (jsonTransactions.getJSONObject("result").get("nextPageCursor") != JSONObject.NULL) {
                cursor = jsonTransactions.getJSONObject("result").getString("nextPageCursor");
                Thread.sleep(500);
                jsonTransactions = this.getTransactionLog(cursor);
                responses.add(jsonTransactions);
            }
        }
        return responses;
    }

    private JSONObject getTransactionLog(String cursor) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {
        String queryString = String.format("accountType=UNIFIED&limit=50&startTime=%s", this.minTimestamp);
        JSONObject transactions = this.getV5Response(cursor, "/v5/account/transaction-log", queryString);
        if (transactions.getInt("retCode") == 10016) {
            Thread.sleep(3000);
            String time = ZonedDateTime.now().toString();
            System.out.println(time);
            return this.getTransactionLog(cursor);
        }
        return transactions;
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
        return convertService.getJsonObject(client, request);
    }

    private String getResponse(String endpoint, List<String> params) throws NoSuchAlgorithmException, InvalidKeyException {
        String TIMESTAMP = Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());
        String queryString = String.join("&", params);
        queryString += String.format("&timestamp=%s", TIMESTAMP);
//        String signature = this.convertService.genV1Sign(TIMESTAMP, queryString, this.API_SECRET);
//        queryString += String.format("&sign=%s", signature);
//        if (!cursor.equals("")) {
//            queryString += String.format("&cursor=%s", cursor);
//        }
        String signature = convertService.genV5Sign(queryString, TIMESTAMP, this.API_KEY, this.API_SECRET, this.RECV_WINDOW);

        String url = String.format("%s%s?%s", this.URL, endpoint, queryString);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-BAPI-API-KEY", API_KEY);
        headers.set("X-BAPI-SIGN", signature);
        headers.set("X-BAPI-TIMESTAMP", TIMESTAMP);
        headers.set("X-BAPI-RECV-WINDOW", this.RECV_WINDOW);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return responseEntity.getBody();
    }
}
