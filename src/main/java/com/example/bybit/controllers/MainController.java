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
@RequestMapping()
//@CrossOrigin(origins = "http://127.0.0.1:8080")

public class MainController {

    @Autowired
    private BybitService bybitService;

    @Autowired
    private TronService tronService;

    @PostMapping("/")
    public ResponseEntity<DealsImportResult> getBybitData(@RequestBody Credentials credentials) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {
        String apiKey = credentials.getAccessKey();
        String apiSecret = credentials.getSecretKey();
        DealsImportResult response = bybitService.getBybitDealImportResult(apiKey, apiSecret);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tron")
    public ResponseEntity<DealsImportResult> getTronData(@RequestBody Credentials credentials) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {
        String address = credentials.getAddress();
        DealsImportResult response = tronService.getTronDetailImportResult(address);
        return ResponseEntity.ok(response);
    }

}
