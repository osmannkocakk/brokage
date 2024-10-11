package com.example.brokage.order.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST,
        reason = "Order Status Is Not Valid")

public class OrderStatusIsNotValidException extends Exception {
}
