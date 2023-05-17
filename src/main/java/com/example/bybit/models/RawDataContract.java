package com.example.bybit.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RawDataContract {
    @JsonSetter("parameter")
    private BigDecimal quantity;
    private Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    public void allSetter(String fieldName, Object fieldValue) {
        properties.put(fieldName, fieldValue);
    }

    public void setQuantity(LinkedHashMap parameter) {
        BigDecimal quantity = null;
        try {
            LinkedHashMap value = (LinkedHashMap) parameter.get("value");
            Integer amount = (Integer) value.get("amount");
            quantity = new BigDecimal(amount / 1000000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.quantity = quantity;
    }
}
