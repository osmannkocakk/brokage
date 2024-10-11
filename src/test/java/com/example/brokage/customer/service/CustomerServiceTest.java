package com.example.brokage.customer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.brokage.customer.CustomerRepo;
import com.example.brokage.customer.model.CustomerRoles;
import com.example.brokage.customer.model.Customer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepo customerRepo;

    @Mock
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        System.out.println("opening mocks..");
        MockitoAnnotations.openMocks(this);
        System.out.println("mocks opened..");
    }

    @Test
    public void testShallSaveCustomer() {
        // Given
        var customer = Customer.builder()
                .username("customer1")
                .name("Name1")
                .surname("Surname1")
                .password("Pass1")
                .role(CustomerRoles.ADMIN)
                .build();

        var expected = Customer.builder()
                .id(1)
                .username("customer1")
                .name("Name1")
                .surname("Surname1")
                .password("encoded-password")
                .role(CustomerRoles.ADMIN)
                .build();

        // Mock the Calls
        Mockito.when(passwordEncoder.encode(customer.getPassword()))
                .thenReturn("encoded-password");

        Mockito.when(customerRepo.save(customer))
                .thenReturn(expected);

        // When
        Customer savedCustomer = customerService.create(customer);

        // Then
        assertEquals(expected.getName(), savedCustomer.getName());
        assertEquals(expected.getSurname(), savedCustomer.getSurname());
        assertEquals(expected.getUsername(), savedCustomer.getUsername());
        assertEquals(expected.getRole(), savedCustomer.getRole());
        assertEquals("encoded-password" , savedCustomer.getPassword());
        assertEquals(expected.getId(), savedCustomer.getId());
    }

    @Test
    public void testShallGetCustomer() {
        // Given
        var expected = Customer.builder()
                .id(1)
                .username("customer1")
                .name("Name1")
                .surname("Surname1")
                .password("encoded-password")
                .role(CustomerRoles.ADMIN)
                .build();

        // Mock the calls
        Mockito.when(customerService.getCustomer(1))
                .thenReturn(Optional.of(expected));

        // When
        Optional<Customer> returnValue = customerService.getCustomer(1);

        // Then
        assertEquals(expected.getUsername(), returnValue.get().getUsername());
        assertEquals(expected.getName(), returnValue.get().getName());
        assertEquals(expected.getSurname(), returnValue.get().getSurname());
        assertEquals(expected.getPassword(), returnValue.get().getPassword());
        assertEquals(expected.getRole(), returnValue.get().getRole());
    }

    @Test
    public void testShallGetAllCustomers() {
        // Given
        var customer1 = Customer.builder()
                .id(1)
                .username("customer1")
                .name("Name1")
                .surname("Surname1")
                .password("encoded-password")
                .role(CustomerRoles.ADMIN)
                .build();

        var customer2 = Customer.builder()
                .id(1)
                .username("customer2")
                .name("Name2")
                .surname("Surname2")
                .password("Customer2-encoded-password")
                .role(CustomerRoles.CUSTOMER)
                .build();

        var expected = List.of(customer1, customer2);

        // Mock the Calls
        Mockito.when(customerRepo.findAll())
                .thenReturn(expected);

        // When
        List<Customer> returnValue = customerService.getAllCustomers();

        // Then
        assertEquals(2, returnValue.size());

        assertEquals(customer1.getName(), returnValue.get(0).getName());
        assertEquals(customer1.getSurname(), returnValue.get(0).getSurname());
        assertEquals(customer1.getUsername(), returnValue.get(0).getUsername());
        assertEquals(customer1.getPassword(), returnValue.get(0).getPassword());
        assertEquals(customer1.getRole(), returnValue.get(0).getRole());

        assertEquals(customer2.getName(), returnValue.get(1).getName());
        assertEquals(customer2.getSurname(), returnValue.get(1).getSurname());
        assertEquals(customer2.getUsername(), returnValue.get(1).getUsername());
        assertEquals(customer2.getPassword(), returnValue.get(1).getPassword());
        assertEquals(customer2.getRole(), returnValue.get(1).getRole());

    }
}