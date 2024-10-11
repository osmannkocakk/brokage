package com.example.brokage.asset.service;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST,
        reason = "There is No Enough Asset")
public class NoEnoughAssetException extends Exception {

    public NoEnoughAssetException() {
        super("There is No Enough Asset");
    }
}


