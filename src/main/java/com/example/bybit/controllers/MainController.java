package com.example.bybit.controllers;

import com.example.bybit.models.Credentials;
import com.example.bybit.models.DealsImportResult;
import com.example.bybit.services.BybitService;
import com.example.bybit.services.TronService;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static Logger logger = LoggerFactory.getLogger(MainController.class);

    @PostMapping("/credentials")
    public ResponseEntity<DealsImportResult> getBybitData(@RequestBody Credentials credentials) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException {
        String apiKey = credentials.getAccessKey();
        String apiSecret = credentials.getSecretKey();
        String startDate = credentials.getStartDate();
        DealsImportResult response = bybitService.getBybitDealImportResult(apiKey, apiSecret, startDate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/credentials/tron")
    public ResponseEntity getTronData(@RequestBody Credentials credentials) throws JSONException {
        String address = credentials.getAddress();
        String startDate = credentials.getStartDate();
        try {
            DealsImportResult response = tronService.getTronDetailImportResult(address, startDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.ok("");
        }
    }
}
