package ru.intelinvest.tronscan.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParseInstruction {
    private boolean correctQuantityByShareLotSize;
    private boolean correctQuantityByAmountAndPrice;
    private boolean holdOriginFeeCurrency;
    private boolean calculateQuantity;
    private boolean calculateNkdPerOne;
    private boolean calculateFacevalue;
    private boolean calculateBondPrice;
}
