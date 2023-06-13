package ru.intelinvest.tronscan.models.troneResponses;

import ru.intelinvest.tronscan.models.ImportTradeDataHolder;
import ru.intelinvest.tronscan.models.Operation;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TronResponseTransactionObject {
    private String txID;
    private String tx_id;
    private String transaction_id;

    @JsonSetter("block_timestamp")
    private Date date;

    private Map<String, Object> call_value = new HashMap<>();

    @JsonSetter("value")
    private BigDecimal quantity;

    private Map<String, Object> properties = new HashMap<>();
    private Long net_usage = 0L;
    private Long energy_usage = 0L;
    private Long energy_usage_total = 0L;
    private Long energy_fee = 0L;
    private TronTransactionRawData raw_data = new TronTransactionRawData();

    @JsonSetter("token_info")
    private String currency = "TRX";
    private String from;
    private String to;

    @JsonAnySetter
    public void allSetter(String fieldName, Object fieldValue) {
        properties.put(fieldName, fieldValue);
    }

    public void setDate(Long date) {
        this.date = new Date(date);
    }

    public void setQuantity(String quantity) {
        if (quantity != null) {
            this.quantity = new BigDecimal(quantity);
            this.quantity = this.quantity.divide(new BigDecimal(1000000));
        }
    }

    public void setEnergy_fee(Integer energy_fee) {
        if (energy_fee != null) {
            this.energy_fee = energy_fee.longValue() / 1000000;
        }
    }

    public BigDecimal getQuantity() {
        BigDecimal quantity = this.quantity;
        if (quantity == null) {
            if (this.call_value.containsKey("_")) {
                quantity = new BigDecimal((Integer) this.call_value.get("_") / 1000000);
            }
        }
        if (quantity == null) {
            quantity = this.raw_data.getQuantity();
        }
        return quantity;
    }

    public void setCurrency(LinkedHashMap<String, Object> token_info) {
        if (token_info != null) {
            this.currency = (String) token_info.get("symbol");
        }
    }

    public String getTradeSystemId() {
        String tradeSystemId = this.txID;
        if (tradeSystemId == null) {
            tradeSystemId = this.tx_id;
        }
        if (tradeSystemId == null) {
            tradeSystemId = this.transaction_id;
        }
        return tradeSystemId;
    }

    public BigDecimal getFee() {
        return new BigDecimal(this.net_usage + this.energy_usage + this.energy_usage_total + this.energy_fee);
    }

    public Operation getOperation(String address) {
        Operation operation = null;
        if (address.equals(from)) {
            operation = Operation.SHARE_OUT;
        } else if (address.equals(to)) {
            operation = Operation.SHARE_IN;
        }
        if (operation == null && raw_data.getContract().size() != 0) {
            operation = raw_data.getContract().get(0).getOperation(address);
        }
        return operation;
    }

    public ImportTradeDataHolder toImportTradeDataHolder(String address) {
        ImportTradeDataHolder object = new ImportTradeDataHolder();
        object.setDate(this.getDate());
        object.setQuantity(this.getQuantity());
        object.setFee(this.getFee());
        object.setCurrency(this.getCurrency());
        object.setOperation(this.getOperation(address));
        object.setTradeSystemId(this.getTradeSystemId());
        return object;
    }
}
