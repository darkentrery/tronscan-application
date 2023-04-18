package com.example.bybit.models;

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
}
