package com.example.bybit.controllers;

import com.example.bybit.models.DealsImportResult;
import com.example.bybit.services.BybitService;
import com.example.bybit.services.TronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

//

    @GetMapping("/")
    public ResponseEntity<DealsImportResult> getBybitData() throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {
        DealsImportResult response = bybitService.getBybitDealImportResult(API_KEY, API_SECRET);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tron/")
    public DealsImportResult getTronData() throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {

        JSONObject response2 = tronService.getTronResponse(address);

        Map<String, BigDecimal> currentMoneyRemainders = new HashMap<>();
        currentMoneyRemainders.put("1", BigDecimal.valueOf(32435333));
        DealsImportResult deal = new DealsImportResult();

        return new DealsImportResult(currentMoneyRemainders, "jh", false);
    }

}
