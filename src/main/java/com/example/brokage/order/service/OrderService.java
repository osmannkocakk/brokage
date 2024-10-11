package com.example.brokage.order.service;

import com.example.brokage.asset.*;
import com.example.brokage.asset.model.Asset;
import com.example.brokage.asset.model.AssetNames;
import com.example.brokage.asset.service.AssetNotFoundException;
import com.example.brokage.asset.service.AssetMissingException;
import com.example.brokage.asset.service.NoEnoughAssetException;
import com.example.brokage.asset.service.NoEnoughMoneyException;
import com.example.brokage.customer.service.CustomerNotFoundException;
import com.example.brokage.order.OrderRepo;
import com.example.brokage.order.model.Order;
import com.example.brokage.order.model.OrderDto;
import com.example.brokage.order.model.Status;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final AssetRepo assetRepo;
    private final OrderMapper orderMapper;

    @Transactional
    public Order createOrder(long customerId, OrderDto dto) throws AssetNotFoundException, NoEnoughMoneyException, NoEnoughAssetException {
        Order order = orderMapper.toOrder(dto);
        order.setCustomerId(customerId);
        return createOrder(order);
    }

    @Transactional
    public Order createOrder(Order order) throws NoEnoughMoneyException, AssetNotFoundException, NoEnoughAssetException {
        return switch (order.getOrderSide()) {
            case BUY -> processBuyOrder(order);
            case SELL -> processSellOrder(order);
        };

    }

    private Order processBuyOrder(Order order) throws NoEnoughMoneyException, AssetNotFoundException {
        // Lock Customer's TRY Asset when getting data to prevent conflicting updates like lost updates.
        Asset TRY_Asset = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(),
                AssetNames.TRY.name()
        ).orElseThrow(AssetNotFoundException::new);

        double cost = order.getSize() * order.getPrice();
        // if TRY asset has enough money then create the order.
        if (TRY_Asset.getUsableSize() >= cost) {
            TRY_Asset.setUsableSize(TRY_Asset.getUsableSize() - cost);
            assetRepo.save(TRY_Asset);
            order.setStatus(Status.PENDING);
            return orderRepo.save(order);
        } else {
            // If TRY asset has not enough money, throw exception
            throw new NoEnoughMoneyException();
        }
    }

    private Order processSellOrder(Order order) throws AssetNotFoundException, NoEnoughAssetException {
        // Lock the Asset to Sell
        Asset asset = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(),
                order.getAssetName()
        ).orElseThrow(AssetNotFoundException::new);

        // If the amount of the asset is enough
        if (asset.getUsableSize() >= order.getSize()) {
            // Decrease the size of the asset
            asset.setUsableSize(asset.getUsableSize() - order.getSize());
            assetRepo.save(asset);
            order.setStatus(Status.PENDING);
            // Create the order
            return orderRepo.save(order);
        } else {
            throw new NoEnoughAssetException();
        }
    }

    @Transactional
    public List<Order> findAllBetween(Long customerId, Date startDate, Date endDate) {
        return orderRepo.findByCustomerIdAndCreateDateGreaterThanAndCreateDateLessThan(
                customerId, startDate, endDate
        );
    }

    @Transactional
    public Order cancelOrder(
            long orderId,
            long customerId) throws OrderNotFoundException,
            OrderStatusIsNotValidException,
            AssetNotFoundException, CustomerNotFoundException {
        // Lock the order to prevent conflicting updates like lost updates.
        Order order = orderRepo.findByIdForUpdate(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(order.getCustomerId() != customerId) {
            throw new CustomerNotFoundException();
        }

        // Check if the order is PENDING
        if(!Status.PENDING.equals(order.getStatus())) {
            throw new OrderStatusIsNotValidException();
        }

        return switch (order.getOrderSide()) {
            case BUY -> processCancelBuyOrder(order);
            case SELL -> processCancelSellOrder(order);
        };
    }

    private Order processCancelSellOrder(Order order) throws AssetNotFoundException {
        // If Sell, lock the asset to sell
        Asset asset = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(),
                order.getAssetName()
        ).orElseThrow(AssetNotFoundException::new);
        // If Sell, increment usableSize of TRY asset
        asset.setUsableSize(asset.getUsableSize() + order.getSize());
        assetRepo.save(asset);
        // Mark the order as CANCELLED.
        order.setStatus(Status.CANCELLED);
        // Save the order.
        return orderRepo.save(order);
    }

    private Order processCancelBuyOrder(Order order) throws AssetNotFoundException {
        // If Buy, lock the TRY asset
        Asset TRY_Asset = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(),
                AssetNames.TRY.name()
        ).orElseThrow(AssetNotFoundException::new);

        //  If Buy, increment usableSize of TRY asset
        double cost = order.getPrice() * order.getSize();

        TRY_Asset.setUsableSize(TRY_Asset.getUsableSize() + cost);
        assetRepo.save(TRY_Asset);
        // Mark the order as CANCELLED.
        order.setStatus(Status.CANCELLED);
        // Save the order.
        return orderRepo.save(order);
    }

    public List<Order> findAll() {
        return orderRepo.findAll();
    }

    public Optional<Order> findOrder(Long customerId, Long orderId) {
        return orderRepo.findById(orderId);
    }

    @Transactional
    public Order matchOrder(long orderId) throws OrderNotFoundException, AssetNotFoundException {
        // LOCK ORDER mechanism is used to prevent lost updates.
        // ORDER OF LOCKS:
        // 1. ORDER
        // 2. ASSET of TRY
        // 3. CUSTOMER
        // 4. BUY ASSET
        Order order = orderRepo.findByIdForUpdate(orderId).orElseThrow(
                OrderNotFoundException::new);

        return switch (order.getOrderSide()) {
            case BUY -> processMatchBuyOrder(order);
            case SELL -> processMatchSellOrder(order);
        };
    }

    private Order processMatchBuyOrder(Order order) throws AssetNotFoundException {
        // LOCK TRY ASSET
        Asset TRY_Asset = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(),
                AssetNames.TRY.name()
        ).orElseThrow(AssetNotFoundException::new);

        // Update TRY asset
        double cost = order.getSize() * order.getPrice();
        TRY_Asset.setSize(TRY_Asset.getSize() - cost);
        assetRepo.save(TRY_Asset);
        // Update the order
        updateOrCreateAssetToBuy(order);
        // Mark the order as MATCHED
        order.setStatus(Status.MATCHED);
        return orderRepo.save(order);
    }

    private void updateOrCreateAssetToBuy(Order order) {
        Optional<Asset> optAssetToBuy = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(), order.getAssetName()
        );
        if(optAssetToBuy.isPresent()) {
            // If the asset exists, update its size and usableSize
            Asset assetToBuy = optAssetToBuy.get();
            assetToBuy.setSize(assetToBuy.getSize() + order.getSize());
            assetToBuy.setUsableSize(assetToBuy.getUsableSize() + order.getSize());
            assetRepo.save(assetToBuy);
        } else {
            // If the asset does not exist, create it.
            Asset newAsset = Asset.builder()
                    .assetName(order.getAssetName())
                    .size(order.getSize())
                    .usableSize(order.getSize())
                    .customerId(order.getCustomerId())
                    .build();
            assetRepo.save(newAsset);
        }
    }

    private Order processMatchSellOrder(Order order) throws AssetNotFoundException {
        // LOCK TRY ASSET
        Asset TRY_Asset = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(),
                AssetNames.TRY.name()
        ).orElseThrow(AssetNotFoundException::new);

        // UPDATE TRY ASSET
        double cost = order.getSize() * order.getPrice();
        TRY_Asset.setSize(TRY_Asset.getSize() + cost);
        TRY_Asset.setUsableSize(TRY_Asset.getUsableSize() + cost);
        assetRepo.save(TRY_Asset);
        // UPDATE ORDER
        updateAssetToSell(order);
        // Mark the order as MATCHED
        order.setStatus(Status.MATCHED);
        return orderRepo.save(order);
    }

    private void updateAssetToSell(Order order) throws AssetNotFoundException {
        Optional<Asset> optAssetToSell = assetRepo.findByCustomerIdAndAssetName(
                order.getCustomerId(), order.getAssetName()
        );
        if(optAssetToSell.isPresent()) {
            // If the asset exists, update its size and usableSize
            Asset assetToSell = optAssetToSell.get();
            assetToSell.setSize(assetToSell.getSize() - order.getSize());
            assetRepo.save(assetToSell);
        } else {
            // IF there is inconsistent situation detected, throw exception
            System.err.println("Inconsistent situation is detected.There are no assets available for which there is a pending SELL order.For details, please check the logs. Order Id: "+ order.getId());
            throw new AssetMissingException();
        }
    }
}
