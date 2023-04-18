package com.example.bybit.models;

public class ShareAlias {
    /** уникальный идентификатор бумаги (Тикер) (при наличии) */
    private String ticker;

    /** уникальный код бумаги (ISIN) (при наличии) */
    private String isin;

    /** алиас бумаги (при наличии) */
    private String alias;

    /** предпочитаемый тип бумаги (может быть не указан) */
    private ShareType preferredType;

    /** биржа бумаги (код, название) (при налиичии) */
    private String exchange;
}
