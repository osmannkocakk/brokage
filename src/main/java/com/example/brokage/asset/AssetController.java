package com.example.brokage.asset;

import com.example.brokage.asset.model.Asset;
import com.example.brokage.asset.model.DepositMoneyDto;
import com.example.brokage.asset.model.WithdrawMoneyDto;
import com.example.brokage.asset.service.AssetNotFoundException;
import com.example.brokage.asset.service.AssetService;
import com.example.brokage.asset.service.NoEnoughMoneyException;
import com.example.brokage.customer.service.CustomerNotFoundException;
import com.example.brokage.utilities.ControllerUtilities;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brokage/api/v1/asset")
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Asset>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @GetMapping("/{customerId}")
    public ResponseEntity<List<Asset>> getAssetsOfCustomer(
            @PathVariable("customerId")
            long customerId
    ) throws CustomerNotFoundException {
        return ResponseEntity.ok(assetService.getAssetsOfCustomer(customerId));
    }

    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id ")
    @PostMapping("/deposit/{customerId}")
    public ResponseEntity<Double> depositMoney(
            @PathVariable("customerId")
            long customerId,

            @Valid @RequestBody
            DepositMoneyDto depositMoneyDto,

            BindingResult bindingResult
    ) throws AssetNotFoundException {
        if(bindingResult.hasErrors()) {
            ControllerUtilities.logErrors(bindingResult);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(
                    assetService.depositMoney(customerId, depositMoneyDto)
            );
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @PostMapping("/withdraw/{customerId}")
    public ResponseEntity<Double> withdrawMoney(
            @PathVariable("customerId")
            long customerId,

            @Valid @RequestBody
            WithdrawMoneyDto depositMoneyDto,

            BindingResult bindingResult
    ) throws NoEnoughMoneyException, AssetNotFoundException {
        if(bindingResult.hasErrors()) {
            ControllerUtilities.logErrors(bindingResult);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(
                    assetService.withdrawMoney(customerId, depositMoneyDto));
        }
    }



}
