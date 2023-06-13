package ru.intelinvest.bybit.models.bybitResponses;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceRecordV1Object {
    private BigDecimal total;
    private String coin;
    private Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    public void allSetter(String fieldName, Object fieldValue) {
        properties.put(fieldName, fieldValue);
    }

    public void setTotal(String total) {
        if (total != null) {
            this.total = new BigDecimal(total);
        }
    }
}
