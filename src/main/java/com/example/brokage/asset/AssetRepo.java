package com.example.brokage.asset;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.brokage.asset.model.Asset;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepo extends JpaRepository<Asset, Long> {

    @Transactional
    List<Asset> findAllByCustomerId(long customerId);

    //Pessimistic write lock to prevent lost updates
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ast FROM Asset ast WHERE ast.customerId = :customerId and ast.assetName = :name")
    Optional<Asset> findByCustomerIdAndAssetName(long customerId, String name);
}
