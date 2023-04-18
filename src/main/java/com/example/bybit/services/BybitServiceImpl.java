package com.example.bybit.services;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

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
        System.out.println(timestamp);
        System.out.println(sb);
        return bytesToHex(sha256_HMAC.doFinal(sb.getBytes()));
    }

    @Override
    public JSONObject getBybitResponse(String API_KEY, String API_SECRET) throws NoSuchAlgorithmException, InvalidKeyException {
        String TIMESTAMP = Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());
        Map<String, Object> map = new HashMap<>();
        map.put("category", "linear");
        map.put("accountType", "UNIFIED");
        map.put("currency", "USDT");
//        map.put("symbol", "BTCUSDT");
//        map.put("side", "Buy");
//        map.put("orderType", "Limit");
//        map.put("qty", "0.01");
//        map.put("price", "17900");
//        map.put("timeInForce", "GoodTillCancel");
        String queryString = "accountType=UNIFIED&category=linear&currency=USDT";
        String signature = this.genPostSign(queryString, TIMESTAMP, API_KEY, API_SECRET);

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = String.format("https://api.bybit.com/v5/account/transaction-log?%s", queryString);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("X-BAPI-API-KEY", API_KEY)
                .addHeader("X-BAPI-SIGN", signature)
                .addHeader("X-BAPI-SIGN-TYPE", "2")
                .addHeader("X-BAPI-TIMESTAMP", TIMESTAMP)
                .addHeader("X-BAPI-RECV-WINDOW", this.RECV_WINDOW)
                .addHeader("Content-Type", "application/json")
                .build();
        JSONObject json = convertService.getJsonObject(client, request);
        return json;
    }
}
