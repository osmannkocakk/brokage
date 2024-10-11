package com.example.brokage.asset.model;

import jakarta.validation.constraints.Positive;

public record DepositMoneyDto(

        @Positive(message = "Deposit money amount must greater than 0")
        double depositAmount
) {
}
