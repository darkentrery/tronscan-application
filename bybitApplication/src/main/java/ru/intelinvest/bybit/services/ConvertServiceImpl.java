package ru.intelinvest.bybit.services;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class ConvertServiceImpl implements ConvertService{
    @Override
    public JSONObject getJsonObject(OkHttpClient client, Request request) {
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            assert response.body() != null;
            String stringToParse = response.body().string();
            JSONObject json = new JSONObject(stringToParse);
            return json;
        }catch (IOException | JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public String genV1Sign(String timestamp, String queryString, String API_SECRET) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(API_SECRET.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return bytesToHex(sha256_HMAC.doFinal(queryString.getBytes()));
    }

    @Override
    public String genV5Sign(String queryString, String timestamp, String API_KEY, String API_SECRET, String RECV_WINDOW) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(API_SECRET.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        String sb = timestamp + API_KEY + RECV_WINDOW + queryString;
        return bytesToHex(sha256_HMAC.doFinal(sb.getBytes()));
    }
}
