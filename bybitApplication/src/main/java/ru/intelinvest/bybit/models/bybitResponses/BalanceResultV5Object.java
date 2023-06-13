package ru.intelinvest.bybit.models.bybitResponses;

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
public class BalanceResultV5Object {
    private ArrayList<BalanceInfoV5Object> list = new ArrayList<>();
    private Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    public void allSetter(String fieldName, Object fieldValue) {
        properties.put(fieldName, fieldValue);
    }
}
