package ru.intelinvest.bybit.services;

import ru.intelinvest.bybit.models.ImportTradeDataHolder;
import ru.intelinvest.bybit.models.bybitResponses.BalanceV5Object;
import ru.intelinvest.bybit.models.bybitResponses.TransactionV5Object;
import ru.intelinvest.bybit.models.bybitResponses.TransactionsV5Object;
import com.google.common.util.concurrent.RateLimiter;
import okhttp3.Call;
import okhttp3.Response;
import org.json.JSONException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BybitV5ServiceImpl extends BybitAbstractService implements BybitV5Service{
    @Override
    public List<ImportTradeDataHolder> getTransactions(String API_KEY, String API_SECRET) throws JSONException {
        List<ImportTradeDataHolder> transactions = new ArrayList<>();
        List<TransactionsV5Object> responses = this.getAllResponses();
        for (TransactionsV5Object transactionsV5Object : responses) {
            for (TransactionV5Object transactionV5Object : transactionsV5Object.getResult().getList()) {
                transactions.add(transactionV5Object.toImportTradeDataHolder());
            }
        }
        return transactions;
    }

    @Override
    public BalanceV5Object getBalanceObject() {
        List<String> params = new ArrayList<>();
        params.add("accountType=UNIFIED");
        try {
            String responseString = this.getResponse("/v5/account/wallet-balance", params);
            return (BalanceV5Object) this.getResponseObject(responseString, BalanceV5Object.class);
        } catch (Exception ex) {
            return new BalanceV5Object();
        }

    }

    private List<TransactionsV5Object> getAllResponses() throws JSONException {
        String cursor = "";
        List<TransactionsV5Object> responses = new ArrayList<>();
        TransactionsV5Object transactionsV5Object = this.getTransactionLog(cursor);
        if (transactionsV5Object.getRetCode() == 0) {
            responses.add(transactionsV5Object);
            RateLimiter rateLimiter = RateLimiter.create(2);
            while (transactionsV5Object.getResult().getNextPageCursor() != null && !transactionsV5Object.getResult().getNextPageCursor().equals("")) {
                cursor = transactionsV5Object.getResult().getNextPageCursor();
                rateLimiter.acquire();
                transactionsV5Object = this.getTransactionLog(cursor);
                responses.add(transactionsV5Object);
            }
        }
        return responses;
    }

    private TransactionsV5Object getTransactionLog(String cursor) throws JSONException {
        List<String> params = new ArrayList<>();
        params.add("accountType=UNIFIED");
        params.add("limit=50");
        params.add(String.format("startTime=%s", this.minTimestamp));

        if (!cursor.equals("")) {
            params.add(String.format("cursor=%s", cursor));
        }
//        String responseString = this.getResponse("/v5/account/transaction-log", params);
//        TransactionsV5Object transactionsV5Object = (TransactionsV5Object) this.getResponseObject(responseString, TransactionsV5Object.class);
        try {
            String responseStringV5 = this.getV5Response("/v5/account/transaction-log", params);
            TransactionsV5Object transactionsV5Object = (TransactionsV5Object) this.getResponseObject(responseStringV5, TransactionsV5Object.class);

            if (transactionsV5Object.getRetCode() == 10016) {
                RateLimiter rateLimiter = RateLimiter.create(0.3);
                rateLimiter.acquire();
                return this.getTransactionLog(cursor);
            }
            return transactionsV5Object;
        } catch (Exception ex) {
            return new TransactionsV5Object();
        }

    }

    private String getV5Response(String endpoint, List<String> params) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String TIMESTAMP = Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());
        String queryString = String.join("&", params);
        String signature = convertService.genV5Sign(queryString, TIMESTAMP, this.API_KEY, this.API_SECRET, this.RECV_WINDOW);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String url = String.format("%s%s?%s", this.URL, endpoint, queryString);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("X-BAPI-API-KEY", API_KEY)
                .addHeader("X-BAPI-SIGN", signature)
                .addHeader("X-BAPI-TIMESTAMP", TIMESTAMP)
                .addHeader("X-BAPI-RECV-WINDOW", this.RECV_WINDOW)
                .addHeader("Content-Type", "application/json")
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    private String getResponse(String endpoint, List<String> params) throws NoSuchAlgorithmException, InvalidKeyException {
        String TIMESTAMP = Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());
        String queryString = String.join("&", params);
        String signature = convertService.genV5Sign(queryString, TIMESTAMP, this.API_KEY, this.API_SECRET, this.RECV_WINDOW);
        String url = String.format("%s%s?%s", this.URL, endpoint, queryString);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-BAPI-API-KEY", API_KEY);
        headers.set("X-BAPI-SIGN", signature);
        headers.set("X-BAPI-TIMESTAMP", TIMESTAMP);
        headers.set("X-BAPI-RECV-WINDOW", this.RECV_WINDOW);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return responseEntity.getBody();
    }
}
