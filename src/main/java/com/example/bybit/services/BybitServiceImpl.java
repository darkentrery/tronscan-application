package com.example.bybit.services;

import com.example.bybit.models.DealsImportResult;
import com.example.bybit.models.ImportTradeDataHolder;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BybitServiceImpl implements BybitService{
    @Autowired
    private ConvertService convertService;

    private final String RECV_WINDOW = "5000";

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String genPostSign(String queryString, String timestamp, String API_KEY, String API_SECRET) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(API_SECRET.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        String sb = timestamp + API_KEY + this.RECV_WINDOW + queryString;
        return bytesToHex(sha256_HMAC.doFinal(sb.getBytes()));
    }

    private JSONObject getBybitResponse(String API_KEY, String API_SECRET, String cursor, String endpoint) throws NoSuchAlgorithmException, InvalidKeyException {
        String TIMESTAMP = Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());
//        String queryString = "accountType=UNIFIED&category=spot&currency=BTC";
        String queryString = "accountType=UNIFIED&limit=5";
        if (!cursor.equals("")) {
            queryString += String.format("&cursor=%s", cursor);
        }
        String signature = this.genPostSign(queryString, TIMESTAMP, API_KEY, API_SECRET);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = String.format("%s?%s", endpoint, queryString);
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

    private JSONObject getTransactionLog(String API_KEY, String API_SECRET, String cursor) throws NoSuchAlgorithmException, InvalidKeyException {
        return this.getBybitResponse(API_KEY, API_SECRET, cursor, "https://api-testnet.bybit.com/v5/account/transaction-log");
    }

    private JSONObject getWalletBalance(String API_KEY, String API_SECRET) throws NoSuchAlgorithmException, InvalidKeyException {
        return this.getBybitResponse(API_KEY, API_SECRET, "", "https://api-testnet.bybit.com/v5/account/wallet-balance");
    }

    private List<JSONObject> getAllResponses(String API_KEY, String API_SECRET) throws NoSuchAlgorithmException, InvalidKeyException, JSONException {
        String cursor = "";
        JSONObject jsonTransactions = this.getTransactionLog(API_KEY, API_SECRET, cursor);
        cursor = jsonTransactions.getJSONObject("result").getString("nextPageCursor");
        List<JSONObject> responses = new ArrayList<>();
        responses.add(jsonTransactions);
        while (!cursor.equals("null")) {
            jsonTransactions = this.getTransactionLog(API_KEY, API_SECRET, cursor);
            cursor = jsonTransactions.getJSONObject("result").getString("nextPageCursor");
            responses.add(jsonTransactions);
        }
        return responses;
    }

    private List<ImportTradeDataHolder> getTransactions(String API_KEY, String API_SECRET) throws JSONException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        List<ImportTradeDataHolder> transactions = new ArrayList<>();
        List<JSONObject> responses = this.getAllResponses(API_KEY, API_SECRET);
        for (JSONObject response : responses) {
            Thread.sleep(200);
            JSONArray jsonTransactions = null;
            try {
                jsonTransactions = response.getJSONObject("result").getJSONArray("list");
                for (int i = 0; i < jsonTransactions.length(); i++) {
                    JSONObject transaction = jsonTransactions.getJSONObject(i);
                    ImportTradeDataHolder tradeDataHolder = new ImportTradeDataHolder(transaction);
                    transactions.add(tradeDataHolder);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return transactions;
    }

    @Override
    public DealsImportResult getBybitDealImportResult(String API_KEY, String API_SECRET) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {
        List<ImportTradeDataHolder> transactions = this.getTransactions(API_KEY, API_SECRET);
        JSONObject balance = this.getWalletBalance(API_KEY, API_SECRET);
        DealsImportResult result = new DealsImportResult();
        result.setTransactions(transactions);
        result.setCurrentMoneyRemainders(balance);
        return result;
    };
}
