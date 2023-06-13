package ru.intelinvest.bybit.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class DealImportError {
    /** текст ошибки */
    private String message;

    /** дата транзакции с которой произошла ошибка (при наличии) */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dealDate;

    /** уникальный идентификатор бумаги/актива в транзакции с которой произошла ошибка (при наличии) */
    private String dealTicker;

    /** код валюты актива/бумаги (при наличии) */
    private String currency;
}
