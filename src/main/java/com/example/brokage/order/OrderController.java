package com.example.brokage.order;

import com.example.brokage.asset.service.AssetNotFoundException;
import com.example.brokage.asset.service.NoEnoughAssetException;
import com.example.brokage.asset.service.NoEnoughMoneyException;
import com.example.brokage.customer.service.CustomerNotFoundException;
import com.example.brokage.order.model.Order;
import com.example.brokage.order.model.OrderDto;
import com.example.brokage.order.service.OrderNotFoundException;
import com.example.brokage.order.service.OrderService;
import com.example.brokage.order.service.OrderStatusIsNotValidException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brokage/api/v1/order")
public class OrderController {

    private final OrderService orderService;


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @GetMapping("/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByDate(
            @PathVariable("customerId") Long customerId,

            @DateTimeFormat(pattern="yyyyMMdd")
            @RequestParam(name="start-date")
            Date startDate,

            @DateTimeFormat(pattern="yyyyMMdd")
            @RequestParam(name="end-date", required = true)
            Date endDate
    ) {
        return ResponseEntity.ok(
                orderService.findAllBetween(customerId, startDate, endDate)
        );
    }

    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @GetMapping("/{customerId}/{orderId}")
    public ResponseEntity<Order> getOrder(
            @PathVariable("customerId") Long customerId,

            @PathVariable("orderId") Long orderId
            ) {
        return orderService.findOrder(customerId, orderId)
                .map(ResponseEntity::ok)
                .orElseGet(
                        () -> new ResponseEntity<>(HttpStatus.NOT_FOUND)
                );
    }


    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @PostMapping("/{customerId}")
    public ResponseEntity<Order> createOrder(
            @PathVariable(name = "customerId")
            long customerId,

            @Valid @RequestBody
            OrderDto order
    ) throws AssetNotFoundException, NoEnoughMoneyException, NoEnoughAssetException {
        return ResponseEntity.ok(
                orderService.createOrder(customerId, order)
        );
    }


    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @DeleteMapping("/{customerId}/{orderId}")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable("customerId")
            long customerId,

            @PathVariable("orderId")
            long orderId
    ) throws OrderNotFoundException,
            OrderStatusIsNotValidException,
            AssetNotFoundException,
            CustomerNotFoundException {
        return ResponseEntity.ok(orderService.cancelOrder(orderId,customerId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/match/{orderId}")
    public ResponseEntity<Order> matchOrder(
            @PathVariable("orderId")
            long orderId
    ) throws OrderNotFoundException,
            OrderStatusIsNotValidException,
            AssetNotFoundException {
        return ResponseEntity.ok(orderService.matchOrder(orderId));
    }

}
