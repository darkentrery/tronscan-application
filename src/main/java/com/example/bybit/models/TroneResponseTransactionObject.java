package com.example.bybit.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TroneResponseTransactionObject {
    private String txID;
    private String tx_id;

    @JsonSetter("block_timestamp")
    private Date date;

    private Map<String, Object> properties = new HashMap<>();
    private Long net_usage;
    private Long energy_usage;
    private Long energy_total_usage;
    private TroneTransactionRawData raw_data;

    @JsonAnySetter
    public void allSetter(String fieldName, Object fieldValue) {
        properties.put(fieldName, fieldValue);
    }

    public void setDate(Long date) {
        this.date = new Date(date);
    }
}
