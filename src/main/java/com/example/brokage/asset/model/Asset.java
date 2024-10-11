package com.example.brokage.asset.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "asset",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = { "customerId", "assetName" })
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message = "Customer id shouldn't be null")
    private long customerId;

    @NotEmpty(message = "Asset name shouldn't be empty")
    private String assetName;

    @NotNull(message = "Size should have a positive value")
    @Positive(message = "Size should have a positive value")
    private double size;

    @NotNull(message = "Usable size should have a positive value or zero")
    @PositiveOrZero(message = "Usable size should have a positive value or zero")
    private double usableSize;
}