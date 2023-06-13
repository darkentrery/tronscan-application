package ru.intelinvest.tronscan.services;

import ru.intelinvest.tronscan.models.DealsImportResult;
import org.json.JSONException;

import java.io.IOException;


public interface TronService {
    DealsImportResult getTronDetailImportResult(String address, String startDate) throws JSONException, InterruptedException, IOException;
}
