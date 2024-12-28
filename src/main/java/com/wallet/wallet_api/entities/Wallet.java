package com.wallet.wallet_api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wallet.wallet_api.entities.enums.CurrencyCode;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;

    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;


    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Entry> entries = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private CurrencyCode currency;

}
