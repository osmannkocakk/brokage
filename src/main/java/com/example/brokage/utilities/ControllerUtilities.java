package com.example.brokage.utilities;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.stream.Collectors;

public class ControllerUtilities {

    public static void logErrors(BindingResult bindingResult) {
        System.err.println(bindingResult
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("\n")));
    }

}
