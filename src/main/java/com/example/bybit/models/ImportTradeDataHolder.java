package com.example.bybit.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Setter
@Getter
@NoArgsConstructor
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

    public ImportTradeDataHolder(TronTransactionObject object, String address) throws JSONException {
        this.date = new Date(object.getLong("block_timestamp"));
        try {
            Integer quantity = object
                    .getJSONObject("raw_data")
                    .getJSONArray("contract")
                    .getJSONObject(0)
                    .getJSONObject("parameter")
                    .getJSONObject("value")
                    .getInt("amount");
            this.quantity = new BigDecimal(quantity / 1000000);
        } catch (JSONException e) {
            log.error(String.valueOf(object.getJSONObject("raw_data")
                    .getJSONArray("contract")
                    .getJSONObject(0)
                    .getJSONObject("parameter")
            ));
        }
        this.currency = "TRX";
        try {
            this.tradeSystemId = object.getString("txID");
        } catch (JSONException e) {
            log.error(e.getMessage());
        }
        Integer fee_sum = object.getInt("net_usage") + object.getInt("energy_usage") + object.getInt("energy_usage_total");
        Integer energy_fee = object.getInt("energy_fee");
        if (energy_fee != 0) {
            fee_sum += energy_fee / 1000000;
        }
        this.fee = new BigDecimal(fee_sum);
        try {
            var contractData = object.getJSONObject("raw_data")
                    .getJSONArray("contract")
                    .getJSONObject(0)
                    .getJSONObject("parameter")
                    .getJSONObject("value");
            if (contractData.getString("owner_address").equals(address)) {
                this.operation = Operation.SHARE_OUT;
            } else if (contractData.has("contract_address") && contractData.getString("contract_address").equals(address)) {
                this.operation = Operation.SHARE_IN;
            } else if (contractData.has("to_address") && contractData.getString("to_address").equals(address)) {
                this.operation = Operation.SHARE_IN;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ImportTradeDataHolder(TronTransaction6SizeObject object) throws JSONException {
        this.date = new Date(object.getLong("block_timestamp"));
        try {
            Integer quantity = object
                    .getJSONObject("data")
                    .getJSONObject("call_value")
                    .getInt("_");
            this.quantity = new BigDecimal(quantity / 1000000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.currency = "TRX";
        try {
            this.tradeSystemId = object.getString("tx_id");
        } catch (JSONException e) {
            log.error(e.getMessage());
        }
    }

    public ImportTradeDataHolder(ImportTradeDataHolder object1, ImportTradeDataHolder object2) {
        this.date = object1.date != null ? object1.date : object2.date;
        this.quantity = object1.quantity != null ? object1.quantity : object2.quantity;
        if (object1.quantity != null && object2.quantity != null) {
            this.quantity = object1.quantity.max(object2.quantity);
        }
        this.currency = object1.currency != null ? object1.currency : object2.currency;
        this.tradeSystemId = object1.tradeSystemId != null ? object1.tradeSystemId : object2.tradeSystemId;
        this.fee = object1.fee != null ? object1.fee : object2.fee;
        if (object1.fee != null && object2.fee != null) {
            this.fee = object1.fee.max(object2.fee);
        }
        this.operation = object1.operation != null ? object1.operation : object2.operation;
    }

    public ImportTradeDataHolder(TronTransactionTrc20Object object, String address) throws JSONException {
        this.date = new Date(object.getLong("block_timestamp"));
        this.quantity = new BigDecimal(object.getString("value"));
        this.quantity = this.quantity.divide(new BigDecimal(1000000));
        this.currency = object.getJSONObject("token_info").getString("symbol");
        this.tradeSystemId = object.getString("transaction_id");
        if (object.getString("from").equals(address)) {
            this.operation = Operation.SHARE_OUT;
        } else if (object.getString("to").equals(address)) {
            this.operation = Operation.SHARE_IN;
        }
    }
}
