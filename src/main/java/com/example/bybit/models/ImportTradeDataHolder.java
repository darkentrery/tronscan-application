package com.example.bybit.models;

import org.springframework.boot.configurationprocessor.json.JSONException;

import java.math.BigDecimal;
import java.util.*;

public class ImportTradeDataHolder {
    /** цена из транзакции */
    private BigDecimal price;

    /** тип операции */
    private Operation operation;

    /** дата транзакции */
    private Date date;

    /** количество актива/бумаг */
    private BigDecimal quantity;

    /** комиссия */
    private BigDecimal fee = BigDecimal.ZERO;

    /** заметка (при наличии) */
    private String note;

    /** список алиасов актива/бумаги */
    private List<ShareAlias> shareAliases = new ArrayList<>();

    /** сумма транзакции */
    private BigDecimal sum;

    /** НКД (для транзакций по облигациям, может быть не задано) */
    private BigDecimal nkd;

    /** номинал (для транзакций по облигациям, может быть не задано) */
    private BigDecimal facevalue;

    /** код валюты актива/бумаги */
    private String currency;

    /** код валюту комиссии */
    private String feeCurrency;

    /** сумма транзакции (заполняется только для транзакций с денежными средствами) */
    private BigDecimal moneyAmount;

    /** данные по связанной сделке */
    private ImportTradeDataHolder linked;

    /** уникальный идентификатор транзакции (при наличии) */
    protected String tradeSystemId;

    /** признак указания НКД или дивиденда на 1 бумагу */
    private boolean perOne = true;

    /** предпочтительный тип сделки, может быть не указан */
    private TradePreferredType tradePreferredType;

    public ImportTradeDataHolder() {

    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public void setOperation(String operation) {
        try {
            this.operation = Operation.valueOf(operation);
        } catch (IllegalArgumentException e) {
            String value = this.operationMapping().get(operation);
            if (!value.equals("")) {
                this.operation = Operation.valueOf(value);
            }
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<ShareAlias> getShareAliases() {
        return shareAliases;
    }

    public void setShareAliases(List<ShareAlias> shareAliases) {
        this.shareAliases = shareAliases;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public BigDecimal getNkd() {
        return nkd;
    }

    public void setNkd(BigDecimal nkd) {
        this.nkd = nkd;
    }

    public BigDecimal getFacevalue() {
        return facevalue;
    }

    public void setFacevalue(BigDecimal facevalue) {
        this.facevalue = facevalue;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFeeCurrency() {
        return feeCurrency;
    }

    public void setFeeCurrency(String feeCurrency) {
        this.feeCurrency = feeCurrency;
    }

    public BigDecimal getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(BigDecimal moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public ImportTradeDataHolder getLinked() {
        return linked;
    }

    public void setLinked(ImportTradeDataHolder linked) {
        this.linked = linked;
    }

    public String getTradeSystemId() {
        return tradeSystemId;
    }

    public void setTradeSystemId(String tradeSystemId) {
        this.tradeSystemId = tradeSystemId;
    }

    public boolean isPerOne() {
        return perOne;
    }

    public void setPerOne(boolean perOne) {
        this.perOne = perOne;
    }

    public TradePreferredType getTradePreferredType() {
        return tradePreferredType;
    }

    public void setTradePreferredType(TradePreferredType tradePreferredType) {
        this.tradePreferredType = tradePreferredType;
    }

    private Map<String, String> operationMapping() {
        HashMap<String, String> map = new HashMap<>();
        map.put("TRANSFER_IN", "SHARE_IN");
        map.put("TRANSFER_OUT", "SHARE_OUT");
        map.put("TRADE", "BUY");
        map.put("-TRADE", "SELL");
        map.put("CURRENCY_BUY", "BUY");
        map.put("CURRENCY_SELL", "SELL");
        map.put("SETTLEMENT", "");
        map.put("DELIVERY", "");
        map.put("LIQUIDATION", "");
        map.put("BONUS", "");
        map.put("FEE_REFUND", "");
        map.put("INTEREST", "");
        return map;
    }

    public ImportTradeDataHolder(V5TradeObject object) throws JSONException {
        this.price = !object.getString("tradePrice").equals("") ? new BigDecimal(object.getString("tradePrice")) : null;
        String operation = object.getString("type");
        if (operation.equals("TRADE") && object.getString("side").equals("Sell")) {
            operation = "-TRADE";
        }
        this.setOperation(operation);
        this.date = new Date(Long.parseLong(object.getString("transactionTime")));
        this.quantity = !object.getString("qty").equals("") ? new BigDecimal(object.getString("qty")) : null;
        this.fee = !object.getString("fee").equals("") ? new BigDecimal(object.getString("fee")) : BigDecimal.ZERO;
        this.currency = object.getString("currency");
        this.tradeSystemId = object.getString("orderId");
    }

    public ImportTradeDataHolder(V1TradeObject object) throws JSONException {
        this.price = !object.getString("price").equals("") ? new BigDecimal(object.getString("price")) : null;
        this.date = new Date(Long.parseLong(object.getString("time")));
        this.quantity = !object.getString("qty").equals("") ? new BigDecimal(object.getString("qty")) : null;
        this.fee = !object.getJSONObject("fee").getString("fee").equals("") ? new BigDecimal(object.getJSONObject("fee").getString("fee")) : BigDecimal.ZERO;
        this.currency = object.getString("symbol");
        this.tradeSystemId = object.getString("orderId");
        this.feeCurrency = object.getJSONObject("fee").getString("feeTokenName");
    }

    public ImportTradeDataHolder(V1OrderObject object) throws JSONException {
        this.price = !object.getString("price").equals("") ? new BigDecimal(object.getString("price")) : null;
        this.date = new Date(Long.parseLong(object.getString("time")));
        this.quantity = !object.getString("executedQty").equals("") ? new BigDecimal(object.getString("executedQty")) : null;
        this.currency = object.getString("symbol");
        this.tradeSystemId = object.getString("orderId");
        String operation = object.getString("side");
        this.setOperation(operation);
    }

    public ImportTradeDataHolder(TronTransactionObject object) throws JSONException {
        this.date = new Date(object.getInt("block_timestamp"));
        try {
            this.quantity = new BigDecimal(object.getJSONObject("raw_data")
                    .getJSONArray("contract")
                    .getJSONObject(0)
                    .getJSONObject("parameter")
                    .getJSONObject("value")
                    .getInt("amount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.currency = "TRX";
        try {
            this.tradeSystemId = object.getString("txID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.fee = new BigDecimal(object.getInt("net_usage") + object.getInt("energy_usage"));

    }

    public ImportTradeDataHolder(TronTransactionTrc20Object object) throws JSONException {
        this.date = new Date(object.getInt("block_timestamp"));
        this.quantity = new BigDecimal(object.getString("value"));
        this.currency = object.getJSONObject("token_info").getString("symbol");
        this.tradeSystemId = object.getString("transaction_id");
    }
}
