package com.wallet.wallet_api.exceptions;

public class ResourceNotFoundException extends RuntimeException {


    public ResourceNotFoundException(String message) {
        super(message);
    }
}
