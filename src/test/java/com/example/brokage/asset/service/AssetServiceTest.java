package com.example.brokage.asset.service;

import com.example.brokage.asset.AssetRepo;
import com.example.brokage.asset.model.Asset;
import com.example.brokage.asset.model.AssetNames;
import com.example.brokage.asset.model.DepositMoneyDto;
import com.example.brokage.asset.model.WithdrawMoneyDto;
import com.example.brokage.customer.service.CustomerNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class AssetServiceTest {

    @InjectMocks
    private AssetService assetService;

    @Mock
    private AssetRepo assetRepo;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShallReturnAssetsOfACustomer() {
        // Given
        Asset TRY_Asset = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(500000)
                .usableSize(500000)
                .build();

        Asset ASSET1_Asset = Asset.builder()
                .id(2)
                .customerId(1)
                .assetName("ASSET1")
                .size(2500)
                .usableSize(2500)
                .build();

        List<Asset> expectedAssets = List.of(
                TRY_Asset, ASSET1_Asset);

        // Mock the calls
        Mockito.when(assetRepo.findAllByCustomerId(1))
                .thenReturn(expectedAssets);

        // When
        List<Asset> returnValue = null;

        try {
            returnValue = assetService.getAssetsOfCustomer(1);
        } catch (CustomerNotFoundException e) {
            fail("shall not throw customer not found exception.");
            e.printStackTrace();
        }

        // Then
        assertNotNull(returnValue);
        checkEquality(TRY_Asset, returnValue);
    }

    private static void checkEquality(Asset TRY_Asset, List<Asset> returnValue) {
        assertEquals(TRY_Asset.getId(), returnValue.get(0).getId());
        assertEquals(TRY_Asset.getCustomerId(), returnValue.get(0).getCustomerId());
        assertEquals(TRY_Asset.getAssetName(), returnValue.get(0).getAssetName());
        assertEquals(TRY_Asset.getSize(), returnValue.get(0).getSize());
        assertEquals(TRY_Asset.getUsableSize(), returnValue.get(0).getUsableSize());
    }


    @Test
    public void testShallDepositMoney() {
        // Given
        Asset TRY_Asset = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(500000)
                .usableSize(500000)
                .build();

        Asset ASSET1_Asset = Asset.builder()
                .id(2)
                .customerId(1)
                .assetName("ASSET1")
                .size(2500)
                .usableSize(2500)
                .build();

        Asset TRY_AssetExpected = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(502500)
                .usableSize(502500)
                .build();

        List<Asset> expectedAssets = List.of(
                TRY_Asset, ASSET1_Asset);

        DepositMoneyDto depositMoneyDto = new DepositMoneyDto(2500);
        // Mock the calls
        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        1, AssetNames.TRY.name()))
                .thenReturn(Optional.of(TRY_Asset));

        Mockito.when(assetRepo.save(null))
                .thenReturn(TRY_AssetExpected);

        // When
        Double remainingAmount = null;
        try {
            remainingAmount = assetService.depositMoney(1, depositMoneyDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw asset not found exception.");
            e.printStackTrace();
        }
        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(TRY_AssetExpected.getUsableSize(), updatedAsset.getUsableSize());
        // Then
        assertNotNull(remainingAmount);
        assertEquals(502500, remainingAmount);
    }

    @Test
    public void testShallThrowAssetNotFoundException() {
        // Given
        Asset ASSET1_Asset = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("ASSET1")
                .size(3000)
                .usableSize(3000)
                .build();

        DepositMoneyDto depositMoneyDto = new DepositMoneyDto(3000);

        // Mock the calls
        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        1, AssetNames.TRY.name()))
                .thenReturn(Optional.empty());

        // When
        boolean exceptionThrown = false;
        try {
            var remaining = assetService.depositMoney(1, depositMoneyDto);
        } catch (AssetNotFoundException e) {
            exceptionThrown = true;
        }

        // Then
        assertTrue(exceptionThrown);
    }

    @Test
    public void testShallWithdrawMoney() {
        // Given
        Asset TRY_Asset = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(250000)
                .usableSize(250000)
                .build();

        Asset ASSET1_Asset = Asset.builder()
                .id(2)
                .customerId(1)
                .assetName("ASSET1")
                .size(1000)
                .usableSize(1000)
                .build();

        Asset TRY_AssetExpected = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(249000)
                .usableSize(249000)
                .build();


        List<Asset> expectedAssets = List.of(
                TRY_Asset, ASSET1_Asset);

        WithdrawMoneyDto depositMoneyDto = new WithdrawMoneyDto(1000,"TR330006100519786457841326");

        // Mock the calls
        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        1, AssetNames.TRY.name()))
                .thenReturn(Optional.of(TRY_Asset));

        Mockito.when(assetRepo.save(null))
                .thenReturn(TRY_AssetExpected);

        // When
        Double remainingAmount = null;
        try {
            remainingAmount = assetService.withdrawMoney(1, depositMoneyDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw assert not found exception.");
            e.printStackTrace();
        } catch (NoEnoughMoneyException e) {
            fail("shall not throw not enough money exception.");
            e.printStackTrace();
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(TRY_AssetExpected.getUsableSize(), updatedAsset.getUsableSize());

        // Then
        assertNotNull(remainingAmount);
        assertEquals(249000, remainingAmount);
    }

    @Test
    public void testShallThrowNotEnoughMoneyExceptionWhenSo() {
        // Given
        Asset TRY_Asset = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(10000)
                .usableSize(10000)
                .build();

        Asset ASSET1_Asset = Asset.builder()
                .id(2)
                .customerId(1)
                .assetName("ASSET1")
                .size(20000)
                .usableSize(20000)
                .build();

        Asset TRY_AssetExpected = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(10000)
                .usableSize(10000)
                .build();


        List<Asset> expectedAssets = List.of(
                TRY_Asset, ASSET1_Asset);

        WithdrawMoneyDto depositMoneyDto = new WithdrawMoneyDto(15000,"TR330006100519786457841326");

        // Mock the calls
        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        1, AssetNames.TRY.name()))
                .thenReturn(Optional.of(TRY_Asset));

        Mockito.when(assetRepo.save(null))
                .thenReturn(TRY_AssetExpected);

        // When
        boolean notEnoughMoneyExceptionThrown = false;
        Double remainingAmount = null;
        try {
            remainingAmount = assetService.withdrawMoney(1, depositMoneyDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw assert not found exception.");
            e.printStackTrace();
        } catch (NoEnoughMoneyException e) {
            notEnoughMoneyExceptionThrown = true;
            e.printStackTrace();
        }

        // Then
        assertNull(remainingAmount);
        assertTrue(notEnoughMoneyExceptionThrown);
    }

    @Test
    public void testShallNotThrowNotEnoughMoneyExceptionOnBoundary() {
        // Given
        Asset TRY_Asset = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(1000)
                .usableSize(1000)
                .build();

        Asset ASSET1_Asset = Asset.builder()
                .id(2)
                .customerId(1)
                .assetName("ASSET1")
                .size(1000)
                .usableSize(1000)
                .build();

        Asset TRY_AssetExpected = Asset.builder()
                .id(1)
                .customerId(1)
                .assetName("TRY")
                .size(1000)
                .usableSize(0)
                .build();


        List<Asset> expectedAssets = List.of(
                TRY_Asset, ASSET1_Asset);

        WithdrawMoneyDto depositMoneyDto = new WithdrawMoneyDto(1000,"TR330006100519786457841326");

        // Mock the calls
        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        1, AssetNames.TRY.name()))
                .thenReturn(Optional.of(TRY_Asset));

        Mockito.when(assetRepo.save(null))
                .thenReturn(TRY_AssetExpected);

        // When
        boolean notEnoughMoneyExceptionThrown = false;
        Double remainingAmount = null;
        try {
            remainingAmount = assetService.withdrawMoney(1, depositMoneyDto);
        } catch (AssetNotFoundException e) {
            fail("shall not throw assert not found exception.");
            e.printStackTrace();
        } catch (NoEnoughMoneyException e) {
            fail("shall not throw not enough money exception");
            notEnoughMoneyExceptionThrown = true;
            e.printStackTrace();
        }

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetCaptor.capture());
        Asset updatedAsset = assetCaptor.getValue();
        assertNotNull(updatedAsset);
        assertEquals(TRY_AssetExpected.getUsableSize(), updatedAsset.getUsableSize());


        // Then
        assertNotNull(remainingAmount);
        assertFalse(notEnoughMoneyExceptionThrown);
    }

    @Test
    public void testShallThrowAssetNotFoundExceptionWhileWithdraw() {
        // Given
        Asset ASSET1_Asset = Asset.builder()
                .id(2)
                .customerId(1)
                .assetName("ASSET1")
                .size(2000)
                .usableSize(2000)
                .build();

        List<Asset> expectedAssets = List.of(ASSET1_Asset);

        WithdrawMoneyDto depositMoneyDto = new WithdrawMoneyDto(2000,"TR330006100519786457841326");

        // Mock the calls
        Mockito.when(assetRepo.findByCustomerIdAndAssetName(
                        1, AssetNames.TRY.name()))
                .thenReturn(Optional.empty());

        Mockito.when(assetRepo.save(null))
                .thenReturn(null);

        // When
        boolean assetNotFoundExceptionThrown = false;
        Double remainingAmount = null;
        try {
            remainingAmount = assetService.withdrawMoney(1, depositMoneyDto);
        } catch (AssetNotFoundException e) {
            assetNotFoundExceptionThrown = true;
        } catch (NoEnoughMoneyException e) {
            fail("shall not throw not enough money exception");
            e.printStackTrace();
        }

        // Then
        assertNull(remainingAmount);
        assertTrue(assetNotFoundExceptionThrown);
    }


}