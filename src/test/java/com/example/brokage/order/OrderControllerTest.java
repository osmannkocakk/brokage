package com.example.brokage.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.brokage.order.OrderController;
import com.example.brokage.order.OrderRepo;
import com.example.brokage.order.model.Order;
import com.example.brokage.order.model.OrderDto;
import com.example.brokage.order.model.OrderSide;
import com.example.brokage.order.model.Status;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private OrderController orderController;

    @Autowired
    private OrderRepo orderRepo;


    @Test
    public void testContextLoaded() {

    }

    @Test
    public void testOrderControllerWired() {
        assertThat(orderController).isNotNull();
    }

    @Test
    public void testAdminCanGetAllOrders() {
        ResponseEntity<Order[]> response = restTemplate.withBasicAuth(
                "admin", "admin")
                .getForEntity("http://localhost:" + port + "/brokage/api/v1/order",
                        Order[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order[] orders = response.getBody();
        assertNotNull(orders);
        assertEquals(2, orders.length);
        assertEqualsCustomer1sASSET1BuyOrder(orders[0]);
        assertEqualsCustomer1sASSET2BuyOrder(orders[1]);
    }

    private void assertEqualsCustomer1sASSET2BuyOrder(Order order) {
        assertEquals(2, order.getId());
        assertEquals(1, order.getCustomerId());
        assertEquals(OrderSide.BUY,order.getOrderSide());
        assertEquals(100,order.getSize());
        assertEquals(Status.PENDING,order.getStatus());
        assertEquals(1.0,order.getPrice());
        assertEquals("ASSET2",order.getAssetName());
        assertNotNull(order.getCreateDate());
        assertTrue(order.getCreateDate().after(new Date(System.currentTimeMillis() - 1000*60*60)));
        assertTrue(order.getCreateDate().before(new Date(System.currentTimeMillis() + 1000*60*60)));
    }

    private void assertEqualsCustomer1sASSET1BuyOrder(Order order) {
        assertEquals(1, order.getId());
        assertEquals(1, order.getCustomerId());
        assertEquals(OrderSide.BUY,order.getOrderSide());
        assertEquals(10,order.getSize());
        assertEquals(Status.PENDING,order.getStatus());
        assertEquals(1.733,order.getPrice());
        assertEquals("ASSET1",order.getAssetName());
        assertNotNull(order.getCreateDate());
        assertTrue(order.getCreateDate().after(new Date(System.currentTimeMillis() - 1000*60*60)));
        assertTrue(order.getCreateDate().before(new Date(System.currentTimeMillis() + 1000*60*60)));

    }

    @Test
    public void testCustomer1IsForbiddenForAllOrders() {
        ResponseEntity response = restTemplate.withBasicAuth(
                        "customer1", "admin")
                .getForEntity("http://localhost:" + port + "/brokage/api/v1/order",
                        Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testAdminCanGetOrdersOfCustomer1() {
        ResponseEntity<Order[]> response = restTemplate.withBasicAuth(
                        "admin", "admin")
                .getForEntity("http://localhost:" + port + "/brokage/api/v1/order/1" +
                                "?start-date=20230101&end-date=20260101",
                        Order[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order[] orders = response.getBody();
        assertNotNull(orders);
        assertEquals(2, orders.length);
        assertEqualsCustomer1sASSET1BuyOrder(orders[0]);
        assertEqualsCustomer1sASSET2BuyOrder(orders[1]);
    }

    @Test
    public void testCustomer1CanGetOwnOrders() {
        ResponseEntity<Order[]> response = restTemplate.withBasicAuth(
                        "customer1", "Pass1")
                .getForEntity("http://localhost:" + port + "/brokage/api/v1/order/1" +
                                "?start-date=20230101&end-date=20260101",
                        Order[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order[] orders = response.getBody();
        assertNotNull(orders);
        assertEquals(2, orders.length);
        assertEqualsCustomer1sASSET1BuyOrder(orders[0]);
        assertEqualsCustomer1sASSET2BuyOrder(orders[1]);
    }

    @Test
    public void testAdminCanPlaceAndCancelOrderForCustomer1() {
        // Place the Order
        Long customerId = 1L;
        OrderDto orderDto = new OrderDto("ASSET1",OrderSide.BUY,100, 1.736);
        ResponseEntity<Order> response = restTemplate.withBasicAuth(
                "admin", "admin")
                .postForEntity("http://localhost:" + port + "/brokage/api/v1/order/1",
                        orderDto, Order.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order savedOrder = response.getBody();
        assertNotNull(savedOrder);
        assertEqualsOrderDto2SavedOrder(orderDto, savedOrder, customerId);

        // Cancel the Order to increase usableSize
        restTemplate.withBasicAuth(
                        "admin", "admin")
                .delete("http://localhost:" + port + "/brokage/api/v1/order/1/"
                + savedOrder.getId(), Order.class);

        ResponseEntity<Order> cancelResponse = restTemplate.withBasicAuth(
                        "admin", "admin")
                .getForEntity("http://localhost:" + port + "/brokage/api/v1/order/1/"
                        + savedOrder.getId(), Order.class);
        assertEquals(HttpStatus.OK, cancelResponse.getStatusCode());
        Order cancelledOrder = cancelResponse.getBody();
        assertEquals(Status.CANCELLED, cancelledOrder.getStatus());

        // clean-up for other tests.
        orderRepo.deleteById(cancelledOrder.getId());
    }

    private static void assertEqualsOrderDto2SavedOrder(OrderDto orderDto, Order savedOrder, Long customerId) {
        assertEquals(orderDto.assetName(), savedOrder.getAssetName());
        assertEquals(orderDto.orderSide(), savedOrder.getOrderSide());
        assertEquals(orderDto.size(), savedOrder.getSize());
        assertEquals(orderDto.price(), savedOrder.getPrice());
        assertEquals(Status.PENDING, savedOrder.getStatus());
        assertEquals(customerId, savedOrder.getCustomerId());
    }

    @Test
    public void testCustomer1CanPlaceOrderForHimself() {
        // Place the Order
        Long customerId = 1L;
        OrderDto orderDto = new OrderDto("ASSET2",OrderSide.BUY,1, 1.736);
        ResponseEntity<Order> response = restTemplate.withBasicAuth(
                        "customer1", "Pass1")
                .postForEntity("http://localhost:" + port + "/brokage/api/v1/order/1",
                        orderDto, Order.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order savedOrder = response.getBody();
        assertNotNull(savedOrder);
        assertEqualsOrderDto2SavedOrder(orderDto, savedOrder, customerId);

        // Cancel the Order to increase usableSize
        restTemplate.withBasicAuth(
                        "customer1", "Pass1")
                .delete("http://localhost:" + port + "/brokage/api/v1/order/1/"
                        + savedOrder.getId(), Order.class);

        ResponseEntity<Order> cancelResponse = restTemplate.withBasicAuth(
                        "customer1", "Pass1")
                .getForEntity("http://localhost:" + port + "/brokage/api/v1/order/1/"
                        + savedOrder.getId(), Order.class);
        assertEquals(HttpStatus.OK, cancelResponse.getStatusCode());
        Order cancelledOrder = cancelResponse.getBody();
        assertEquals(Status.CANCELLED, cancelledOrder.getStatus());

        // clean-up for other tests.
        orderRepo.deleteById(cancelledOrder.getId());
    }

    @Test
    public void testCustomer1IsForbiddenToCancelCustomer2sOrders() {
        // Place the Order AS Admin
        OrderDto orderDto = new OrderDto("ASSET1",OrderSide.BUY,100, 1.736);
        ResponseEntity<Order> response = restTemplate.withBasicAuth(
                        "admin", "admin")
                .postForEntity("http://localhost:" + port + "/brokage/api/v1/order/2",
                        orderDto, Order.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Order savedOrder = response.getBody();
        assertNotNull(savedOrder);

        // Cannot Cancel Order as Customer1
        restTemplate.withBasicAuth(
                        "customer1", "Pass1")
                .delete("http://localhost:" + port + "/brokage/api/v1/order/2/"
                        + savedOrder.getId(), Order.class);

        ResponseEntity<Order> cancelResponse = restTemplate.withBasicAuth(
                        "admin", "admin")
                .getForEntity("http://localhost:" + port + "/brokage/api/v1/order/2/"
                        + savedOrder.getId(), Order.class);
        assertEquals(HttpStatus.OK, cancelResponse.getStatusCode());
        Order pendingOrder = cancelResponse.getBody();
        assertEquals(Status.PENDING, pendingOrder.getStatus());

        // clean-up for other tests.
        orderRepo.deleteById(savedOrder.getId());
    }

}