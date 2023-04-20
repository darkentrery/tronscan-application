package com.example.bybit.models;

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

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
