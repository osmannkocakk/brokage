package com.example.brokage.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message = "Customer id shouldn't be null")
    private long customerId;

    @NotEmpty(message = "Asset name shouldn't be empty")
    private String assetName;

    private OrderSide orderSide;

    @NotNull(message = "Size should have a positive value")
    @Positive(message = "Size should have a positive value")
    private double size;

    @NotNull(message = "Price should have a positive value")
    @Positive(message = "Price should have a positive value")
    private double price;

    private Status status;

    @NotNull(message = "Create date should have a value")
    private Date createDate;
}