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
public class TradeV1Object {
    private BigDecimal price;

    @JsonSetter("time")
    private Date date;

    @JsonSetter("qty")
    private BigDecimal quantity;

    @JsonSetter("feeAmount")
    private BigDecimal fee;

    @JsonSetter("feeTokenId")
    private String feeCurrency;

    private String currency;

    @JsonSetter("orderId")
    private String tradeSystemId;

    private List<ShareAlias> shareAliases = new ArrayList<>();

    private Operation operation;

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

    public void setFee(String fee) {
        if (fee != null && !fee.equals("")) {
            this.fee = new BigDecimal(fee);
        }
    }

    public void setFeeCurrency(String feeTokenId) {
        this.feeCurrency = feeTokenId;
        String symbol = (String) this.properties.get("symbol");
        if (symbol != null && feeTokenId != null) {
            String currency = symbol.replaceAll(feeTokenId, "");
            this.currency = currency;
            ShareAlias shareAlias = new ShareAlias();
            shareAlias.setAlias(feeTokenId);
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
        object.setFeeCurrency(this.getFeeCurrency());
        object.setShareAliases(this.getShareAliases());
        return object;
    }
}
