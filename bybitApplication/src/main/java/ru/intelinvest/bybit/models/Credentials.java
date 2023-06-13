package ru.intelinvest.bybit.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Credentials {
    /** ключ доступа к стороннему API (может быть не задан) */
    private String accessKey;

    /** секретный ключ доступа к стороннему API (может быть не задан) */
    private String secretKey;

    /** адрес кошелька, идентификатор счета (который интегрируется) */
    private String address;

    /** пароль для доступа к стороннему API (может быть не задан) */
    private String password;

    /** дата начала выборки транзакций (может быть не задана) */
    private String startDate;

    /** уникальный идентификатор запроса (необходим для служебных действий, например, для сохранения оригинального ответа от стороннего API) */
    private String requestId;
}
