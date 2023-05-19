package com.example.tron.models.troneResponses;

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
public class TronResponseAccountObject {
    private ArrayList<TronAccountInfoObject> data;
    private Boolean success;
    private Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    public void allSetter(String fieldName, Object fieldValue) {
        properties.put(fieldName, fieldValue);
    }

    public ArrayList<AssetV2Object> getAssets() {
        return this.data.get(0).getAssetV2();
    }

    public ArrayList<AssetTrc20Object> getAssetsTrc20() {
        return this.data.get(0).getTrc20();
    }
}
