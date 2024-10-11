package com.example.brokage.asset.service;

import org.springframework.stereotype.Service;

import com.example.brokage.asset.model.Asset;
import com.example.brokage.asset.model.AssetDto;

@Service
public class AssetMapper {

    public Asset toAsset(AssetDto dto) {
        return Asset.builder()
                .assetName(dto.assetName())
                .size(dto.assetSize())
                .usableSize(dto.assetSize())
                .customerId(dto.customerId())
                .build();

    }

}
