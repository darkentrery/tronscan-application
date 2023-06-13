package ru.intelinvest.tronscan.controllers;

import ru.intelinvest.tronscan.models.Credentials;
import ru.intelinvest.tronscan.models.DealsImportResult;
import ru.intelinvest.tronscan.services.TronService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/input")
public class MainController {

    @Autowired
    private TronService tronService;

    @PostMapping("/credentials")
    public ResponseEntity getTronData(@RequestBody Credentials credentials) throws JSONException {
        String address = credentials.getAddress();
        String startDate = credentials.getStartDate();
        try {
            DealsImportResult response = tronService.getTronDetailImportResult(address, startDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.ok("");
        }
    }
}
