package com.example.brokage.asset.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record WithdrawMoneyDto(

        @NotNull(message = "Deposit money amount must have a value")
        @Positive(message = "Deposit money amount must greater than 0")
        double withdrawAmount,

        @NotEmpty(message = "IBAN must not be empty")
        @Length(message = "IBAN length must be 26 characters", min = 26, max = 26)
        String iban
) {
}
