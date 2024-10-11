package com.example.brokage.customer;

import com.example.brokage.customer.model.CustomerRoles;
import com.example.brokage.customer.model.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    public void testCustomer1CanGetOwnData() throws JsonProcessingException {
        String forObject = this.restTemplate
                .withBasicAuth("customer1","Pass1")
                .getForObject("http://localhost:" + port +
                "/brokage/api/v1/customer/1", String.class);

        Customer customer = objectMapper.readValue(forObject, Customer.class);
        assertEqualsCustomerCustomer1(customer);
    }

    @Test
    public void testCustomer1CannotGetOtherCustomerData() {
        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("customer1", "Pass1")
                .getForEntity("http://localhost:" + port +
                        "/brokage/api/v1/customer/2", Customer.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testCustomer1CannotGetOtherCustomerData2() {
        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("customer1", "Pass1")
                .getForEntity("http://localhost:" + port +
                        "/brokage/api/v1/customer/3", Customer.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testCustomer1CannotGetOtherCustomerData3() {
        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("customer1", "Pass1")
                .getForEntity("http://localhost:" + port +
                        "/brokage/api/v1/customer/4", Customer.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testCustomer1CannotGetOtherCustomerData4() {
        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("customer1", "Pass1")
                .getForEntity("http://localhost:" + port +
                        "/brokage/api/v1/customer/5", Customer.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testAdminCanAccessCustomer1() {
        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("http://localhost:" + port +
                        "/brokage/api/v1/customer/1", Customer.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Customer customer1 = response.getBody();
        assertEqualsCustomerCustomer1(customer1);
    }

    @Test
    public void testAdminCanAccessCustomer2() {
        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("http://localhost:" + port +
                        "/brokage/api/v1/customer/2", Customer.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Customer customer2 = response.getBody();
        assertEqualsCustomerCustomer2(customer2);
    }

    @Test
    public void testAdminCanAccessAllCustomers() {
        ResponseEntity<List> admin = this.restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("http://localhost:" + port +
                        "/brokage/api/v1/customer", List.class);

        assertEquals(HttpStatus.OK, admin.getStatusCode());
        assertNotNull(admin.getBody());
        assertEquals(3, admin.getBody().size());
    }

    @Test
    public void testAdminCreatesCustomer() {
        Customer customer3 = Customer.builder()
                .name("Name3")
                .surname("Surname3")
                .username("customer3")
                .password("Pass3")
                .role(CustomerRoles.CUSTOMER)
                .build();

        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("http://localhost:" +
                                port + "/brokage/api/v1/customer",
                                customer3,
                        Customer.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Customer saved = response.getBody();
        assertNotNull(saved);
        assertEqualsCustomers(customer3, saved);
    }

    @Test
    public void testCustomerCannotCreateCustomer() {
        Customer customer4 = Customer.builder()
                .name("Name4")
                .surname("Surname4")
                .username("customer4")
                .password("Pass4")
                .role(CustomerRoles.CUSTOMER)
                .build();

        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("customer1", "Pass1")
                .postForEntity("http://localhost:" +
                                port + "/brokage/api/v1/customer",
                                customer4,
                        Customer.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    private void assertEqualsCustomers(Customer newCustomer, Customer saved) {
        assertEquals(newCustomer.getRole(), saved.getRole());
        assertEquals(newCustomer.getName(), saved.getName());
        assertEquals(newCustomer.getSurname(), saved.getSurname());
        assertEquals(newCustomer.getUsername(), saved.getUsername());
        assertTrue(passwordEncoder.matches(newCustomer.getPassword(), saved.getPassword()));
        assertEquals(newCustomer.getUsername(), saved.getUsername());
    }


    private void assertEqualsCustomerCustomer1(Customer customer) {
        assertEquals("Name1", customer.getName());
        assertEquals("Surname1", customer.getSurname());
        assertEquals("customer1", customer.getUsername());
        assertEquals(1, customer.getId());
        assertEquals(CustomerRoles.CUSTOMER, customer.getRole());
        assertTrue(customer.isAccountNonExpired());
        assertFalse(customer.isAdmin());
        assertTrue(customer.isEnabled());
        assertTrue(customer.isCredentialsNonExpired());
        assertTrue(customer.isAccountNonLocked());
        assertTrue(passwordEncoder.matches("Pass1",customer.getPassword()));
    }

    private void assertEqualsCustomerCustomer2(Customer customer) {
        assertEquals("Name2", customer.getName());
        assertEquals("Surname2", customer.getSurname());
        assertEquals("customer2", customer.getUsername());
        assertEquals(2, customer.getId());
        assertEquals(CustomerRoles.CUSTOMER, customer.getRole());
        assertTrue(customer.isAccountNonExpired());
        assertFalse(customer.isAdmin());
        assertTrue(customer.isEnabled());
        assertTrue(customer.isCredentialsNonExpired());
        assertTrue(customer.isAccountNonLocked());
        assertTrue(passwordEncoder.matches("Pass2",customer.getPassword()));
    }

}