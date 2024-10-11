package com.example.brokage.asset.service;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.NOT_FOUND,
        code = HttpStatus.NOT_FOUND,
        reason = "There is no Asset")
public class AssetNotFoundException extends Exception {

}
