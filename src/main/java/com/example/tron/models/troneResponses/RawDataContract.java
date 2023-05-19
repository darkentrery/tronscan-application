package com.example.tron.models.troneResponses;

import com.example.tron.models.Operation;
import com.fasterxml.jackson.annotation.JsonAnySetter;
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

    private Map<String, Object> parameter;
    private String type;
    private Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    public void allSetter(String fieldName, Object fieldValue) {
        properties.put(fieldName, fieldValue);
    }

    public BigDecimal getQuantity() {
        BigDecimal quantity = null;
        try {
            LinkedHashMap value = (LinkedHashMap) parameter.get("value");
            if (value.containsKey("amount")) {
                if (value.get("amount") instanceof Integer) {
                    Integer amount = (Integer) value.get("amount");
                    quantity = new BigDecimal(amount / 1000000);
                } else if (value.get("amount") instanceof Long) {
                    Long amount = (Long) value.get("amount");
                    quantity = new BigDecimal(amount / 1000000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return quantity;
    }

    public Operation getOperation(String address) {
        Operation operation = null;
        try {
            LinkedHashMap<String, Object> contractData = (LinkedHashMap<String, Object>) this.parameter.get("value");
            if (contractData.get("owner_address").equals(address)) {
                operation = Operation.SHARE_OUT;
            } else if (contractData.containsKey("contract_address") && contractData.get("contract_address").equals(address)) {
                operation = Operation.SHARE_IN;
            } else if (contractData.containsKey("to_address") && contractData.get("to_address").equals(address)) {
                operation = Operation.SHARE_IN;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return operation;
    }
}
