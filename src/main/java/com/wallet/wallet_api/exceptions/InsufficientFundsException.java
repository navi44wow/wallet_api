package com.wallet.wallet_api.exceptions;

public class InsufficientFundsException extends RuntimeException{


    public InsufficientFundsException(String message) {
        super(message);
    }


}
