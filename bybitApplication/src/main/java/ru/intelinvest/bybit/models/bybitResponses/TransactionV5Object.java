package ru.intelinvest.bybit.models.bybitResponses;

import ru.intelinvest.bybit.models.ImportTradeDataHolder;
import ru.intelinvest.bybit.models.Operation;
import ru.intelinvest.bybit.models.ShareAlias;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionV5Object {
    @JsonSetter("tradePrice")
    private BigDecimal price;

    @JsonSetter("type")
    private Operation operation;

    @JsonSetter("transactionTime")
    private Date date;

    @JsonSetter("qty")
    private BigDecimal quantity;

    private BigDecimal fee = BigDecimal.ZERO;

    private String currency;

    private String symbol;

    private List<ShareAlias> shareAliases = new ArrayList<>();

    @JsonSetter("orderId")
    private String tradeSystemId;
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

    public void setOperation(String operation) {
        if (operation.equals("TRADE") && properties.get("side").equals("Sell")) {
            operation = "-TRADE";
        }
        ImportTradeDataHolder dataHolder = new ImportTradeDataHolder();
        String value = dataHolder.operationMapping().get(operation);
        if (!value.equals("")) {
            this.operation = Operation.valueOf(value);
        }
    }

    public void setQuantity(String quantity) {
        if (quantity != null && !quantity.equals("")) {
            this.quantity = new BigDecimal(quantity);
        }
    }

    public void setFee(String fee) {
        if (fee != null && !fee.equals("")) {
            this.fee = new BigDecimal(fee);
        }
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        if (this.symbol != null && !this.symbol.equals("") && currency != null && !currency.equals("")) {
            String token = this.symbol.replaceAll(currency, "");
            ShareAlias shareAlias = new ShareAlias();
            shareAlias.setAlias(token);
            this.shareAliases.add(shareAlias);
        }
    }

    public ImportTradeDataHolder toImportTradeDataHolder() {
        ImportTradeDataHolder object = new ImportTradeDataHolder();
        object.setDate(this.getDate());
        object.setQuantity(this.getQuantity());
        object.setCurrency(this.getCurrency());
        object.setFee(this.getFee());
        object.setTradeSystemId(this.getTradeSystemId());
        object.setPrice(this.getPrice());
        object.setShareAliases(this.getShareAliases());
        object.setOperation(this.getOperation());
        return object;
    }
}
