package com.example.bybit.controllers;

import com.example.bybit.models.Credentials;
import com.example.bybit.models.DealsImportResult;
import com.example.bybit.services.BybitService;
import com.example.bybit.services.TronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping()
//@CrossOrigin(origins = "http://127.0.0.1:8080")

public class MainController {

    @Autowired
    private BybitService bybitService;

    @Autowired
    private TronService tronService;

    final static String address = "TASUAUKXCqvwYjesEWv22pFjRsCeF4NKot";

    @PostMapping("/")
    public ResponseEntity<DealsImportResult> getBybitData(@RequestBody Credentials credentials) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {
        String apiKey = credentials.getAccessKey();
        String apiSecret = credentials.getSecretKey();
        DealsImportResult response = bybitService.getBybitDealImportResult(apiKey, apiSecret);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tron")
    public DealsImportResult getTronData() throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {

        JSONObject response2 = tronService.getTronResponse(address);

        Map<String, BigDecimal> currentMoneyRemainders = new HashMap<>();
        currentMoneyRemainders.put("1", BigDecimal.valueOf(32435333));
        DealsImportResult deal = new DealsImportResult();

        return new DealsImportResult(currentMoneyRemainders, "jh", false);
    }

}
