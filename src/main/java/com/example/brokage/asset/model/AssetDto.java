package com.example.brokage.asset.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;

public record AssetDto(
        @NotEmpty(message = "Asset name shouldn't be empty")
        String assetName,

        @NotNull(message = "Customer id should have a positive value")
        @Positive(message = "Customer id should have a positive value")
        long customerId,

        @PositiveOrZero(message = "Asset size should have a positive value or zero")
        int assetSize
) {
}
