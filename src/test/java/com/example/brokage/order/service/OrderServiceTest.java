package com.example.brokage.order.service;

import com.example.brokage.asset.AssetRepo;
import com.example.brokage.asset.model.Asset;
import com.example.brokage.asset.model.AssetNames;
import com.example.brokage.asset.service.AssetNotFoundException;
import com.example.brokage.asset.service.NoEnoughAssetException;
import com.example.brokage.asset.service.NoEnoughMoneyException;
import com.example.brokage.customer.service.CustomerNotFoundException;
import com.example.brokage.order.OrderRepo;
import com.example.brokage.order.model.Order;
import com.example.brokage.order.model.OrderDto;
import com.example.brokage.order.model.OrderSide;
import com.example.brokage.order.model.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepo orderRepo;

    @Mock
    AssetRepo assetRepo;

    @Mock
    OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShallCreateBuyOrder() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("ASSET1", OrderSide.BUY, 100, 1.736);

        Order buyASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.BUY)
                .size(100)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultBuyASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.BUY)
                .size(100)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset TRY_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .usableSize(10000)
                .size(10000)
                .build();

        Asset savedTRY_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(10000)
                .usableSize(10000 - 100*10.80)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(buyASSET1);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                AssetNames.TRY.name()
        )).thenReturn(Optional.of(TRY_Asset));

        Mockito.when(assetRepo.save(TRY_Asset))
                .thenReturn(savedTRY_Asset);

        Mockito.when(orderRepo.save(buyASSET1))
                .thenReturn(resultBuyASSET1);

        //;
        //return orderRepo.save(order);

        // When

        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw AssetNotFoundException");
        } catch (NoEnoughMoneyException e) {
            fail("shall not throw NoEnoughMoneyException");
        } catch (NoEnoughAssetException e) {
            fail("shall not throw NoEnoughAssetException");
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(savedTRY_Asset.getUsableSize(), updatedAsset.getUsableSize());

        // Then
        assertNotNull(returnValue);
        assertEquals(buyASSET1.getId(), returnValue.getId());
        assertEquals(buyASSET1.getOrderSide(), returnValue.getOrderSide());
        assertEquals(buyASSET1.getSize(), returnValue.getSize());
        assertEquals(buyASSET1.getPrice(), returnValue.getPrice());
        assertEquals(Status.PENDING, returnValue.getStatus());
        assertEquals(buyASSET1.getCreateDate(), returnValue.getCreateDate());
        assertEquals(buyASSET1.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testShallCreateSellOrder() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("ASSET1", OrderSide.SELL, 100, 1.736);

        Order sellASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultSELLASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset ASSET1_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("ASSET1")
                .usableSize(125)
                .size(125)
                .build();

        Asset savedASSET1_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("ASSET1")
                .usableSize(25)
                .size(125)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(sellASSET1);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                "ASSET1"
        )).thenReturn(Optional.of(ASSET1_Asset));

        Mockito.when(assetRepo.save(ASSET1_Asset))
                .thenReturn(savedASSET1_Asset);

        Mockito.when(orderRepo.save(sellASSET1))
                .thenReturn(resultSELLASSET1);

        // When

        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw AssetNotFoundException");
        } catch (NoEnoughMoneyException e) {
            fail("shall not throw NoEnoughMoneyException");
        } catch (NoEnoughAssetException e) {
            fail("shall not throw NoEnoughAssetException");
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(savedASSET1_Asset.getUsableSize(), updatedAsset.getUsableSize());


        // Then
        assertNotNull(returnValue);
        assertEquals(sellASSET1.getId(), returnValue.getId());
        assertEquals(sellASSET1.getOrderSide(), returnValue.getOrderSide());
        assertEquals(sellASSET1.getSize(), returnValue.getSize());
        assertEquals(sellASSET1.getPrice(), returnValue.getPrice());
        assertEquals(Status.PENDING, returnValue.getStatus());
        assertEquals(sellASSET1.getCreateDate(), returnValue.getCreateDate());
        assertEquals(sellASSET1.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testShallThrowNoEnoughAssetException() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("ASSET1", OrderSide.SELL, 11, 1.736);

        Order sellASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultSELLASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset ASSET1_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("ASSET1")
                .usableSize(10)
                .size(125)
                .build();

        Asset savedASSET1_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("ASSET1")
                .usableSize(10)
                .size(125)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(sellASSET1);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                "ASSET1"
        )).thenReturn(Optional.of(ASSET1_Asset));

        Mockito.when(assetRepo.save(ASSET1_Asset))
                .thenReturn(savedASSET1_Asset);

        Mockito.when(orderRepo.save(sellASSET1))
                .thenReturn(resultSELLASSET1);

        // When

        var msg = assertThrows(NoEnoughAssetException.class,
                () -> orderService.createOrder(customerId, orderDto),
                "should have thrown NoEnoughAssetException");
        assertEquals("There is No Enough Asset", msg.getMessage());
    }

    @Test
    public void testShallNotThrowNoEnoughAssetExceptionOnBoundary() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("ASSET1", OrderSide.SELL, 10, 1.736);

        Order sellASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.SELL)
                .size(10)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultSELLASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.SELL)
                .size(10)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset ASSET1_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("ASSET1")
                .usableSize(10)
                .size(125)
                .build();

        Asset savedASSET1_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("ASSET1")
                .usableSize(0)
                .size(125)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(sellASSET1);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                "ASSET1"
        )).thenReturn(Optional.of(ASSET1_Asset));

        Mockito.when(assetRepo.save(ASSET1_Asset))
                .thenReturn(savedASSET1_Asset);

        Mockito.when(orderRepo.save(sellASSET1))
                .thenReturn(resultSELLASSET1);

        // When

        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw AssetNotFoundException");
        } catch (NoEnoughMoneyException e) {
            fail("shall not throw NoEnoughMoneyException");
        }catch (NoEnoughAssetException e) {
            fail("shall not throw NoEnoughAssetException");
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(savedASSET1_Asset.getUsableSize(), updatedAsset.getUsableSize());

        // Then
        assertNotNull(returnValue);
        assertEquals(sellASSET1.getId(), returnValue.getId());
        assertEquals(sellASSET1.getOrderSide(), returnValue.getOrderSide());
        assertEquals(sellASSET1.getSize(), returnValue.getSize());
        assertEquals(sellASSET1.getPrice(), returnValue.getPrice());
        assertEquals(Status.PENDING, returnValue.getStatus());
        assertEquals(sellASSET1.getCreateDate(), returnValue.getCreateDate());
        assertEquals(sellASSET1.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testShallThrowNotEnoughMoneyExceptionWhenSo() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("ASSET1", OrderSide.BUY, 10000, 10.80);

        Order buyASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.BUY)
                .size(10000)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultBuyASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.BUY)
                .size(10000)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset TRY_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .usableSize(10000)
                .size(10000)
                .build();

        Asset savedTRY_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(10000)
                .usableSize(10000 - 10.80*10000)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(buyASSET1);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                AssetNames.TRY.name()
        )).thenReturn(Optional.of(TRY_Asset));

        Mockito.when(assetRepo.save(TRY_Asset))
                .thenReturn(savedTRY_Asset);

        Mockito.when(orderRepo.save(buyASSET1))
                .thenReturn(resultBuyASSET1);


        // When

        boolean notEnoughMoneyExcThrown = false;
        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw AssetNotFoundException");
        } catch (NoEnoughMoneyException e) {
            notEnoughMoneyExcThrown = true;
        }catch (NoEnoughAssetException e) {
            fail("shall not throw NoEnoughAssetException");
        }

        // Then
        assertNull(returnValue);
        assertTrue(notEnoughMoneyExcThrown);
    }

    @Test
    public void testShallThrowNotEnoughMoneyExceptionWhenJustEqual() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("ASSET1", OrderSide.BUY, 10000, 1);

        Order buyASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.BUY)
                .size(10000)
                .price(1)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultBuyASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.BUY)
                .size(10000)
                .price(1)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset TRY_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .usableSize(10000)
                .size(10000)
                .build();

        Asset savedTRY_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(10000)
                .usableSize(10000 - 1.0*10000)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(buyASSET1);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                AssetNames.TRY.name()
        )).thenReturn(Optional.of(TRY_Asset));

        Mockito.when(assetRepo.save(TRY_Asset))
                .thenReturn(savedTRY_Asset);

        Mockito.when(orderRepo.save(buyASSET1))
                .thenReturn(resultBuyASSET1);



        // When

        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw AssetNotFoundException");
        } catch (NoEnoughMoneyException e) {
            fail("shall not throw NoEnoughMoneyException");
        }catch (NoEnoughAssetException e) {
            fail("shall not throw NoEnoughAssetException");
        }

        // Then
        assertNotNull(returnValue);
        assertEquals(buyASSET1.getId(), returnValue.getId());
        assertEquals(buyASSET1.getOrderSide(), returnValue.getOrderSide());
        assertEquals(buyASSET1.getSize(), returnValue.getSize());
        assertEquals(buyASSET1.getPrice(), returnValue.getPrice());
        assertEquals(Status.PENDING, returnValue.getStatus());
        assertEquals(buyASSET1.getCreateDate(), returnValue.getCreateDate());
        assertEquals(buyASSET1.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testShallThrowAssetNotFoundExceptionWhenSo() {
        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("ASSET1", OrderSide.BUY, 10000, 10.80);

        Order buyASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.BUY)
                .size(10000)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultBuyASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.BUY)
                .size(10000)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(buyASSET1);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                AssetNames.TRY.name()
        )).thenReturn(Optional.empty());

        //;
        //return orderRepo.save(order);

        // When

        boolean assetNotFoundExcThrown = false;
        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            assetNotFoundExcThrown = true;
        } catch (NoEnoughMoneyException e) {
            fail("shall not throw NoEnoughMoneyException");
        }catch (NoEnoughAssetException e) {
            fail("shall not throw NoEnoughAssetException");
        }

        // Then
        assertNull(returnValue);
        assertTrue(assetNotFoundExcThrown);
    }

    @Test
    public void testCanCancelBuyOrder() {
        // Given
        var customerId = 1L;

        Order buyASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.BUY)
                .size(100)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultBuyASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.BUY)
                .size(100)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.CANCELLED)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset TRY_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(10000)
                .usableSize(10000 - 10.80*100)
                .build();

        Asset savedTRY_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(10000)
                .usableSize(10000)
                .build();


        // Mock the Calls
        Mockito.when(orderRepo.findByIdForUpdate(buyASSET1.getId()))
                .thenReturn(Optional.of(buyASSET1));

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                AssetNames.TRY.name()
        )).thenReturn(Optional.of(TRY_Asset));

        Mockito.when(assetRepo.save(TRY_Asset))
                .thenReturn(savedTRY_Asset);

        Mockito.when(orderRepo.save(buyASSET1))
                .thenReturn(resultBuyASSET1);

        // When
        Order returnValue = null;
        try {
            returnValue = orderService.cancelOrder(buyASSET1.getId(), customerId);
        } catch (OrderNotFoundException e) {
            fail("shall not throw OrderNotFoundException");
        } catch (OrderStatusIsNotValidException e) {
            fail("shall not throw OrderStatusIsNotValidException");
        } catch (CustomerNotFoundException e) {
            fail("shall not throw CustomerNotFoundException");
        } catch (AssetNotFoundException e) {
            fail("shall not throw AssetNotFoundException");
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(savedTRY_Asset.getUsableSize(), updatedAsset.getUsableSize());

        // Then
        assertNotNull(returnValue);
        assertEquals(buyASSET1.getId(), returnValue.getId());
        assertEquals(buyASSET1.getOrderSide(), returnValue.getOrderSide());
        assertEquals(buyASSET1.getSize(), returnValue.getSize());
        assertEquals(buyASSET1.getPrice(), returnValue.getPrice());
        assertEquals(Status.CANCELLED, returnValue.getStatus());
        assertEquals(buyASSET1.getCreateDate(), returnValue.getCreateDate());
        assertEquals(buyASSET1.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testCanCancelSellOrder() {

        // Given
        var customerId = 1L;
        var orderDto = new OrderDto("ASSET1", OrderSide.SELL, 100, 10.80);

        Order sellASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order resultSELLASSET1 = Order.builder()
                .assetName("ASSET1")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(10.80)
                .id(2)
                .customerId(customerId)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset ASSET1_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("ASSET1")
                .usableSize(125)
                .size(125)
                .build();

        Asset savedASSET1_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName("ASSET1")
                .usableSize(25)
                .size(125)
                .build();


        // Mock the Calls
        Mockito.when(orderMapper.toOrder(orderDto))
                .thenReturn(sellASSET1);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,
                "ASSET1"
        )).thenReturn(Optional.of(ASSET1_Asset));

        Mockito.when(assetRepo.save(ASSET1_Asset))
                .thenReturn(savedASSET1_Asset);

        Mockito.when(orderRepo.save(sellASSET1))
                .thenReturn(resultSELLASSET1);

        // When
        Order returnValue = null;
        try {
            returnValue = orderService.createOrder(customerId, orderDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw AssetNotFoundException");
        } catch (NoEnoughMoneyException e) {
            fail("shall not throw NoEnoughMoneyException");
        } catch (NoEnoughAssetException e) {
            fail("shall not throw NoEnoughAssetException");
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(savedASSET1_Asset.getUsableSize(), updatedAsset.getUsableSize());


        // Then
        assertNotNull(returnValue);
        assertEquals(sellASSET1.getId(), returnValue.getId());
        assertEquals(sellASSET1.getOrderSide(), returnValue.getOrderSide());
        assertEquals(sellASSET1.getSize(), returnValue.getSize());
        assertEquals(sellASSET1.getPrice(), returnValue.getPrice());
        assertEquals(Status.PENDING, returnValue.getStatus());
        assertEquals(sellASSET1.getCreateDate(), returnValue.getCreateDate());
        assertEquals(sellASSET1.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testShallMatchBuyOrders() {
        // Given
        var customerId = 1L;
        var orderId = 1L;

        Order buyASSET1 = Order.builder()
                .id(1)
                .customerId(customerId)
                .assetName("ASSET1")
                .orderSide(OrderSide.BUY)
                .size(10)
                .price(10.80)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order savedBuyASSET1 = Order.builder()
                .id(1)
                .customerId(customerId)
                .assetName("ASSET1")
                .orderSide(OrderSide.BUY)
                .size(10)
                .price(10.80)
                .status(Status.MATCHED)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order buyASSET2 = Order.builder()
                .id(2)
                .customerId(customerId)
                .assetName("ASSET2")
                .orderSide(OrderSide.BUY)
                .size(100)
                .price(1)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset TRY_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(1000)
                .usableSize(1000 - (10.80 * 10) - (1.0 * 100))
                .build();

        Asset savedTRY_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(1000 - (10.80 * 10))
                .usableSize(1000 - (10.80 * 10) - (1.0 * 100))
                .build();

        Asset newASSET1_Asset = Asset.builder()
                .customerId(customerId)
                .assetName("ASSET1")
                .size(10)
                .usableSize(10)
                .build();

        Asset savedASSET1_Asset = Asset.builder()
                .id(5)
                .customerId(customerId)
                .assetName("ASSET1")
                .size(10)
                .usableSize(10)
                .build();

        // Mock the Calls
        Mockito.when(orderRepo.findByIdForUpdate(orderId))
                .thenReturn(Optional.of(buyASSET1));

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                customerId,  AssetNames.TRY.name()))
                .thenReturn(Optional.of(TRY_Asset));

        Mockito.when(assetRepo.save(TRY_Asset))
                .thenReturn(savedTRY_Asset);

        Mockito.when(orderRepo.save(buyASSET1))
                .thenReturn(savedBuyASSET1);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                buyASSET2.getCustomerId(), buyASSET1.getAssetName()
        )).thenReturn(Optional.empty());

        Mockito.when(assetRepo.save(newASSET1_Asset))
                .thenReturn(savedASSET1_Asset);

        // When

        Order returnValue = null;
        try {
            returnValue = orderService.matchOrder(orderId);
        } catch (OrderNotFoundException e) {
            fail("shall not throw OrderNotFoundException");
        } catch (AssetNotFoundException e) {
            fail("shall not throw AssetNotFoundException");
        }

        verify(assetRepo, times(1)).save(TRY_Asset);
        verify(assetRepo, times(1)).save(newASSET1_Asset);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo, times(2)).save(assetCaptor.capture());
        List<Asset> allValues = assetCaptor.getAllValues();
        assertEquals(2, allValues.size());

        // Assert that TRY asset is updated correctly.
        Asset capturedTRY_Asset = allValues.get(0);
        assertEquals(savedTRY_Asset.getSize(), capturedTRY_Asset.getSize());
        assertEquals(savedTRY_Asset.getUsableSize(), capturedTRY_Asset.getUsableSize());

        // Assert that ASSET1 Asset is created correctly.
        Asset capturedASSET1_Asset = allValues.get(1);
        assertEquals(savedASSET1_Asset.getSize(), capturedASSET1_Asset.getSize());
        assertEquals(savedASSET1_Asset.getUsableSize(), capturedASSET1_Asset.getUsableSize());

        // Then
        assertNotNull(returnValue);
        assertEquals(savedBuyASSET1.getId(), returnValue.getId());
        assertEquals(savedBuyASSET1.getOrderSide(), returnValue.getOrderSide());
        assertEquals(savedBuyASSET1.getSize(), returnValue.getSize());
        assertEquals(savedBuyASSET1.getPrice(), returnValue.getPrice());
        assertEquals(Status.MATCHED, returnValue.getStatus());
        assertEquals(savedBuyASSET1.getCreateDate(), returnValue.getCreateDate());
        assertEquals(savedBuyASSET1.getCustomerId(), returnValue.getCustomerId());
    }

    @Test
    public void testShallMatchSellOrders() {
        // Given
        var customerId = 1L;
        var orderId = 3L;

        Order sellASSET3 = Order.builder()
                .id(3)
                .customerId(customerId)
                .assetName("ASSET3")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(90.0)
                .status(Status.PENDING)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Order savedSellASSET3 = Order.builder()
                .id(3)
                .customerId(customerId)
                .assetName("ASSET3")
                .orderSide(OrderSide.SELL)
                .size(100)
                .price(90.0)
                .status(Status.MATCHED)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        Asset TRY_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(1000)
                .usableSize(1000 - (10.80 * 10) - (1.0 * 100))
                .build();

        Asset savedTRY_Asset = Asset.builder()
                .id(1)
                .customerId(customerId)
                .assetName(AssetNames.TRY.name())
                .size(1000 + (90.0 * 100))
                .usableSize(1000 - (10.80 * 10) - (1.0 * 100) + (90.0 * 100))
                .build();

        Asset ASSET3_Asset = Asset.builder()
                .id(2L)
                .customerId(customerId)
                .assetName("ASSET3")
                .size(1000)
                .usableSize(900)
                .build();

        Asset savedASSET3_Asset = Asset.builder()
                .id(2L)
                .customerId(customerId)
                .assetName("ASSET3")
                .size(900)
                .usableSize(900)
                .build();

        // Mock the Calls
        Mockito.when(orderRepo.findByIdForUpdate(orderId))
                .thenReturn(Optional.of(sellASSET3));

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        customerId,  AssetNames.TRY.name()))
                .thenReturn(Optional.of(TRY_Asset));

        Mockito.when(assetRepo.save(TRY_Asset))
                .thenReturn(savedTRY_Asset);

        Mockito.when(orderRepo.save(sellASSET3))
                .thenReturn(savedSellASSET3);

        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                sellASSET3.getCustomerId(), sellASSET3.getAssetName()
        )).thenReturn(Optional.of(ASSET3_Asset));

        Mockito.when(assetRepo.save(ASSET3_Asset))
                .thenReturn(savedASSET3_Asset);

        // When

        Order returnValue = null;
        try {
            returnValue = orderService.matchOrder(orderId);
        } catch (OrderNotFoundException e) {
            fail("shall not throw OrderNotFoundException");
        } catch (AssetNotFoundException e) {
            fail("shall not throw AssetNotFoundException");
        }

        verify(assetRepo, times(1)).save(TRY_Asset);
        verify(assetRepo, times(1)).save(ASSET3_Asset);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo, times(2)).save(assetCaptor.capture());
        List<Asset> allValues = assetCaptor.getAllValues();
        assertEquals(2, allValues.size());

        // Assert that TRY asset is updated correctly.
        Asset capturedTRY_Asset = allValues.get(0);
        assertEquals(savedTRY_Asset.getSize(), capturedTRY_Asset.getSize());
        assertEquals(savedTRY_Asset.getUsableSize(), capturedTRY_Asset.getUsableSize());

        // Assert that ASSET3 is created correctly.
        Asset capturedASSET3_Asset = allValues.get(1);
        assertEquals(savedASSET3_Asset.getSize(), capturedASSET3_Asset.getSize());
        assertEquals(savedASSET3_Asset.getUsableSize(), capturedASSET3_Asset.getUsableSize());

        // Then
        assertNotNull(returnValue);
        assertEquals(savedSellASSET3.getId(), returnValue.getId());
        assertEquals(savedSellASSET3.getOrderSide(), returnValue.getOrderSide());
        assertEquals(savedSellASSET3.getSize(), returnValue.getSize());
        assertEquals(savedSellASSET3.getPrice(), returnValue.getPrice());
        assertEquals(Status.MATCHED, returnValue.getStatus());
        assertEquals(savedSellASSET3.getCreateDate(), returnValue.getCreateDate());
        assertEquals(savedSellASSET3.getCustomerId(), returnValue.getCustomerId());
    }

}