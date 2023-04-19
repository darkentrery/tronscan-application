package com.example.bybit.models;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public ImportTradeDataHolder(JSONObject transaction) throws JSONException {
        this.price = !transaction.getString("tradePrice").equals("") ? new BigDecimal(transaction.getString("tradePrice")) : null;
//        this.operation = Operation.AMORTIZATION;
        this.date = new Date(Long.parseLong(transaction.getString("transactionTime")));
        this.quantity = !transaction.getString("qty").equals("") ? new BigDecimal(transaction.getString("qty")) : null;
        this.fee = !transaction.getString("fee").equals("") ? new BigDecimal(transaction.getString("fee")) : BigDecimal.ZERO;
        this.currency = transaction.getString("currency");
        this.tradeSystemId = transaction.getString("tradeId");
    }
}
