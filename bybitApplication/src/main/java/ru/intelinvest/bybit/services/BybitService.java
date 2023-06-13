package ru.intelinvest.bybit.services;

import ru.intelinvest.bybit.models.DealsImportResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface BybitService {

    DealsImportResult getBybitDealImportResult(String API_KEY, String API_SECRET, String startDate) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException, IOException;
}
