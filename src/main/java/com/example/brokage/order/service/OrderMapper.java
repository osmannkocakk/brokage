package com.example.brokage.order.service;

import org.springframework.stereotype.Service;

import com.example.brokage.order.model.Order;
import com.example.brokage.order.model.OrderDto;
import com.example.brokage.order.model.Status;

import java.util.Date;

@Service
public class OrderMapper {

    public Order toOrder(OrderDto dto) {
        return Order.builder()
                .orderSide(dto.orderSide())
                .assetName(dto.assetName())
                .size(dto.size())
                .price(dto.price())
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();
    }
}
