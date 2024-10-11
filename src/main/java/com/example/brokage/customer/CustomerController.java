package com.example.brokage.customer;

import com.example.brokage.customer.model.Customer;
import com.example.brokage.customer.service.CustomerService;
import com.example.brokage.utilities.ControllerUtilities;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brokage/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomer(
            @PathVariable("customerId")
            long customerId
    ) {
        return customerService.getCustomer(customerId)
                .map(ResponseEntity::ok)
                .orElseGet(
                        () -> new ResponseEntity<>(HttpStatus.NOT_FOUND)
                );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Customer> createCustomer(
            @Valid @RequestBody
            Customer customer,

            BindingResult bindingResult
    ) {
        if(bindingResult.hasErrors()) {
            ControllerUtilities.logErrors(bindingResult);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(customerService.create(customer));
        }
    }
}
