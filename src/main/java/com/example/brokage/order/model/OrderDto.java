package com.example.brokage.order.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record OrderDto(
        @NotNull(message = "Order shall be for an asset")
        @NotEmpty(message = "AssetName cannot be omitted")
        String assetName,

        OrderSide orderSide,

        @PositiveOrZero(message = "Size of the asset must be positive or zero")
        int size,

        @PositiveOrZero(message = "Asset must have a positive or zero price")
        double price
) {
}
