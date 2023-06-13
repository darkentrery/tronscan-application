package ru.intelinvest.bybit.models;

import com.fasterxml.jackson.annotation.JsonRawValue;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

public class AssetModel {
    /** Тип актива */
    private AssetCategory category;

    /** Код актива (может быть ticker/isin или еще какой-то однозначно идентифицирующий актив во внешней системе) */
    private String ticker;

    /** Валюта актива */
    private String currency;

    /** Название актива */
    private String name;

    /** Цена актива (текущая) */
    @Nullable
    private BigDecimal price;

    /** Заметка */
    @Nullable
    private String note;

    /** Количество значащих разрядов в цене */
    private int decimals = 9;

    /** Признак закрепления актива за пользователем, а не за системой */
    private boolean userDefined;

    /** Метаданные по активу */
    @JsonRawValue
    private String metaData;
}
