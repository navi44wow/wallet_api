package com.wallet.wallet_api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wallet.wallet_api.entities.enums.EntryOperationType;
import com.wallet.wallet_api.entities.enums.EntryType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private EntryType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type")
    private EntryOperationType operationType;

    private LocalDateTime date = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    @JsonIgnore
    private Wallet wallet;

    private String fromCurrency;

    private String toCurrency;

}
