package com.wallet.wallet_api.entities.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDTO {

    private Long userId;

    private Long walletId;

    private Long receiverId;

    private Long receiverWalletId;

    private BigDecimal amount;



}