package com.example.brokage.asset.model;

public enum AssetNames {

    TRY("TRY");

    private final String name;

    AssetNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
