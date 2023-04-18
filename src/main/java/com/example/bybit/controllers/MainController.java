package com.example.bybit.controllers;

import com.example.bybit.models.DealsImportResult;
import com.example.bybit.services.BybitService;
import com.example.bybit.services.TronService;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
//@RequestMapping("/")
//@CrossOrigin(origins = "http://localhost:3000")

public class MainController {

    @Autowired
    private BybitService bybitService;

    @Autowired
    private TronService tronService;

    final static String API_KEY = "";
    final static String API_SECRET = "";
    final static String OPTIMIST_API_KEY = "";
    final static String address = "";

    @GetMapping("/")
    public DealsImportResult getAllArticles() throws NoSuchAlgorithmException, InvalidKeyException {
//        JSONObject response = bybitService.getBybitResponse(API_KEY, API_SECRET);

        JSONObject response = tronService.getTronResponse(address);

        Map<String, BigDecimal> currentMoneyRemainders = new HashMap<>();
        currentMoneyRemainders.put("1", BigDecimal.valueOf(32435333));
        DealsImportResult deal = new DealsImportResult();

        return new DealsImportResult(currentMoneyRemainders, "jh", false);
    }

}
