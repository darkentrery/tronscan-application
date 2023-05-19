package com.example.tron.models.troneResponses;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TronTransactionRawData {
    private ArrayList<RawDataContract> contract = new ArrayList<>();
    private Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    public void allSetter(String fieldName, Object fieldValue) {
        properties.put(fieldName, fieldValue);
    }

    public BigDecimal getQuantity() {
        BigDecimal quantity = null;
        for (RawDataContract contract1 : this.contract) {
            if (contract1.getQuantity() != null) {
                quantity = contract1.getQuantity();
                break;
            }
        }
        return quantity;
    }
}
