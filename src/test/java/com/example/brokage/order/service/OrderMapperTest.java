package com.example.brokage.order.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.brokage.order.model.Order;
import com.example.brokage.order.model.OrderDto;
import com.example.brokage.order.model.OrderSide;
import com.example.brokage.order.model.Status;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        System.out.println("setting up order mapper..");
        orderMapper = new OrderMapper();
    }

    @AfterEach
    void tearDown() {
        orderMapper = null;
        System.out.println("tearing down order mapper..");
    }

    @Test
    public void testOrderConversionIsCorrect() {
        System.out.println("testing order conversion from dto");
        OrderDto orderDto = new OrderDto(
                "ASSET1",
                OrderSide.SELL,
                1250,
                10.75);
        Order order = orderMapper.toOrder(orderDto);

        compareEquals(orderDto, order);
    }

    @Test
    public void testOrderConversionIsCorrect2() {
        System.out.println("testing order conversion from dto");
        OrderDto orderDto = new OrderDto(
                "ASSET1",
                OrderSide.BUY,
                1100,
                10.99);
        Order order = orderMapper.toOrder(orderDto);

        compareEquals(orderDto, order);
    }

    private static void compareEquals(OrderDto orderDto, Order order) {
        assertEquals(orderDto.orderSide(), order.getOrderSide());
        assertEquals(orderDto.price(), order.getPrice());
        assertEquals(orderDto.size(), order.getSize());
        assertEquals(orderDto.assetName(), order.getAssetName());
        assertEquals(Status.PENDING, order.getStatus());

        // check date is within 4 seconds of difference
        assertTrue(order.getCreateDate().before(new Date(System.currentTimeMillis() + 4000)));
        assertTrue(order.getCreateDate().after(new Date(System.currentTimeMillis() - 4000)));
    }

}