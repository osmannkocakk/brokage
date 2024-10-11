package com.example.brokage.asset.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.NOT_FOUND,
        code = HttpStatus.NOT_FOUND,
        reason = "The Asset is Missing")
public class AssetMissingException extends AssetNotFoundException {
}
