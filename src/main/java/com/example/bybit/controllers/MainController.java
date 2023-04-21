package com.example.bybit.controllers;

import com.example.bybit.models.Credentials;
import com.example.bybit.models.DealsImportResult;
import com.example.bybit.services.BybitService;
import com.example.bybit.services.TronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/input")
public class MainController {

    @Autowired
    private BybitService bybitService;

    @Autowired
    private TronService tronService;

    @PostMapping("/credentials")
    public ResponseEntity<DealsImportResult> getBybitData(@RequestBody Credentials credentials) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {
        String apiKey = credentials.getAccessKey();
        String apiSecret = credentials.getSecretKey();
        String startDate = credentials.getStartDate();
        DealsImportResult response = bybitService.getBybitDealImportResult(apiKey, apiSecret, startDate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/credentials/tron")
    public ResponseEntity<DealsImportResult> getTronData(@RequestBody Credentials credentials) throws JSONException, InterruptedException {
        String address = credentials.getAddress();
        String startDate = credentials.getStartDate();
        DealsImportResult response = tronService.getTronDetailImportResult(address, startDate);
        return ResponseEntity.ok(response);
    }
}
