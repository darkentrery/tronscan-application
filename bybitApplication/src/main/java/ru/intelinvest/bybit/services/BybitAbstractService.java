package ru.intelinvest.bybit.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@Setter
@Getter
public class BybitAbstractService {
    @Autowired
    public ConvertService convertService;
    public String RECV_WINDOW;
    public String URL;
    public String API_KEY;
    public String API_SECRET;
    public String minTimestamp;

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

    protected Object getResponseObject(String response, Class objectClass) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response, objectClass);
    }
}
