package com.example.bybit.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DealsImportResult {
    /** Список ошибок при обработке транзакций */
    private List<DealImportError> errors = new ArrayList<>();

    /** Список обработанных транзакций */
    private List<ImportTradeDataHolder> transactions = new ArrayList<>();

    /** Список данных по активам/бумагам (необходимые для создания сущности в системе) */
    private List<AssetModel> assetMetaData = new ArrayList<>();

    /** Список балансов по заданному аккаунту */
    private Map<String, BigDecimal> currentMoneyRemainders = new HashMap<>();

    /** Глобальная ошибка импорта */
    private String generalError;

    /** Признак валидности ответа (используется когда нет сделок, и ответ из API пустой) */
    private boolean reportValid;

    /** Дополнительные инструкции для обработки транзаций */
    private ParseInstruction parseInstruction;

    public DealsImportResult() {

    }

    public Map<String, BigDecimal> getCurrentMoneyRemainders() {
        return currentMoneyRemainders;
    }

    public void setCurrentMoneyRemainders(Map<String, BigDecimal> currentMoneyRemainders) {
        this.currentMoneyRemainders = currentMoneyRemainders;
    }

    public void setCurrentMoneyRemainders(JSONObject object) throws JSONException {
        if (object != null) {
            JSONArray balances = object.getJSONObject("result").getJSONArray("list");
            for (int j = 0; j < balances.length(); j++) {
                JSONArray coins = balances.getJSONObject(j).getJSONArray("coin");
                for (int i = 0; i < coins.length(); i++) {
                    JSONObject coin = coins.getJSONObject(i);
                    String key = coin.getString("coin");
                    BigDecimal value = new BigDecimal(coin.getString("walletBalance"));
                    this.currentMoneyRemainders.put(key, value);
                }
            }
        }
    }

    public void setCurrentMoneyRemainders(V1BalanceObject object) throws JSONException {
        if (object != null) {
            try {
                JSONArray balances = object.getJSONObject("result").getJSONArray("balances");
                for (int i = 0; i < balances.length(); i++) {
                    JSONObject coin = balances.getJSONObject(i);
                    String key = coin.getString("coin");
                    BigDecimal value = new BigDecimal(coin.getString("total"));
                    this.currentMoneyRemainders.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getGeneralError() {
        return generalError;
    }

    public void setGeneralError(String generalError) {
        this.generalError = generalError;
    }

    public boolean isReportValid() {
        return reportValid;
    }

    public void setReportValid(boolean reportValid) {
        this.reportValid = reportValid;
    }

    public List<DealImportError> getErrors() {
        return errors;
    }

    public void setErrors(List<DealImportError> errors) {
        this.errors = errors;
    }

    public List<ImportTradeDataHolder> getTransactions() {
        return transactions;
    }

    public List<AssetModel> getAssetMetaData() {
        return assetMetaData;
    }

    public void setAssetMetaData(List<AssetModel> assetMetaData) {
        this.assetMetaData = assetMetaData;
    }

    public void setTransactions(List<ImportTradeDataHolder> transactions) {
        this.transactions = transactions;
    }

    public void extendTransactions(List<ImportTradeDataHolder> transactions) {
        for (ImportTradeDataHolder transaction : transactions) {
            this.transactions.add(transaction);
        }
    }

    public ParseInstruction getParseInstruction() {
        return parseInstruction;
    }

    public void setParseInstruction(ParseInstruction parseInstruction) {
        this.parseInstruction = parseInstruction;
    }

    public DealsImportResult(Map<String, BigDecimal> currentMoneyRemainders, String generalError, boolean reportValid) {
        this.currentMoneyRemainders = currentMoneyRemainders;
        this.generalError = generalError;
        this.reportValid = reportValid;
    }
}
