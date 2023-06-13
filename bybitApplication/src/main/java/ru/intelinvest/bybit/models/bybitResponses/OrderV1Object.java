package ru.intelinvest.bybit.models.bybitResponses;

import ru.intelinvest.bybit.models.ImportTradeDataHolder;
import ru.intelinvest.bybit.models.Operation;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderV1Object {
    private BigDecimal price;

    @JsonSetter("time")
    private Date date;

    @JsonSetter("executedQty")
    private BigDecimal quantity;

    @JsonSetter("symbol")
    private String currency;

    @JsonSetter("orderId")
    private String tradeSystemId;

    @JsonSetter("side")
    private Operation operation;

    private String status;

    private String type;

    private Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    public void allSetter(String fieldName, Object fieldValue) {
        properties.put(fieldName, fieldValue);
    }

    public void setPrice(String price) {
        if (price != null && !price.equals("")) {
            this.price = new BigDecimal(price);
        }
    }

    public void setQuantity(String quantity) {
        if (quantity != null && !quantity.equals("")) {
            this.quantity = new BigDecimal(quantity);
        }
    }

    public void setOperation(String side) {
        try {
            this.operation = Operation.valueOf(side);
        } catch (IllegalArgumentException e) {
            ImportTradeDataHolder dataHolder = new ImportTradeDataHolder();
            String value = dataHolder.operationMapping().get(operation);
            if (!value.equals("")) {
                this.operation = Operation.valueOf(value);
            }
        }
    }

    public ImportTradeDataHolder toImportTradeDataHolder() {
        ImportTradeDataHolder object = new ImportTradeDataHolder();
        object.setDate(this.getDate());
        object.setQuantity(this.getQuantity());
        object.setCurrency(this.getCurrency());
        object.setOperation(this.getOperation());
        object.setTradeSystemId(this.getTradeSystemId());
        object.setPrice(this.getPrice());
        return object;
    }
}
