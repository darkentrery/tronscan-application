package ru.intelinvest.bybit.services;

import ru.intelinvest.bybit.models.*;
import ru.intelinvest.bybit.models.bybitResponses.BalanceV1Object;
import ru.intelinvest.bybit.models.bybitResponses.BalanceV5Object;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Service
public class BybitServiceImpl implements BybitService{

    @Autowired
    private BybitV1Service bybitV1Service;

    @Autowired
    private BybitV5Service bybitV5Service;

    private final String RECV_WINDOW = "5000";

    @Value("${url.bybit}")
    String URL;

    private String API_KEY;
    private String API_SECRET;
    private String minTimestamp = "0";

    public void setMinTimestamp(String minTimestamp) {
        try {
            ZonedDateTime date = LocalDate.parse(minTimestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneId.of("UTC"));
            long epochMilli = date.toInstant().toEpochMilli();
            this.minTimestamp = Long.toString(epochMilli);
        } catch (Exception e) {
            this.minTimestamp = "0";
        }
    }

    public Map<String, String> getParameters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("accessKey", this.API_KEY);
        parameters.put("secretKey", this.API_SECRET);
        parameters.put("minTimestamp", this.minTimestamp);
        parameters.put("recvWindow", this.RECV_WINDOW);
        parameters.put("url", this.URL);
        return parameters;
    }

    @Override
    public DealsImportResult getBybitDealImportResult(String API_KEY, String API_SECRET, String startDate) throws NoSuchAlgorithmException, InvalidKeyException, JSONException, InterruptedException, IOException {
        DealsImportResult result = new DealsImportResult();
        this.setMinTimestamp(startDate);
        if (API_KEY != null && API_SECRET != null) {
            this.setAPI_KEY(API_KEY);
            this.setAPI_SECRET(API_SECRET);
            bybitV1Service.setParameters(this.getParameters());
            bybitV5Service.setParameters(this.getParameters());

            BalanceV1Object balanceV1 = bybitV1Service.getBalanceObject();
            result.setCurrentMoneyRemainders(balanceV1);
            BalanceV5Object balanceV5Object = bybitV5Service.getBalanceObject();
            result.setCurrentMoneyRemainders(balanceV5Object);

            List<ImportTradeDataHolder> orders = bybitV1Service.getV1Orders();
            List<ImportTradeDataHolder> trades = bybitV1Service.getV1Trades();
            for (ImportTradeDataHolder trade : trades) {
                for (ImportTradeDataHolder order : orders) {
                    if (trade.getTradeSystemId().equals(order.getTradeSystemId())) {
                        trade.setOperation(order.getOperation());
                    }
                }
            }

            List<ImportTradeDataHolder> transactions = bybitV5Service.getTransactions(API_KEY, API_SECRET);
            result.extendTransactions(transactions);
            result.extendTransactions(trades);

        }
        return result;
    }
}
