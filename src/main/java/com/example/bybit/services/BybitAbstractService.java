package com.example.bybit.services;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public class BybitAbstractService {
    @Autowired
    public ConvertService convertService;
    public String RECV_WINDOW;
    public String URL;
    public String API_KEY;
    public String API_SECRET;
    public String minTimestamp;

    public String getRECV_WINDOW() {
        return RECV_WINDOW;
    }

    public void setRECV_WINDOW(String RECV_WINDOW) {
        this.RECV_WINDOW = RECV_WINDOW;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getAPI_KEY() {
        return API_KEY;
    }

    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public String getAPI_SECRET() {
        return API_SECRET;
    }

    public void setAPI_SECRET(String API_SECRET) {
        this.API_SECRET = API_SECRET;
    }

    public String getMinTimestamp() {
        return minTimestamp;
    }

    public void setMinTimestamp(String minTimestamp) {
        this.minTimestamp = minTimestamp;
    }

    public void setParameters(Map<String, String> parameters) {
        this.API_KEY = parameters.get("accessKey");
        this.API_SECRET = parameters.get("secretKey");
        this.RECV_WINDOW = parameters.get("recvWindow");
        this.URL = parameters.get("url");
        this.minTimestamp = parameters.get("minTimestamp");
    }

    public ZonedDateTime getMinTimestampZoneDate() {
        Instant i = Instant.ofEpochMilli(Long.parseLong(this.minTimestamp));
        ZonedDateTime z = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
        ZonedDateTime now = ZonedDateTime.now();
        if (z.toInstant().toEpochMilli() < now.minusMonths(12).toInstant().toEpochMilli()) {
            z = now.minusMonths(12);
        }
        return z;
    }
}
