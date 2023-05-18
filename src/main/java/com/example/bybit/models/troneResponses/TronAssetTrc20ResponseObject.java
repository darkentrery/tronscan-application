package com.example.bybit.models.troneResponses;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TronAssetTrc20ResponseObject {
    private String account_name;
    private Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    public void allSetter(String fieldName, Object fieldValue) {
        properties.put(fieldName, fieldValue);
    }
}
