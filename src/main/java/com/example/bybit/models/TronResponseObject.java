package com.example.bybit.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TronResponseObject {
    private ArrayList<TroneResponseTransactionObject> data;
    private Boolean success;
//    private String meta;
//    private Map<String, String> user = new HashMap<>();
    private Map<String, Object> unrecognizedFields = new HashMap<>();

    @JsonAnySetter
    public void allSetter(String fieldName, Object fieldValue) {
        unrecognizedFields.put(fieldName, fieldValue);
    }


}

