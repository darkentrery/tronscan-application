package ru.intelinvest.bybit.models;

import ru.intelinvest.bybit.models.bybitResponses.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class DealsImportResult {
    /** Список ошибок при обработке транзакций */
    private List<DealImportError> errors = new ArrayList<>();

    /** Список обработанных транзакций */
    private List<ImportTradeDataHolder> transactions = new ArrayList<>();

    /** Список данных по активам/бумагам (необходимые для создания сущности в системе) */
    private List<AssetModel> assetMetaData = new ArrayList<>();

    /** Список балансов по заданному аккаунту */
    private Map<String, BigDecimal> currentMoneyRemainders = new HashMap<>();

    /** Глобальная ошибка импорта */
    private String generalError;

    /** Признак валидности ответа (используется когда нет сделок, и ответ из API пустой) */
    private boolean reportValid;

    /** Дополнительные инструкции для обработки транзаций */
    private ParseInstruction parseInstruction;

    public void setCurrentMoneyRemainders(Map<String, BigDecimal> currentMoneyRemainders) {
        this.currentMoneyRemainders = currentMoneyRemainders;
    }

    public void setCurrentMoneyRemainders(BalanceV1Object object) {
        for (BalanceRecordV1Object record : object.getResult().getBalances()) {
            this.currentMoneyRemainders.put(record.getCoin(), record.getTotal());
        }
    }

    public void setCurrentMoneyRemainders(BalanceV5Object object) {
        for (BalanceInfoV5Object balanceInfoV5Object : object.getResult().getList()) {
            for (BalanceRecordV5Object balanceRecordV5Object : balanceInfoV5Object.getCoin()) {
                this.currentMoneyRemainders.put(balanceRecordV5Object.getCoin(), balanceRecordV5Object.getWalletBalance());
            }
        }
    }

    public void extendTransactions(List<ImportTradeDataHolder> transactions) {
        for (ImportTradeDataHolder transaction : transactions) {
            this.transactions.add(transaction);
        }
    }

    public DealsImportResult(Map<String, BigDecimal> currentMoneyRemainders, String generalError, boolean reportValid) {
        this.currentMoneyRemainders = currentMoneyRemainders;
        this.generalError = generalError;
        this.reportValid = reportValid;
    }
}
