package com.example.brokage.asset.service;

import com.example.brokage.asset.AssetRepo;
import com.example.brokage.asset.model.*;
import com.example.brokage.customer.service.CustomerNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepo assetRepo;

    public List<Asset> getAssetsOfCustomer(
            long customerId) throws CustomerNotFoundException {
        return assetRepo.findAllByCustomerId(customerId);
    }

    @Transactional
    public Double depositMoney(
            long customerId,
            DepositMoneyDto depositMoneyDto
    ) throws AssetNotFoundException {
        var depositAmount = depositMoneyDto.depositAmount();

        Asset asset = assetRepo.findByCustomerIdAndAssetName(
                customerId, AssetNames.TRY.name()
        ).orElseThrow(AssetNotFoundException::new);

        asset.setSize(asset.getSize() + depositAmount);
        asset.setUsableSize(asset.getUsableSize() + depositAmount);
        assetRepo.save(asset);
        return asset.getSize();
    }

    @Transactional
    public Double withdrawMoney(
            long customerId,
            WithdrawMoneyDto depositMoneyDto
    ) throws NoEnoughMoneyException,
            AssetNotFoundException {
        var amount = depositMoneyDto.withdrawAmount();

        Asset asset = assetRepo.findByCustomerIdAndAssetName(
                customerId, AssetNames.TRY.name()
        ).orElseThrow(AssetNotFoundException::new);

        if(asset.getUsableSize() >= amount) {
            asset.setSize(asset.getSize() - amount);
            asset.setUsableSize(asset.getUsableSize() - amount);
            assetRepo.save(asset);
            return asset.getSize();
        } else {
            throw new NoEnoughMoneyException();
        }
    }

    public List<Asset> getAllAssets() {
        return assetRepo.findAll();
    }

    public Asset create(Asset asset) {
        return assetRepo.save(asset);
    }

}
