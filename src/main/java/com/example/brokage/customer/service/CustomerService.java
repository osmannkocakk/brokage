package com.example.brokage.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.brokage.customer.CustomerRepo;
import com.example.brokage.customer.model.Customer;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepo customerRepo;
    private final PasswordEncoder passwordEncoder;

    public Customer create(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerRepo.save(customer);
    }

    public Optional<Customer> getCustomer(long customerId) {
        return customerRepo.findById(customerId);
    }

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }
}
