package com.example.bybit.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DealsImportResult {
    /** Список ошибок при обработке транзакций */
    private final List<DealImportError> errors = new ArrayList<>();

    /** Список обработанных транзакций */
    private final List<ImportTradeDataHolder> transactions = new ArrayList<>();

    /** Список данных по активам/бумагам (необходимые для создания сущности в системе) */
    private final List<AssetModel> assetMetaData = new ArrayList<>();

    /** Список балансов по заданному аккаунту */
    private Map<String, BigDecimal> currentMoneyRemainders = new HashMap<>();

    /** Глобальная ошибка импорта */
    private String generalError;

    /** Признак валидности ответа (используется когда нет сделок, и ответ из API пустой) */
    private boolean reportValid;

    /** Дополнительные инструкции для обработки транзаций */
    private ParseInstruction parseInstruction;

    public DealsImportResult() {

    }

    public Map<String, BigDecimal> getCurrentMoneyRemainders() {
        return currentMoneyRemainders;
    }

    public void setCurrentMoneyRemainders(Map<String, BigDecimal> currentMoneyRemainders) {
        this.currentMoneyRemainders = currentMoneyRemainders;
    }

    public String getGeneralError() {
        return generalError;
    }

    public void setGeneralError(String generalError) {
        this.generalError = generalError;
    }

    public boolean isReportValid() {
        return reportValid;
    }

    public void setReportValid(boolean reportValid) {
        this.reportValid = reportValid;
    }

    public DealsImportResult(Map<String, BigDecimal> currentMoneyRemainders, String generalError, boolean reportValid) {
        this.currentMoneyRemainders = currentMoneyRemainders;
        this.generalError = generalError;
        this.reportValid = reportValid;
    }
}
