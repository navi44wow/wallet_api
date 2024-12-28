package com.wallet.wallet_api.entities.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositWithdrawalDTO {

    private Long userId;

    private Long walletId;

    private BigDecimal amount;

    private String operationType;

}
