package com.example.bybit.models.troneResponses;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssetTrc20Object {
    private Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    public void allSetter(String fieldName, Object fieldValue) {
        properties.put(fieldName, fieldValue);
    }

    public String getTokenName() {
        String name = "";
        for (String key : properties.keySet()) {
            name = key;
            break;
        }
        return name;
    }

    public String getValue() {
        return (String) properties.get(this.getTokenName());
    }
}
