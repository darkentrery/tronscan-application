package ru.intelinvest.bybit.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Map<String, String> operationMapping() {
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
}
